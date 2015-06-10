import groovyx.gaelyk.GaelykBindings
import java.util.logging.Logger
import com.google.appengine.api.datastore.*
import static com.google.appengine.api.datastore.FetchOptions.Builder.*
import java.text.NumberFormat
import java.text.DecimalFormat
import static FbmIndices.*

@GaelykBindings
class KlseMarketValuationService {
	private static final Logger log = Logger.getLogger(KlseMarketValuationService.class.name) 

	Map getFbmIndices() {
		Map fbmIndices = memcache.getAll(FbmIndices.values().collect { it.value })
		List fbmIndicesValues = fbmIndices.values() as List

		// Ref URL: http://www.leveluplunch.com/groovy/examples/remove-filter-null-items-from-arraylist-collection/
		if (fbmIndicesValues.minus(null).size() == 5) {
			log.info "fbmIndices from memcache: $fbmIndices"
		} else {
		    def marketValuationEntities = getMarketValuationEntities()
		    MarketValuation marketVal
		    for (e in marketValuationEntities) {
		    	marketVal = e as MarketValuation
		    	fbmIndices[marketVal.indexName] = marketVal
		    	memcache[marketVal.indexName] = marketVal
		    }
		    log.info 'fbmIndices from datastore'
		}
		return fbmIndices
	}

	private getMarketValuationEntities() {
		Query query = new Query("MarketValuation").addSort("ended", Query.SortDirection.DESCENDING)

		PreparedQuery preparedQuery = datastore.prepare(query)

		return preparedQuery.asList( withLimit(5) )
	}

	def sendEmail() {
		mail.send from: Constants.EMAIL_FROM,
		to: Constants.EMAIL_TO,
		subject: getEmailSubject(),
		textBody: getEmailBody()
	}

	private String getEmailSubject() {
		Date on = getFbmIndices()[FBMKLCI.value].ended
		return "FBM Indices Valuation on ${on.format(Constants.DEFAULT_DATE_FORMAT)}"
	}

	def getEmailBody() {
		Map fbmIndices = getFbmIndices()
		MarketValuation marketVal
		StringBuilder body = new StringBuilder(1020)
		Integer n = Constants.EMAIL_BODY_COLUMN_MAX_CHARS
		NumberFormat nf = NumberFormat.getInstance()
		// Ref URL: https://gist.github.com/kdabir/1890733
		DecimalFormat df = new DecimalFormat("#,##0.00");
		int charsPerLine = n * 6 + 5
		String line = '-' * charsPerLine
		body << getEmailSubject().center(charsPerLine)
		body << "\n$line"

		// Ref URL: http://mrhaki.blogspot.com/2009/09/groovy-goodness-padding-strings.html
		body << "\n${'Index'.center(n)}|${'Last'.center(n)}|${"Volume ('00)".center(n)}|${'PE'.center(n)}|${'EY (%)'.center(n)}|${'DY (%)'.center(n)}"
		body << "\n$line"
		FbmIndices.values().each {
			marketVal = fbmIndices[it.value]
    		marketVal.with {
    			body << "\n${indexName.center(n)}|${nf.format(point).center(n)}|"
    			body << "${nf.format(volume).center(n)}|${df.format(priceEarningRatio).center(n)}|"
    			body << "${df.format(earningYield).center(n)}|${df.format(dividendYield).center(n)}"
    		}				
		}
		body << "\n$line"
	}	
}