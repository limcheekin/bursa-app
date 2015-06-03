package yahoofinance

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Contains different stock data. Uses jackson @Json annotation.
 * @author ialfonso
 */
@JsonIgnoreProperties(["Ask"])
class Quote{
	@JsonProperty("AfterHoursChangeRealtime")
	String afterHoursChangeRealtime
	@JsonProperty("AnnualizedGain")
	String annualizedGain
	@JsonProperty("Ask")
	String ask
	@JsonProperty("AskRealtime")
	String askRealtime
	@JsonProperty("AverageDailyVolume")
	String averageDailyVolume
	@JsonProperty("Bid")
	String bid
	@JsonProperty("BidRealtime")
	String bidRealtime
	@JsonProperty("BookValue")
	String bookValue
	@JsonProperty("Change")
	String change
	@JsonProperty("ChangeFromFiftydayMovingAverage")
	String changeFromFiftydayMovingAverage
	@JsonProperty("ChangeFromTwoHundreddayMovingAverage")
	String changeFromTwoHundreddayMovingAverage
	@JsonProperty("ChangeFromYearHigh")
	String changeFromYearHigh
	@JsonProperty("ChangeFromYearLow")
	String changeFromYearLow
	@JsonProperty("ChangePercentRealtime")
	String changePercentRealtime
	@JsonProperty("ChangeRealtime")
	String changeRealtime
	@JsonProperty("Change_PercentChange")
	String change_PercentChange
	@JsonProperty("ChangeinPercent")
	String changeinPercent
	@JsonProperty("Commission")
	String commission
	@JsonProperty("DaysHigh")
	String daysHigh
	@JsonProperty("DaysLow")
	String daysLow
	@JsonProperty("DaysRange")
	String daysRange
	@JsonProperty("DaysRangeRealtime")
	String daysRangeRealtime
	@JsonProperty("DaysValueChange")
	String daysValueChange
	@JsonProperty("DaysValueChangeRealtime")
	String daysValueChangeRealtime
	@JsonProperty("DividendPayDate")
	String dividendPayDate
	@JsonProperty("DividendShare")
	String dividendShare
	@JsonProperty("DividendYield")
	String dividendYield
	@JsonProperty("EBITDA")
	String eBITDA
	@JsonProperty("EPSEstimateCurrentYear")
	String ePSEstimateCurrentYear
	@JsonProperty("EPSEstimateNextQuarter")
	String ePSEstimateNextQuarter
	@JsonProperty("EPSEstimateNextYear")
	String ePSEstimateNextYear
	@JsonProperty("EarningsShare")
	String earningsShare
	@JsonProperty("ErrorIndicationreturnedforsymbolchangedinvalid")
	String errorIndicationreturnedforsymbolchangedinvalid
	@JsonProperty("ExDividendDate")
	String exDividendDate
	@JsonProperty("FiftydayMovingAverage")
	String fiftydayMovingAverage
	@JsonProperty("HighLimit")
	String highLimit
	@JsonProperty("HoldingsGain")
	String holdingsGain
	@JsonProperty("HoldingsGainPercent")
	String holdingsGainPercent
	@JsonProperty("HoldingsGainPercentRealtime")
	String holdingsGainPercentRealtime
	@JsonProperty("HoldingsGainRealtime")
	String holdingsGainRealtime
	@JsonProperty("HoldingsValue")
	String holdingsValue
	@JsonProperty("HoldingsValueRealtime")
	String holdingsValueRealtime
	@JsonProperty("LastTradeDate")
	String lastTradeDate
	@JsonProperty("LastTradePriceOnly")
	String lastTradePriceOnly
	@JsonProperty("LastTradeRealtimeWithTime")
	String lastTradeRealtimeWithTime
	@JsonProperty("LastTradeTime")
	String lastTradeTime
	@JsonProperty("LastTradeWithTime")
	String lastTradeWithTime
	@JsonProperty("LowLimit")
	String lowLimit
	@JsonProperty("MarketCapRealtime")
	String marketCapRealtime
	@JsonProperty("MarketCapitalization")
	String marketCapitalization
	@JsonProperty("MoreInfo")
	String moreInfo
	@JsonProperty("Name")
	String name
	@JsonProperty("Notes")
	String notes
	@JsonProperty("OneyrTargetPrice")
	String oneyrTargetPrice
	@JsonProperty("Open")
	String open
	@JsonProperty("OrderBookRealtime")
	String orderBookRealtime
	@JsonProperty("PEGRatio")
	String pEGRatio
	@JsonProperty("PERatio")
	String pERatio
	@JsonProperty("PERatioRealtime")
	String pERatioRealtime
	@JsonProperty("PercebtChangeFromYearHigh")
	String percebtChangeFromYearHigh
	@JsonProperty("PercentChange")
	String percentChange
	@JsonProperty("PercentChangeFromFiftydayMovingAverage")
	String percentChangeFromFiftydayMovingAverage
	@JsonProperty("PercentChangeFromTwoHundreddayMovingAverage")
	String percentChangeFromTwoHundreddayMovingAverage
	@JsonProperty("PercentChangeFromYearLow")
	String percentChangeFromYearLow
	@JsonProperty("PreviousClose")
	String previousClose
	@JsonProperty("PriceBook")
	String priceBook
	@JsonProperty("PriceEPSEstimateCurrentYear")
	String priceEPSEstimateCurrentYear
	@JsonProperty("PriceEPSEstimateNextYear")
	String priceEPSEstimateNextYear
	@JsonProperty("PricePaid")
	String pricePaid
	@JsonProperty("PriceSales")
	String priceSales
	@JsonProperty("SharesOwned")
	String sharesOwned
	@JsonProperty("ShortRatio")
	String shortRatio
	@JsonProperty("StockExchange")
	String stockExchange
	@JsonProperty("symbol")
	String symbol
	@JsonProperty("TickerTrend")
	String tickerTrend
	@JsonProperty("TradeDate")
	String tradeDate
	@JsonProperty("TwoHundreddayMovingAverage")
	String twoHundreddayMovingAverage
	@JsonProperty("Volume")
	String volume
	@JsonProperty("YearHigh")
	String yearHigh
	@JsonProperty("YearLow")
	String yearLow
	@JsonProperty("YearRange")
	String yearRange
	@JsonProperty("Symbol")
	String sSymbol

	String toString() { symbol }
}
