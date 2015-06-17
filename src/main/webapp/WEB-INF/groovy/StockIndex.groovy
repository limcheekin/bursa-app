import groovyx.gaelyk.datastore.Entity
import groovy.transform.ToString
import groovyx.gaelyk.datastore.Unindexed 

@ToString(includeNames=true)
@Entity(unindexed=false)
class StockIndex {
  String name
  @Unindexed String components
  Date effectiveDate
  Date created
}