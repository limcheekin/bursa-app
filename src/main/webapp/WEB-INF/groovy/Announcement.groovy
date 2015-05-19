import groovyx.gaelyk.datastore.*

class Announcement {
  @Key String link
  String stockName
  Date date
  @Unindexed String title
  @Unindexed String description
  @Unindexed Date created
}