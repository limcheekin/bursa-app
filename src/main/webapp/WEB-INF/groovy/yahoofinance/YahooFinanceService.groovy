package yahoofinance

import com.fasterxml.jackson.databind.ObjectMapper

import au.com.bytecode.opencsv.CSVReader

/**
 * IMPORTANT: PLEASE READ
 * I do not guarantee the reliability or availability of the Yahoo web services
 *
 * I am not qualified, licensed, approved or authorized to make any investment recommendations or give any such advice.
 * All data and information on this site is based on data from sources that are not under my control.
 * I cannot guarantee the accuracy of any of the data, in fact I know of some significant data problems such as,
 * but not limited to, incorrect stock splits that lead to radically incorrect prices.
 * No investing decision should be taken based on the information on this site alone,
 * and none of the information here is a recommendation to buy or sell any security!!
 * Do the appropriate further analysis and due diligence before investing.
 * I make no guarantees whatsoever about future returns using the methods.
 */

/**
 * Yahoo Finance grails plugin.
 * Service class to use to make web services call to Yahoo YQL
 * @author ibarra.alfonso@gmail.com
 * (c) 2013 Ibarra Alfonso
 * This software may be freely redistributed under the terms of the GNU
 * public license version 2.
 * This package is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the "GNU General Public License" for more detail.
 *
 */
class YahooFinanceService {


	/**
	 * List of stock statistical data available
	 */
	final static List availableStats = [ "Ask", "AverageDailyVolume", "AskSize", "Bid", "Ask(Realtime)", "Bid(Realtime)", "BookValue", "BidSize",
		"Change&PercentChange", "Change", "Commission", "Change(Realtime)", "AfterHoursChange(Realtime)", "Dividend/Share",
		"LastTradeDate", "TradeDate", "Earnings/Share", "ErrorIndication", "EPSEstimateCurrentYear", "EPSEstimateNextYear",
		"EPSEstimateNextQuarter", "FloatShares", "DaysLow", "DaysHigh", "52WeekLow", "52WeekHigh",
		"HoldingsGainPercent", "AnnualizedGain", "HoldingsGain", "HoldingsGainPercent(Realtime)", "HoldingsGain(Realtime)",
		"MoreInfo", "OrderBook(Realtime)", "MarketCapitalization", "MarketCapitalization(Realtime)", "EBITDA", "ChangeFrom52WeekLow",
		"PercentChangeFrom52WeekLow", "LastTrade(Realtime)WithTime", "ChangePercent(Realtime)", "LastTradeSize", "ChangeFrom52WeekHigh",
		"PercentChangeFrom52WeekHigh", "LastTrade(WithTime)", "LastTrade", "HighLimit",
		"LowLimit", "DaysRange", "DaysRange(Realtime)", "50DayMovingAverage", "200DayMovingAverage", "ChangeFrom200DayMovingAverage",
		"PercentChangeFrom200DayMovingAverage", "ChangeFrom50DayMovingAverage", "PercentChangeFrom50DayMovingAverage", "Name",
		"Notes", "Open", "PreviousClose", "PricePaid", "ChangeInPercent", "Price/Sales", "Price/Book", "ExDividendDate",
		"PERatio", "DividendPayDate", "PERatio(Realtime)", "PEGRatio", "Price/EPSEstimateCurrentYear", "Price/EPSEstimateNextYear",
		"Symbol", "SharesOwned", "ShortRatio", "LastTradeTime", "TradeLinks", "TickerTrend", "1YrTargetPrice", "Volume",
		"HoldingsValue", "HoldingsValue(Realtime)", "52WeekRange", "DaysValueChange", "DaysValueChange(Realtime)", "StockExchange",
		"DividendYield"]
	/**
	 * Test method to make call for quotes of APPL and YHOO
	 * @return response
	 */
    def testYahooQuote() {
		def stockSymbols = "AAPL+YHOO"
		def rval = "ERROR"
		//def urlString = "http://query.yahooapis.com/v1/public/yql?q=select * from yahoo.finance.quotes where symbol in (\"AAPL\")&env=store://datatables.org/alltableswithkeys&format=json"
		def queryString = "select * from yahoo.finance.quotes where symbol in (\"AAPL\")&env=store://datatables.org/alltableswithkeys&format=json"
		def urlStr = "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20in%20%28%22${stockSymbols}%22%29&env=store://datatables.org/alltableswithkeys&format=json"
		def uri = URLEncoder.encode(urlStr, "UTF-8")
		log.debug "ENCODED: ${uri}"
		def url = new URL(urlStr)
		def connection = url.openConnection()
		connection.setRequestProperty("Method","GET")
		connection.setRequestProperty("Content-Type" ,"application/xml" )
		connection.doOutput = true
		connection.connect()
		def resu = connection.content.text
		log.debug "GOT REQUEST: ${resu}"
		return resu
	}

