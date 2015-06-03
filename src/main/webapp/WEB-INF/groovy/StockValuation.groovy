import groovyx.gaelyk.datastore.Entity
import groovy.transform.ToString

@ToString(includeNames=true)
@Entity(unindexed=false)
class StockValuation {
  String shortName
  String code
  BigDecimal price
  Long volume
  BigDecimal earningPerShare
  BigDecimal dividendPerShare
  Double netTangibleAsset
  Double priceEarningRatio
  Double dividendYield
  Double returnOnEquity
  Double priceToBookValue
  Double marketCapital
  String indexName
  Date created
}