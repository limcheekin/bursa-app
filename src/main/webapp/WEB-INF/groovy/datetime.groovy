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
