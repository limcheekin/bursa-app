import com.sun.syndication.feed.synd.*
import com.sun.syndication.io.SyndFeedOutput
import groovyx.gaelyk.datastore.Key

// ref: http://blogs.bytecode.com.au/glen/2006/12/22/generating-rss-feeds-with-grails-and-rome.html

def supportedFormats = [ "rss_0.90", "rss_0.91", "rss_0.92", "rss_0.93", "rss_0.94", "rss_1.0", "rss_2.0", "atom_0.3"]

def format = params.format?:'rss_1.0'

if (supportedFormats.contains(format)) {
    response.contentType = "text/xml"
    response.characterEncoding = "UTF-8"
    println getFeed(format)
} else {
    response.sendError(response.SC_FORBIDDEN);
}

def getFeed(feedType) {
    def announcements = [
            new Announcement(
                    link: '/EDMS\\edmsweb.nsf/LsvAllByID/1C46E3F09C11FDF5482579BC003BB938?OpenDocument',
                    date: Date.parse('dd/MM/yyyy', '09/03/2012'),
                    title: 'TRANSACTIONS (CHAPTER 10 OF LISTING REQUIREMENTS): NON RELATED PARTY TRANSACTIONS',
                    description: 'PROPOSED STRATEGIC ALLIANCE WITH OPTIMA SYNERGY RESOURCES LIMITED (“OSRL”) THROUGH OSRL’S SUBSCRIPTION OF UP TO 479,833,766 EQUIVALENT TO 23% EQUITY INTEREST IN BEMBAN CORPORATION LIMITED (“BCL”) WITH INJECTION OF CASH AND/OR RELEVANT ASSETS INTO BCL'
            )
    ]


    def entries = []
    announcements.each { announcement ->
        def desc = new SyndContentImpl(type: "text/plain", value: announcement.description)
        def entry = new SyndEntryImpl(title: announcement.title,
                link: announcement.link,
                publishedDate: announcement.date, description: desc)
        entries.add(entry)

    }
    SyndFeed feed = new SyndFeedImpl(feedType: feedType, title: 'MALAYSIA SMELTING CORPORATION BERHAD',
            link: 'http://announcements.bursamalaysia.com/EDMS%5CEdmsWeb.nsf/dfDisplayForm?Openform&Count=-1&form=dfDisplayForm&viewname=LsvAnnsAll&category=MALAYSIA+SMELTING+CORPORATION+BERHAD+', description: 'Announcements published in the last few days',
            entries: entries)

    StringWriter writer = new StringWriter()
    SyndFeedOutput output = new SyndFeedOutput()
    output.output(feed,writer)
    writer.close()

    return writer.toString()

}
