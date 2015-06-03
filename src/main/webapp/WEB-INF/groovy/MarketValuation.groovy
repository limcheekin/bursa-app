import groovyx.gaelyk.datastore.*

@Entity
class MarketValuation {
  @Indexed String indexName
  Double point
  BigDecimal totalPrice
  BigDecimal totalEarningPerShare
  BigDecimal totalDividendPerShare
  Double priceEarningRatio
  Double earningYield
  Double dividendYield
  @Indexed Date created
}