	/**
	 * Called by yahoo finance taglib. In the view, this is
	 * invoked using <yahoofinance:quote symbol="YHOO" stat="lastTrade" />
	 * @param symbol company stock symbol
	 * @param stat name of statistic to retrieve
	 * @return value of statistic passed in the parameter
	 */
	def taglibQuote(String symbol, String stat) {
		def resultList = quote(symbol, stat)[0]
		return resultList[stat]
	}

	/**
	 * Returns a list of stock standard key statistics information. Uses the Yahoo CSV web-service.
	 * @param symbols string of stock symbol/s (ex. TGT,WMT,AAPL)
	 * @return list of data for requested stock/s.
	 */
	List quote(String symbols) {
		symbols = convertCommasToPlus(symbols)
		def standardTags = getStandardTags()
		def tagKeys = getStandardTagKeys()
		def url = getCSVQuoteURL(symbols, standardTags)
		def quoteReply = executeYQLCSV(url)
		return mapQuote(quoteReply, tagKeys)
	}

	/**
	 * Returns a list of stock data depending on specific statistics requested. Uses the Yahoo CSV web-service.
	 * @param symbols string of stock symbol/s (ex. TGT,WMT,AAPL)
	 * @param specificData string of specific data to request (ex. name, symbol, lastTrade)
	 * @return
	 */
	List quote(String symbols, String specificData) {
		specificData = specificData.replaceAll(/\s/, "")
		symbols = convertCommasToPlus(symbols)
		def reqTags = getTags(specificData)
		def tagKeys = specificData.split(",") as List
		def url = getCSVQuoteURL(symbols, reqTags)
		def quoteReply = executeYQLCSV(url)
		return mapQuote(quoteReply, tagKeys)
	}

	/**
	 * Retrieves stock data of company symbols passed using Yahoo YQL. Uses the well documented Yahoo YQL
	 * web services. Unfortunately the Yahoo YQL web service is not highly reliable. Frequent 404 errors are seen.
	 * @param symbols company stock symbols (ex. "YHOO, GOOG")
	 * @param keystats name of statistic to retrieve
	 * @return
	 */
	def getQuotes(String symbols) {
		symbols = convertCommasToPlus(symbols)
		def url = getQuoteURL(symbols)
		def quoteReply = executeYQL(url)
		def mapper = new ObjectMapper()
		StockQuote yqlRequest = mapper.readValue(quoteReply, StockQuote.class)
		return yqlRequest.getAllStockData()
	}

	/**
	 * Retrieves stock data of company symbols passed using Yahoo YQL. Uses the well documented Yahoo YQL
	 * web services. Unfortunately the Yahoo YQL web service is not highly reliable. Frequent 404 errors are seen.
	 * @param symbols company stock symbols (ex. "YHOO, GOOG")
	 * @param keystats name of statistic to retrieve
	 * @return
	 */
	List getQuotes(String symbols, String keystats) {
		def spacePattern = /\s/
		keystats = keystats.replaceAll(spacePattern, "")
		def keyStatList = keystats.split(",")
		List returnData = []
		symbols = convertCommasToPlus(symbols)
		def url = getQuoteURL(symbols)
		def quoteReply = executeYQL(url)
		def mapper = new ObjectMapper()
		StockQuote yqlRequest = mapper.readValue(quoteReply, StockQuote.class)
		def quotes = yqlRequest.getQuotes()
		if (quotes) {
			quotes.each {
				def quote = it
				def stockMap = [:]
				stockMap['symbol'] = quote['symbol']
				keyStatList.each {
					stockMap[it] = quote[it]
				}
				returnData.add(stockMap)
			}
		}
		return returnData
	}

	/**
	 * Returns a list of historical stock statistics within the past number of days specified. List of statistics are <br/>
	 * [Date, Open, High, Low, Close, Volume, Adj Close]
	 * @param symbol Company stock symbol (ex. YHOO)
	 * @param numberOfDays number of past days from current date.
	 * @return List of statistics within the past number of days
	 */
	List getHistoricalQuotes(String symbol, int numberOfDays) {
		def today = new Date()
		def beforeDate = today - numberOfDays
		return getHistoricalQuotes(symbol, beforeDate, today)
	}

