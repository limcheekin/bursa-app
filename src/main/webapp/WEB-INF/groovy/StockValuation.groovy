import groovyx.gaelyk.datastore.Entity
import groovy.transform.ToString

@ToString(includeNames=true)
@Entity(unindexed=false)
class StockValuation {
  String shortName
  String name
  String code
  String category
  Double price
  Long volume
  Double earningPerShare
  Double dividendPerShare
  Double netTangibleAsset
  Double priceEarningRatio
  Double dividendYield
  Double returnOnEquity
  Double priceToBookValue
  Double marketCapital
  Date created
}