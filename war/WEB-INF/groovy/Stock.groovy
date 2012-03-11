import groovyx.gaelyk.datastore.*

class Stock {
    @Key String shortName
    String name
    @Unindexed Date created
    @Unindexed Date updated
}