	/**
	 * Returns list of historical stock statistics between the specified dates. List stock data are <br/>
	 * [Date, Open, High, Low, Close, Volume, Adj Close]
	 * @param symbol company stock symbol to look up
	 * @param startDate starting date string (mm/dd/yyyy)
	 * @param endDate ending date string (mm/dd/yyyy)
	 * @return List of historical data
	 */
	List getHistoricalQuotes(String symbol, String startDate, String endDate) {
		def datePattern = /[\d]+[\/|-][\d]+[\/|-][\d]+/
		if ( !(startDate ==~ datePattern) || !(endDate ==~ datePattern) ) {
			return ["ERROR: Date Strings must be in the format mm/dd/yyyy or mm-dd-yyyy"]
		}
		def sdateList
		def edateList
		if (startDate.find(/-/)) {
			sdateList = startDate.split("\\-")
		} else {
			sdateList = startDate.split("\\/")
		}
		if (endDate.find(/-/)) {
			edateList = endDate.split("\\-")
		} else {
			edateList = endDate.split("\\/")
		}
		Date sDate = new GregorianCalendar(sdateList[2].toInteger(), sdateList[0].toInteger() - 1, sdateList[1].toInteger()).getTime()
		Date eDate = new GregorianCalendar(edateList[2].toInteger(), edateList[0].toInteger() - 1, edateList[1].toInteger()).getTime()
		return getHistoricalQuotes(symbol, sDate, eDate)
	}

	/**
	 * Returns list of historical stock data between the specified dates. List stock data are <br/>
	 * [Date, Open, High, Low, Close, Volume, Adj Close]
	 * @param symbol company stock symbol to look up
	 * @param startDate Date object with starting date
	 * @param endDate Date object with ending date
	 * @return list of historical data
	 */
	List getHistoricalQuotes(String symbol, Date startDate, Date endDate) {
		if (startDate > endDate) {
			endDate = startDate
		}
		def url = getHistoricalDataCSVURL(symbol, startDate, endDate)
		return executeYQLCSV(url)
	}

	/**
	 * Returns integer part of the date to extract depending on parameter passed.
	 * @param d Date object to extract DAY, MONTH, YEAR, ...
	 * @param part Specific part to extract (DATE, DAY, MONTH, YEAR, HOUR, MINUTE, DAY_OF_WEEK, DAY_OF_WEEK_IN_MONTH)
	 * @return integer value of extracted part of the date
	 */
	def extractDateField(Date d, String part) {
		def monthStart = 1
		switch(part) {
			case 'DATE':
			case 'DAY':
				return d.getAt(Calendar.DATE)
			case 'MONTH':
				def monthPart = d.getAt(Calendar.MONTH)
				if (monthStart == 0) {
					monthPart = monthPart - 1
				}
				return monthPart
			case 'YEAR':
				return d.getAt(Calendar.YEAR)
			case 'HOUR':
				return d.getAt(Calendar.HOUR)
			case 'MINUTE':
				return d.getAt(Calendar.MINUTE)
			case 'DAY_OF_WEEK':
				return d.getAt(Calendar.DAY_OF_WEEK)
			case 'DAY_OF_WEEK_IN_MONTH':
				return d.getAt(Calendar.DAY_OF_WEEK_IN_MONTH)
		}
	}

	/**
	 * Converts commas in the string to '+' symbol. This is the format needed when making web-service calls using YQL.
	 * Also removes any spaces in the string to format correctly.
	 * @param symbolString string with multiple stock symbols separated by commas.
	 * @return
	 */
	def convertCommasToPlus(String symbolString) {
		def spacePattern = /\s/
		symbolString = symbolString.replaceAll(spacePattern, "")
		def pattern = /,/
		return symbolString.replaceAll(pattern, "+")
	}

	/**
	 * Executes the URL to make the web service call. Used with getQuoteURL()
	 * @param urlToRun String of URL to call
	 * @return
	 */
	def executeYQL(String urlToRun) {
		def url = new URL(urlToRun)
		def connection = url.openConnection()
		connection.setRequestProperty("Method","GET")
		connection.setRequestProperty("Content-Type" ,"application/xml" )
		connection.doOutput = true
		connection.connect()
		def result = '{"query":{"count":0,"created":"1970-01-01T00:00:00Z","lang":"en-US","results":{"quote":[{}]}}}'
		if (connection.responseCode == 200 || connection.responseCode == 201) {
			result = connection.content.text
			result = result.replace('"quote":{"symbol"', '"quote":[{"symbol"')
			result = result.replace('}}}}', '}]}}}')
		}
		return result
	}

