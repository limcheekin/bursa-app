import groovy.json.JsonSlurper 

    String indexDataJson = Constants.STARBIZ_INDICES_JSON_URL.toURL().text
    int index = indexDataJson.indexOf('{')
    def slurper = new JsonSlurper()
    def indexData = slurper.parseText(indexDataJson.substring(index))
    def indexDataMap = [:]
    indexData.Indices.each {
        indexDataMap[it.symbol] = it
    } 

    Date maxDate = datastore.execute {
        select created: Date from stockValuation
        sort desc by created
        limit 1
    }
    maxDate.clearTime() // remove time portion

    MarketValuation fbmKlciVal = createMarketValue(Constants.FBMKLCI_NAME, Constants.FBMKLCI_COMPONENTS, maxDate, indexDataMap)
    MarketValuation fbm70Val = createMarketValue(Constants.FBM70_NAME, Constants.FBM70_COMPONENTS, maxDate, indexDataMap)
    MarketValuation fbmT100 = new MarketValuation(indexName: Constants.FBMT100_NAME, created: maxDate)
    fbmT100.with {
        point = toDouble(indexDataMap[Constants.FBMT100_NAME].last)
        volume = toLong(indexDataMap[Constants.FBMT100_NAME].vol)
        totalPrice = fbmKlciVal.totalPrice + fbm70Val.totalPrice
        totalEarningPerShare = fbmKlciVal.totalEarningPerShare + fbm70Val.totalEarningPerShare
        totalDividendPerShare = fbmKlciVal.totalDividendPerShare + fbm70Val.totalDividendPerShare
        priceEarningRatio = totalPrice / totalEarningPerShare
        earningYield = totalEarningPerShare / totalPrice * 100
        dividendYield = totalDividendPerShare / totalPrice  * 100        
    }
    MarketValuation fbmSCapVal = createMarketValue(Constants.FBMSCAP_NAME, Constants.FBMSCAP_COMPONENTS, maxDate, indexDataMap)
    MarketValuation fbmEmas = new MarketValuation(indexName: Constants.FBMEMAS_NAME, created: maxDate)
    fbmEmas.with {
        point = toDouble(indexDataMap[Constants.FBMEMAS_NAME].last)
        volume = toLong(indexDataMap[Constants.FBMEMAS_NAME].vol)
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

    memcache[Constants.FBM_INDICES] = [
        "${Constants.FBMKLCI_NAME}": fbmKlciVal,
        "${Constants.FBM70_NAME}": fbm70Val,
        "${Constants.FBMT100_NAME}": fbmT100,
        "${Constants.FBMSCAP_NAME}": fbmSCapVal,
        "${Constants.FBMEMAS_NAME}": fbmEmas
    ]

    println memcache[Constants.FBM_INDICES]



def createMarketValue(String indexName, String indexComponentNames, Date maxDate, Map indexDataMap) {
    List indexComponentNameList = indexComponentNames.split(',') as List
    def indexComponents = StockValuation.findAll {
        where created >= maxDate 
        and created < maxDate + 1 
        and shortName in indexComponentNameList
    }

    MarketValuation marketVal = new MarketValuation(indexName: indexName, created: maxDate)
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

def toDouble(String value) {
    value ? value.replaceAll(',', '').toDouble(): 0.0
}

def toLong(String value) {
    value ? value.replaceAll(',', '').toLong(): 0
}

/*def printMarketValue(marketVal) {
    println "******************* FBM Valuation *********************"
    println "\nIndex\t|\tLast\t|\tVolume\t|\tPE\t|\tEY\t|\tDY"
    marketVal.with {
    println "\n$indexName\t|\t$point\t|\t$volume\t|\t$priceEarningRatio\t|\t$earningYield\t|\t$dividendYield"
    }

    println "Point: ${marketVal.totalPrice}</p>"
    println "Total EPS: ${marketVal.totalEarningPerShare}</p>"
    println "Total DPS: ${marketVal.totalDividendPerShare}</p>"
    println "PE: ${marketVal.priceEarningRatio}</p>"
    println "EY: ${marketVal.earningYield}</p>"
    println "DY: ${marketVal.dividendYield}</p>"
}*/

// https://gist.github.com/kdabir/1890733




