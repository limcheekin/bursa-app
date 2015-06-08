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
        def entries = []
        def desc = new SyndContentImpl(type: "text/plain", value: "")
        def entry = new SyndEntryImpl(title: 'Stock Announcements Feed Service is no longer supported.',
                    link: '#',
                    publishedDate: new Date(), description: desc)
        entries.add(entry)

        SyndFeed feed = new SyndFeedImpl(feedType: feedType, title: "Service Unavailable",
                link: '#', description: '',
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