	/**
	 * Makes the web-service call to Yahoo Finance for historical stock quotes in CSV format. Uses
	 * CSVReader to get response.
	 * @param urlToRun URL of yahoo finance web-service
	 * @return List of CSV data
	 */
	List executeYQLCSV(urlToRun) {
		def url = new URL(urlToRun)
		def connection = url.openConnection()
		connection.setRequestProperty("Method","GET")
		connection.setRequestProperty("Content-Type" ,"application/xml" )
		connection.doOutput = true
		//connection.inputStream
		def cvsInputStream = connection.inputStream
		CSVReader csvReader = new CSVReader(new InputStreamReader(cvsInputStream))
		String [] nextLine
		def csvlist = []
		while ((nextLine = csvReader.readNext()) != null) {
			csvlist.add(nextLine)
		}
		return csvlist
	}

	/**
	 * Returns the URL needed to make a yahoo finance web service call to get historical stock data in CSV form.
	 * @param stockSymbol company stock symbol
	 * @param startDate Date object with starting date
	 * @param endDate Date object with ending date
	 * @return return URL to call
	 */
	String getHistoricalDataCSVURL(String stockSymbol, Date startDate, Date endDate) {
		def sday = extractDateField(startDate, "DAY")
		def smonth = extractDateField(startDate, "MONTH")
		def syear = extractDateField(startDate, "YEAR")
		def eday = extractDateField(endDate, "DAY")
		def emonth = extractDateField(endDate, "MONTH")
		def eyear = extractDateField(endDate, "YEAR")
		def stockHistoricalYQL="http://itable.finance.yahoo.com/table.csv?s=${stockSymbol}&g=d&a=${smonth}&b=${sday}&c=${syear}&d=${emonth}&e=${eday}&f=${eyear}"
		return stockHistoricalYQL
	}

	/**
	 * Currently not used. Returns the URL needed to get news articles related to the stocks.<br/>
	 * NOTE: Probably can use java.net.URLEncoder.encode to make the URL more readable.
	 * @param stockSymbols String of stock symbols to fetch news
	 * @return URL of yahoo web service to fetch news related to stocks passed.
	 */
	String getNewsURL(String stockSymbols) {
		return "http://query.yahooapis.com/v1/public/yql?q=select%20href%20from%20html%20where%20url%3D%22http%3A%2F%2Ffinance.yahoo.com%2Fq%3Fs%3D${stockSymbols}%22%20and%20xpath%3D'%2F%2Fdiv%5B%40id%3D%22yfi_headlines%22%5D%2Fdiv%5B2%5D%2Ful%2Fli%2Fa'&format=json&callback=cbfunc"
	}

	/**
	 * Currently not used because yahoo seems to block this after several calls.
	 * @param stockSymbol
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	def getHistoricalDataURL(stockSymbol, startDate, endDate) {
		return "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.historicaldata%20where%20symbol%3D%22${stockSymbol}%22%20and%20startDate%3D%22${startDate}%22%20and%20endDate%3D%22${endDate}%22&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=cbfunc"
	}

	/**
	 * Returns String URL for calling yahoo web-service. <br/>
	 * Ex. getQuoteURL("YHOO+AAPL+MSFT")
	 * @param stockSymbols stock symbols separated by '+'
	 * @return URL string to call YQL in JSON
	 */
	def getQuoteURL(stockSymbols) {
		return "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20in%20%28%22${stockSymbols}%22%29&env=store://datatables.org/alltableswithkeys&format=json"
	}

	/**
	 * Returns String URL for calling yahoo finance. URL returns a CSV <br/>
	 * Ex. getQuoteURL("YHOO+AAPL+MSFT", "snl1yr") - Stock symbols are separated by '+', data info requested are specified by the yahoo finance special tags
	 *    for example 's' is for symbol or 'y' returns the yield, see tagMap for more equivalent symbols.
	 *
	 * @param stockSymbols stock symbols separated by '+'
	 * @param dataTags tags corresponding to data requested
	 * @return URL string to call YQL in JSON
	 */
	String getCSVQuoteURL(String stockSymbols, String dataTags) {
		return " http://finance.yahoo.com/d/quotes.csv?s=${stockSymbols}&f=${dataTags}"
	}

