import org.htmlparser.Parser
import org.htmlparser.filters.TagNameFilter
import org.htmlparser.util.NodeList
import static KlseScreenerColumn.*


    final String indexName = params.indexName
    List components = params.components.split(',')

    // Ref URL: http://www.kellyrob99.com/blog/2013/02/10/groovy-and-http/
    HttpURLConnection connection = Constants.KLSE_SCREENER_URL.toURL().openConnection()
    connection.doOutput = true
    connection.requestMethod = "POST"
    connection.outputStream.withWriter { Writer writer ->
        writer << "board=1" // main board only
    }

    connection.connectTimeout = Constants.CONNECT_TIMEOUT
    connection.readTimeout = Constants.READ_TIMEOUT
 
    Parser parser = new Parser (connection)
    NodeList rowNodes = parser.extractAllNodesThatMatch (new TagNameFilter('tr'))
    int size = rowNodes.size()
    String stockName
    NodeList cellNodes
    int componentCount = 0
    Date systemDate = new Date()
    StockValuation stockVal
    MarketValuation marketVal = new MarketValuation(indexName: indexName, totalPrice: 0, totalEarningPerShare: 0,
            totalDividendPerShare: 0, created: systemDate)

    for (int i = 1; i < size; i++) {
        cellNodes = rowNodes.elementAt(i).children
        stockName = cellNodes.elementAt(NAME.value).toPlainTextString().substring(0, 9).trim()
        if (components.contains(stockName)) {
            ++componentCount
            stockVal = new StockValuation()
            stockVal.with {
                shortName = stockName
                code = cellNodes.elementAt(CODE.value).toPlainTextString()
                price = cellNodes.elementAt(PRICE.value).toPlainTextString().toBigDecimal()
                volume = cellNodes.elementAt(VOLUME.value).toPlainTextString().toLong()
                earningPerShare = cellNodes.elementAt(EPS.value).toPlainTextString().toBigDecimal()
                dividendPerShare = cellNodes.elementAt(DPS.value).toPlainTextString().toBigDecimal()
                netTangibleAsset = cellNodes.elementAt(NTA.value).toPlainTextString().toDouble()
                priceEarningRatio = cellNodes.elementAt(PE.value).toPlainTextString().toDouble()
                dividendYield = cellNodes.elementAt(DY.value).toPlainTextString().toDouble()
                returnOnEquity = cellNodes.elementAt(ROE.value).toPlainTextString().toDouble()
                priceToBookValue = cellNodes.elementAt(PTBV.value).toPlainTextString().toDouble()
                marketCapital = cellNodes.elementAt(MARKET_CAP.value).toPlainTextString().toDouble()
                indexName = indexName
                created = systemDate
            }
            stockVal.save()
            println "<p>$stockVal</p>"
            marketVal.totalPrice += stockVal.price
            marketVal.totalEarningPerShare += stockVal.earningPerShare
            marketVal.totalDividendPerShare += stockVal.dividendPerShare
            stockVal = null
            if (componentCount == components.size()) {
                marketVal.with {
                    totalEarningPerShare *= 0.01
                    totalDividendPerShare *= 0.01
                    priceEarningRatio = totalPrice / totalEarningPerShare
                    earningYield = totalEarningPerShare / totalPrice * 100
                    dividendYield = totalPrice / totalDividendPerShare * 100
                }
                marketVal.save()
                println "<p>****************************************************</p>"
                println "<p>Total Price: ${marketVal.totalPrice}</p>"
                println "<p>Total EPS: ${marketVal.totalEarningPerShare}</p>"
                println "<p>Total DPS: ${marketVal.totalDividendPerShare}</p>"
                println "<p>PE: ${marketVal.priceEarningRatio}</p>"
                println "<p>EY: ${marketVal.earningYield}</p>"
                println "<p>DY: ${marketVal.dividendYield}</p>"
                break;
            }
        }
    }



