import groovy.json.JsonSlurper
import com.google.appengine.api.datastore.*
import static com.google.appengine.api.datastore.FetchOptions.Builder.*
import static FbmIndices.*

    // Ref URL: http://mrhaki.blogspot.com/2011/09/groovy-goodness-use-connection.html
    String indexDataJson = Constants.STARBIZ_INDICES_JSON_URL.toURL().getText(connectTimeout: Constants.CONNECT_TIMEOUT, 
                                                                              readTimeout: Constants.READ_TIMEOUT)
    int index = indexDataJson.indexOf('{')
    def slurper = new JsonSlurper()
    def indexData = slurper.parseText(indexDataJson.substring(index))
    def indexDataMap = [:]
    indexData.Indices.each {
        indexDataMap[it.symbol] = it
    } 

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
    }    

    Date maxDate = getMaxDate()
    maxDate.clearTime() // remove time portion

    MarketValuation fbmKlciVal = createMarketValue(FBMKLCI.value, stockIndexMap[FBMKLCI.value], maxDate, now, indexDataMap)
    MarketValuation fbm70Val = createMarketValue(FBM70.value, stockIndexMap[FBM70.value], maxDate, now, indexDataMap)
    MarketValuation fbmT100 = new MarketValuation(indexName: FBMT100.value, ended: maxDate, created: now)
    fbmT100.with {
        point = toDouble(indexDataMap[FBMT100.value].last)
        volume = toLong(indexDataMap[FBMT100.value].vol)
        totalPrice = fbmKlciVal.totalPrice + fbm70Val.totalPrice
        totalEarningPerShare = fbmKlciVal.totalEarningPerShare + fbm70Val.totalEarningPerShare
        totalDividendPerShare = fbmKlciVal.totalDividendPerShare + fbm70Val.totalDividendPerShare
        priceEarningRatio = totalPrice / totalEarningPerShare
        earningYield = totalEarningPerShare / totalPrice * 100
        dividendYield = totalDividendPerShare / totalPrice  * 100        
    }
    MarketValuation fbmSCapVal = createMarketValue(FBMSCAP.value, stockIndexMap[FBMSCAP.value], maxDate, now, indexDataMap)
    MarketValuation fbmEmas = new MarketValuation(indexName: FBMEMAS.value, ended: maxDate, created: now)
    fbmEmas.with {
        point = toDouble(indexDataMap[FBMEMAS.value].last)
        volume = toLong(indexDataMap[FBMEMAS.value].vol)
        totalPrice = fbmT100.totalPrice + fbmSCapVal.totalPrice
        totalEarningPerShare = fbmT100.totalEarningPerShare + fbmSCapVal.totalEarningPerShare
        totalDividendPerShare = fbmT100.totalDividendPerShare + fbmSCapVal.totalDividendPerShare
        priceEarningRatio = totalPrice / totalEarningPerShare
        earningYield = totalEarningPerShare / totalPrice * 100
        dividendYield = totalDividendPerShare / totalPrice  * 100          
    }

    datastore.withTransaction(true) {
        fbmKlciVal.save()
        fbm70Val.save() 
        fbmT100.save() 
        fbmSCapVal.save() 
        fbmEmas.save() 
    }    

    memcache[FBMKLCI.value] = fbmKlciVal
    memcache[FBM70.value] = fbm70Val
    memcache[FBMT100.value] = fbmT100
    memcache[FBMSCAP.value] = fbmSCapVal
    memcache[FBMEMAS.value] = fbmEmas

    new KlseMarketValuationService().sendEmail()


def createMarketValue(String indexName, String indexComponentNames, Date maxDate, Date now, Map indexDataMap) {
    List indexComponentNameList = indexComponentNames.split(',') as List
    def indexComponents = StockValuation.findAll {
        where created >= maxDate 
        and created < maxDate + 1 
        and shortName in indexComponentNameList
    }

    MarketValuation marketVal = new MarketValuation(indexName: indexName, ended: maxDate, created: now)
    marketVal.with {
        point = toDouble(indexDataMap[indexName].last)
        volume = toLong(indexDataMap[indexName].vol)
        totalPrice = indexComponents*.price.sum()
        totalEarningPerShare = indexComponents*.earningPerShare.sum() * 0.01
        totalDividendPerShare = indexComponents*.dividendPerShare.sum() * 0.01
        priceEarningRatio = totalPrice / totalEarningPerShare
        earningYield = totalEarningPerShare / totalPrice * 100
        dividendYield = totalDividendPerShare / totalPrice  * 100
    }
    return marketVal
}

Date getMaxDate() {
    query = new Query("StockValuation")
    query.addSort("created", Query.SortDirection.DESCENDING)
    PreparedQuery preparedQuery = datastore.prepare(query)
    def entities = preparedQuery.asList( withLimit(1) )
    def stockValuation = entities[0] as StockValuation
    return stockValuation.created
}

def toDouble(String value) {
    value ? value.replaceAll(',', '').toDouble(): 0.0
}

def toLong(String value) {
    value ? value.replaceAll(',', '').toLong(): 0
}




