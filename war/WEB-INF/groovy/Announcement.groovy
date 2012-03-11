import groovyx.gaelyk.datastore.*

class Announcement {
  @Key String link
  String stockName
  @Unindexed Date date
  @Unindexed String title
  @Unindexed String description

}