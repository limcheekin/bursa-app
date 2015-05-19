import com.rometools.rome.feed.synd.*
import com.rometools.rome.io.SyndFeedOutput
import com.google.appengine.api.datastore.*
import static com.google.appengine.api.datastore.FetchOptions.Builder.*
import com.google.appengine.api.datastore.EntityNotFoundException

// ref: http://blogs.bytecode.com.au/glen/2006/12/22/generating-rss-feeds-with-grails-and-rome.html

def supportedFormats = [ "rss_0.90", "rss_0.91", "rss_0.92", "rss_0.93", "rss_0.94", "rss_1.0", "rss_2.0", "atom_0.3", "atom_1.0"]

def format = params.format?:'rss_1.0'

if (supportedFormats.contains(format)) {
    response.contentType = "text/xml"
    response.characterEncoding = "UTF-8"
    println getFeed(format)
} else {
    response.sendError(response.SC_FORBIDDEN);
}

def getFeed(feedType) {
    try {
        Entity stockEntity = datastore.get('Stock', params.stockName)
        Stock stock = stockEntity as Stock

        Announcement announcement

        def query = new Query("Announcement")

        query.addSort("date", Query.SortDirection.DESCENDING)

        query.addFilter("stockName", Query.FilterOperator.EQUAL, params.stockName)

        PreparedQuery preparedQuery = datastore.prepare(query)

        def entities = preparedQuery.asList( withLimit(Constants.ANNOUNCEMENTS_LIMIT) )

        def entries = []
        entities.each { entity ->
            announcement = entity as Announcement
            def desc = new SyndContentImpl(type: "text/plain", value: announcement.description)
            def entry = new SyndEntryImpl(title: announcement.title,
                    link: Constants.BURSA_ANNOUNCEMENTS_URL + announcement.link,
                    publishedDate: announcement.date, description: desc)
            entries.add(entry)

        }
        SyndFeed feed = new SyndFeedImpl(feedType: feedType, title: stock.name,
                link: Constants.URL_PREFIX + stock.name.replaceAll(' ', '+'), description: 'Announcements published in the last few days',
                entries: entries)

        StringWriter writer = new StringWriter()
        SyndFeedOutput output = new SyndFeedOutput()
        output.output(feed,writer)
        writer.close()

        return writer.toString()
    } catch (EntityNotFoundException e) {
        log.warning e.message
        response.sendError(response.SC_FORBIDDEN)
    }
}
