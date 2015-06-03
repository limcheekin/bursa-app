package yahoofinance

/**
 * Used by Query.groovy class. Used by StockQuote to get a specific stock statistical data.
 * @author ialfonso
 */
class Results{

	List quote

	/**
	 * Gets the specific stock list of statistics from a Map
	 * @param stockSymbol
	 * @return Map of stock data
	 */
	def findStockData(String stockSymbol) {
		for(Map m : quote) {
			if (m.symbol == stockSymbol) {
				return m
			}
		}
	}
}