	/**
	 * Transposes two lists and creates a list of mapped values.
	 * @param responseList List of CSV values
	 * @param keyList List of keys
	 * @return Returns a list of transposed values
	 */
	List mapQuote(List responseList, List keyList) {
		def rval = []
		responseList.each {
			def singleData = it
			def transposed = [keyList,singleData].transpose().collectEntries { it }
			rval.add(transposed)
		}
		return rval
	}

	/**
	 * Returns list of stock statistical keys. Used if user does not specify any specific statistic to get.
	 * @return List of standart statistics keys.
	 */
	List getStandardTagKeys() {
		return stdMap.keySet() as List
	}

	/**
	 * Returns the equivalent tags for statistics keys. This is specified by the yahoo web service.
	 * @param requestedTags String of statictics separated by commas
	 * @return String containing tags for the statistical info.
	 */
	String getTags(String requestedTags) {
		def resultTag = ""
		requestedTags= requestedTags.replaceAll(/\s/, "")
		def rtags = requestedTags.toLowerCase().split(",")
		rtags.each { key ->
			def lowercaseKey = key.toLowerCase()
			if (tagMap[lowercaseKey]) {
				resultTag += tagMap[lowercaseKey]
			}
		}
		return resultTag
	}

	/**
	 * Gets all the tags for standard statistical stock data.
	 * @return String of standard statistical tags.
	 */
	String getStandardTags() {
		return stdMap.values().join()
	}

	/**
	 * Map of standard stock statistical data and equivalent tag. <br/>
	 * The lookup is not case sensitive (see method getTags() ).
	 * NOTE: Not all of these statistics are available from the yahoo web-service and
	 * simply returns empty or NA.
	 */
	final static Map stdMap = [ "symbol": 's',
		"name" : 'n',
		"lastTrade" : 'l1',
		"date" : 'd1',
		"time" : 't1',
		"change" : 'c' ,
		"changePoints" : 'c1' ,
		"changePercent" : 'p2',
		"previousClose" : 'p',
		"open" : 'o',
		"dayHigh" : 'h',
		"dayLow" : 'g',
		"volume" : 'v',
		"dayRange" : 'm',
		"lastTradeWithTime" : 'l',
		"tickerTrend" : 't7',
		"averageDailyVolume" : 'a2',
		"bid" : 'b',
		"ask" : 'a']

	/**
	 * Map of ALL stock statistical data available from the Yahoo web service. <br/>
	 * The lookup is not case sensitive (see method getTags() ).
	 */
	final static Map tagMap = [ "ask": 'a',
		"averagedailyvolume": 'a2',
		"asksize": 'a5',
		"bid": 'b',
		"ask(realtime)": 'b2',
		"bid(realtime)": 'b3',
		"bookvalue": 'b4',
		"bidsize": 'b6',
		"change&percentchange": 'c',
		"change": 'c1',
		"commission": 'c3',
		"change(realtime)": 'c6',
		"afterhourschange(realtime)": 'c8',
		"dividend/share": 'd',
		"lasttradedate": 'd1',
		"tradedate": 'd2',
		"earnings/share": 'e',
		"errorindication": 'e1',
		"epsestimatecurrentyear": 'e7',
		"epsestimatenextyear": 'e8',
		"epsestimatenextquarter": 'e9',
		"floatshares": 'f6',
		"dayslow": 'g',
		"dayshigh": 'h',
		"52weeklow": 'j',
		"52weekhigh": 'k',
		"holdingsgainpercent": 'g1',
		"annualizedgain": 'g3',
		"holdingsgain": 'g4',
		"Holdingsgainpercent(realtime)": 'g5',
		"Holdingsgain(realtime)": 'g6',
		"moreinfo": 'i',
		"orderbook(realtime)": 'i5',
		"marketcapitalization": 'j1',
		"marketcapitalization(realtime)": 'j3',
		"ebitda": 'j4',
		"changefrom52weeklow": 'j5',
		"percentchangefrom52weeklow": 'j6',
		"lasttrade(realtime)withtime": 'k1',
		"changepercent(realtime)": 'k2',
		"lasttradesize": 'k3',
		"changefrom52weekhigh": 'k4',
		"percentchangefrom52weekhigh": 'k5',
		"lasttrade(withtime)": 'l',
		"lasttrade": 'l1',
		"highlimit": 'l2',
		"lowlimit": 'l3',
		"daysrange": 'm',
		"daysrange(realtime)": 'm2',
		"50daymovingaverage": 'm3',
		"200daymovingaverage": 'm4',
		"changefrom200daymovingaverage": 'm5',
		"percentchangefrom200daymovingaverage": 'm6',
		"changefrom50daymovingaverage": 'm7',
		"percentchangefrom50daymovingaverage": 'm8',
		"name": 'n',
		"notes": 'n4',
		"open": 'o',
		"previousclose": 'p',
		"pricepaid": 'p1',
		"changeinpercent": 'p2',
		"price/sales": 'p5',
		"price/book": 'p6',
		"exdividenddate": 'q',
		"peratio": 'r',
		"dividendpaydate": 'r1',
		"peratio(realtime)": 'r2',
		"pegratio": 'r5',
		"price/epsestimatecurrentyear": 'r6',
		"price/epsestimatenextyear": 'r7',
		"symbol": 's',
		"sharesowned": 's1',
		"shortratio": 's7',
		"lasttradetime": 't1',
		"tradelinks": 't6',
		"tickertrend": 't7',
		"1yrtargetprice": 't8',
		"volume": 'v',
		"holdingsvalue": 'v1',
		"holdingsvalue(realtime)": 'w7',
		"52weekrange": 'w',
		"daysvaluechange": 'w1',
		"daysvaluechange(realtime)": 'w4',
		"stockexchange": 'x',
		"dividendyield": 'y']



