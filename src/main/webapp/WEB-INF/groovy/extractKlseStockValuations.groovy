import org.htmlparser.Parser
import org.htmlparser.filters.TagNameFilter
import org.htmlparser.util.NodeList
import org.htmlparser.tags.TableColumn
import static KlseScreenerColumn.*

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
    NodeList cellNodes
    TableColumn nameColumn
    Date systemDate = new Date()
    StockValuation stockVal
    int size = rowNodes.size()
    String stockName
    int stockValCount = 0

    for (int i = 1; i < size; i++) {
        cellNodes = rowNodes.elementAt(i).children
        nameColumn = cellNodes.elementAt(NAME.value)
        stockName = nameColumn.getChild(0).toPlainTextString()
         
        if (stockName.lastIndexOf('-') == -1) { // not W, PA, OP, etc
            stockVal = new StockValuation()
            stockVal.with {
                shortName = stockName
                name = nameColumn.getAttribute('title')
                code = cellNodes.elementAt(CODE.value).toPlainTextString()
                category = cellNodes.elementAt(CATEGORY.value).toPlainTextString().replace(', Main Market', '')
                price = toDouble(cellNodes.elementAt(PRICE.value).toPlainTextString())
                volume = cellNodes.elementAt(VOLUME.value).toPlainTextString().toLong()
                earningPerShare = toDouble(cellNodes.elementAt(EPS.value).toPlainTextString())
                dividendPerShare = toDouble(cellNodes.elementAt(DPS.value).toPlainTextString())
                netTangibleAsset = toDouble(cellNodes.elementAt(NTA.value).toPlainTextString())
                priceEarningRatio = toDouble(cellNodes.elementAt(PE.value).toPlainTextString())
                dividendYield = toDouble(cellNodes.elementAt(DY.value).toPlainTextString())
                returnOnEquity = toDouble(cellNodes.elementAt(ROE.value).toPlainTextString())
                priceToBookValue = toDouble(cellNodes.elementAt(PTBV.value).toPlainTextString())
                marketCapital = toDouble(cellNodes.elementAt(MARKET_CAP.value).toPlainTextString())
                created = systemDate
            }
            stockVal.save()
            stockVal = null
            ++stockValCount
        }
    }

    String info = "$stockValCount stocks extracted on ${systemDate.format(Constants.DEFAULT_DATE_TIME_FORMAT, Constants.DEFAULT_TIME_ZONE)}" 
    log.info info
    println info

def toDouble(String value) {
    value ? value.replaceAll(',', '').toDouble(): 0.0
}

def toBigDecimal(String value) {
    value ? value.replaceAll(',', '').toBigDecimal(): BigDecimal.ZERO
}
