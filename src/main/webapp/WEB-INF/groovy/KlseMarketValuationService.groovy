import groovyx.gaelyk.GaelykBindings
import java.util.logging.Logger

@GaelykBindings
class KlseMarketValuationService {
	private static final Logger log = Logger.getLogger(KlseMarketValuationService.class.name) 

	Map getFbmIndices() {
		Map fbmIndices

		if (Constants.FBM_INDICES in memcache && memcache[Constants.FBM_INDICES]) {
			fbmIndices = memcache[Constants.FBM_INDICES]
			log.info "fbmIndices from memcache: $fbmIndices"
		} else {
		    def marketValuations = datastore.execute {
		        select all from marketValuation
		        sort desc by created
		        limit 5
		    }
		    fbmIndices = [:]
		    for (marketVal in marketValuations) {
		    	fbmIndices[marketVal.indexName] = marketVal
		    }
		    memcache[Constants.FBM_INDICES] = fbmIndices
		    log.info 'fbmIndices from datastore'
		}
		return fbmIndices
	}
}