package yahoofinance

/**
 * Used to get specific stock data.
 * @author ialfonso
 */
class StockQuote{

	Query query

	/**
	 * Gets a List of Mapped stock data.
	 * @return List of Map containing stock data
	 */
	List getQuotes() {
		if (query) {
			Results results =  query.getResults()
			if (results) {
				return query.getResults().getQuote()
			}
		}
		return null
	}

	Results getResults() {
		return query?.getResults()
	}

	/**
	 * Returns the Last Trade Price
	 * @param stockSymbol stock symbol (Ex. YHOO)
	 * @return stock last trade price
	 */
	String getLastTrade(String stockSymbol) {
		def returnVal = ""
		def stock_result = query.getResults().getQuote()
		def quote = query.getResults().findStockData(stockSymbol)
		if (quote) {
			returnVal = quote['LastTradePriceOnly']
		}
		return returnVal
	}

	/**
	 * Returns the specific stock statistic passed in the parameter. <br/>
	 * Ex. getValue("YHOO", "volume")
	 * @param stockSymbol String stock symbol
	 * @param whichStat String statistic
	 * @return value of statistic requested in String
	 */
	String getValue(String stockSymbol, String whichStat) {
		def returnVal = ""
		def results = getResults()
		if (results) {
			def quote = results.findStockData(stockSymbol)
			if (quote) {
				returnVal = quote[whichStat]
			}
		}
		return returnVal
	}

	/**
	 * Retrieves all data of a specific stock
	 * @param stockSymbol company stock symbol
	 * @return
	 */
	String getAllStockData() {
		return getResults()?.getQuote()
	}

	String toString() {
		def stock_result = query.getResults().getQuote()
		return "com.ibaralf.StockQuote ${stock_result.size()}"
	}
}