	/* NOT USED AT THE MOMENT
	def extendedMap = ["s" : [ "symbol", "val" ],
		"n" : [ "name", "val" ],
		"w" : [ "weeks52Range", "val" ],
		"j5" : [ "weeks52ChangeFromLow", "val.to_f" ],
		"j6" : [ "weeks52ChangePercentFromLow", "val" ],
		"k4" : [ "weeks52ChangeFromHigh", "val.to_f" ],
		"k5" : [ "weeks52ChangePercentFromHigh", "val" ],
		"e" : [ "earningsPerShare", "val.to_f" ],
		"r" : [ "peRatio", "val.to_f" ],
		"s7" : [ "shortRatio", "val" ],
		"r1" : [ "dividendPayDate", "val" ],
		"q" : [ "exDividendDate", "val" ],
		"d" : [ "dividendPerShare", "convert(val)" ],
		"y" : [ "dividendYield", "convert(val)" ],
		"j1" : [ "marketCap", "convert(val)" ],
		"t8" : [ "oneYearTargetPrice", "val" ],
		"e7" : [ "epsEstimateCurrentYear", "val" ],
		"e8" : [ "epsEstimateNextYear", "val" ],
		"e9" : [ "epsEstimateNextQuarter", "val" ],
		"r6" : [ "pricePerEPSEstimateCurrentYear", "val" ],
		"r7" : [ "pricePerEPSEstimateNextYear", "val" ],
		"r5" : [ "pegRatio", "val.to_f" ],
		"b4" : [ "bookValue", "val.to_f" ],
		"p6" : [ "pricePerBook", "val.to_f" ],
		"p5" : [ "pricePerSales", "val.to_f" ],
		"j4" : [ "ebitda", "val" ],
		"m3" : [ "movingAve50days", "val" ],
		"m7" : [ "movingAve50daysChangeFrom", "val" ],
		"m8" : [ "movingAve50daysChangePercentFrom", "val" ],
		"m4" : [ "movingAve200days", "val" ],
		"m5" : [ "movingAve200daysChangeFrom", "val" ],
		"m6" : [ "movingAve200daysChangePercentFrom", "val" ],
		"s1" : [ "sharesOwned", "val" ],
		"p1" : [ "pricePaid", "val" ],
		"c3" : [ "commission", "val" ],
		"v1" : [ "holdingsValue", "val" ],
		"w1" : [ "dayValueChange", "val" ],
		"g1" : [ "holdingsGainPercent", "val" ],
		"g4" : [ "holdingsGain", "val" ],
		"d2" : [ "tradeDate", "val" ],
		"g3" : [ "annualizedGain", "val" ],
		"l2" : [ "highLimit", "val" ],
		"l3" : [ "lowLimit", "val" ],
		"n4" : [ "notes", "val" ],
		"x" : [ "stockExchange", "val" ]]
		*/
}
