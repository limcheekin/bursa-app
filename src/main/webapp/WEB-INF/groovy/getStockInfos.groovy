import org.htmlparser.Parser
import org.htmlparser.filters.TagNameFilter
import org.htmlparser.util.NodeList
import org.htmlparser.util.NodeIterator
import org.htmlparser.Node
import com.google.appengine.api.datastore.*
import java.net.HttpURLConnection

import static com.google.appengine.api.datastore.FetchOptions.Builder.*

Entity entity
Date systemDate = new Date()

int STOCK_NAME = 7
int STOCK_CODE = 9
int STOCK_PRICE = 13
int STOCK_VOLUME = 15
int STOCK_EPS = 17
int STOCK_DPS = 19
int STOCK_NTA = 21
int STOCK_PE = 23
int STOCK_DY = 25
int STOCK_ROE = 27
int STOCK_PTBV = 29
int STOCK_MARKET_CAP = 31

// Ref URL: https://cloud.google.com/appengine/docs/java/urlfetch/#Java_Quotas_and_limits
int CONNECT_TIMEOUT = 15000
int READ_TIMEOUT = 5000

List FBMKLCI_COMPONENTS = [
    'AMBANK','ASTRO','AXIATA','BAT','CIMB',
    'DIGI','FGV','GENTING','GENM','HLBANK',
    'HLFG','IHH','IOICORP','KLCC','KLK',
    'MAYBANK','MAXIS','MISC','PCHEM','PETDAG',
    'PETGAS','PPB','PBBANK','RHBCAP','SKPETRO',
    'SIME','TM','TENAGA','UMW','YTL'
]

    // Ref URL: http://www.kellyrob99.com/blog/2013/02/10/groovy-and-http/
    HttpURLConnection connection = Constants.KLSE_SCREENER_URL.toURL().openConnection()
    connection.doOutput = true
    connection.requestMethod = "POST"
    connection.outputStream.withWriter { Writer writer ->
        writer << "board=1"
    }

    connection.connectTimeout = CONNECT_TIMEOUT
    connection.readTimeout = READ_TIMEOUT    
 
    Parser parser = new Parser (connection)
    NodeList rowNodes = parser.extractAllNodesThatMatch (new TagNameFilter('tr'))
    int size = rowNodes.size()
    String stockName
    NodeList cellNodes
    StringBuilder stockInfo = new StringBuilder(100)
    int fbmKlciCount = 0
    BigDecimal totalPrice = 0
    BigDecimal totalEPS = 0
    BigDecimal totalDPS = 0
    BigDecimal price = 0
    BigDecimal eps = 0
    BigDecimal dps = 0
    for (int i = 1; i < size; i++) {
        cellNodes = rowNodes.elementAt(i).children
        stockName = cellNodes.elementAt(STOCK_NAME).toPlainTextString().substring(0, 9).trim()
        if (FBMKLCI_COMPONENTS.contains(stockName)) {
            ++fbmKlciCount
            stockInfo = new StringBuilder(100)
            stockInfo << "$i) "
            stockInfo << "${stockName}, "
            stockInfo << "${cellNodes.elementAt(STOCK_CODE).toPlainTextString()}, "
            price = cellNodes.elementAt(STOCK_PRICE).toPlainTextString().toBigDecimal()
            stockInfo << "Price: ${price}, "
            stockInfo << "Vol: ${cellNodes.elementAt(STOCK_VOLUME).toPlainTextString()}, "
            eps = cellNodes.elementAt(STOCK_EPS).toPlainTextString().toBigDecimal()
            stockInfo << "EPS: ${eps}, "
            dps = cellNodes.elementAt(STOCK_DPS).toPlainTextString().toBigDecimal()
            stockInfo << "DPS: ${dps}, "
            stockInfo << "NTA: ${cellNodes.elementAt(STOCK_NTA).toPlainTextString()}, "
            stockInfo << "PE: ${cellNodes.elementAt(STOCK_PE).toPlainTextString()}, "
            stockInfo << "DY: ${cellNodes.elementAt(STOCK_DY).toPlainTextString()}, "
            stockInfo << "ROE: ${cellNodes.elementAt(STOCK_ROE).toPlainTextString()}, "
            stockInfo << "PTBV: ${cellNodes.elementAt(STOCK_PTBV).toPlainTextString()}, "
            stockInfo << "Market Cap: ${cellNodes.elementAt(STOCK_MARKET_CAP).toPlainTextString()}"        
            println "<p>${stockInfo.toString()}</p>"
            stockInfo = null
            totalPrice += price
            totalEPS += eps
            totalDPS += dps
            if (fbmKlciCount == FBMKLCI_COMPONENTS.size()) {
                println "<p>****************************************************</p>"
                println "<p>Total Price: $totalPrice</p>" 
                totalEPS *= 0.01
                totalDPS *= 0.01
                println "<p>Total EPS: $totalEPS</p>" 
                println "<p>Total DPS: $totalDPS</p>" 
                println "<p>PE: ${totalPrice / totalEPS}</p>" 
                println "<p>EY: ${totalEPS / totalPrice}</p>" 
                println "<p>DY: ${totalDPS / totalPrice}</p>" 

                break;
            }
        }
    }



