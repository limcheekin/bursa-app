import groovyx.gaelyk.datastore.Entity
import groovyx.gaelyk.datastore.Indexed
import groovy.transform.ToString

@ToString(includeNames=true)
@Entity(unindexed=false)
class MarketValuation implements Serializable {
  String indexName
  Double point
  Long volume
  Double totalPrice
  Double totalEarningPerShare
  Double totalDividendPerShare
  Double priceEarningRatio
  Double earningYield
  Double dividendYield
  Date created
}