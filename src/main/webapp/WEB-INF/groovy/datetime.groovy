import com.google.appengine.api.datastore.*
import static com.google.appengine.api.datastore.FetchOptions.Builder.*

	def query = new Query("StockValuation")

	query.addSort("created", Query.SortDirection.DESCENDING)

	PreparedQuery preparedQuery = datastore.prepare(query)

	def entities = preparedQuery.asList( withLimit(1) )

	def stockValuation = entities[0] as StockValuation
    
	
	println stockValuation

    stockValuation.created.clearTime() // remove time portion

	println stockValuation.created.format(Constants.DEFAULT_DATE_TIME_FORMAT, Constants.DEFAULT_TIME_ZONE)

	Date now = new Date()
	
	def stockIndexes = datastore.execute {
    	from 'StockIndex' as StockIndex
    	where effectiveDate < now
    	sort desc by effectiveDate 
    	limit 3
	} 

	def stockIndexMap = [:]
	for(stockIndex in stockIndexes){
		stockIndexMap[stockIndex.name] = stockIndex.components
    	println "<p>$stockIndex</p>"
	}

	println "<p>#${stockIndexMap}#</p>"