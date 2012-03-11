import org.htmlparser.Parser
import org.htmlparser.filters.TagNameFilter
import org.htmlparser.util.NodeList
import org.htmlparser.util.NodeIterator
import org.htmlparser.Node
import com.google.appengine.api.datastore.*
import static com.google.appengine.api.datastore.FetchOptions.Builder.*

final String URL_PREFIX = 'http://announcements.bursamalaysia.com/EDMS%5CEdmsWeb.nsf/dfDisplayForm?Openform&Count=-1&form=dfDisplayForm&viewname=LsvAnnsAll&category='
final String TITLE_DESC_SEPARATOR = '</A></FONT><B><FONT color=e01f25></FONT></B><FONT SIZE=2 FACE=ARIAL Color=#000000><br><br>'
final String END_LOCATOR = '</FONT></TD></TR></a></font></td></tr>'

Parser parser
def tableNode
NodeList rowNodes
Node rowNode
String rowHtml
int separatorIndex
int endIndex
Announcement announcement
Entity entity
Stock stock
Date systemDate = new Date()

def stockEntities = datastore.prepare(new Query("Stock")).asList( withLimit(1000) )

stockEntities.each { stockEntity ->
    stock = stockEntity as Stock
    parser = new Parser (URL_PREFIX + stock.name.replaceAll(' ', '+'))
    tableNode = parser.parse (new TagNameFilter('table')).elementAt(0)
    rowNodes = tableNode.getChildren()
    for (NodeIterator e = rowNodes.elements(); e.hasMoreNodes ();) {
        rowNode = e.nextNode()
        rowHtml = rowNode.toHtml()
        if (rowHtml.trim()) {
            announcement = new Announcement()
            announcement.stockName = stock.shortName
            announcement.date = Date.parse('dd/MM/yyyy', rowHtml.substring(270, 280))
            announcement.link = rowHtml.substring(310, 384)
            separatorIndex = rowHtml.indexOf(TITLE_DESC_SEPARATOR)

            if (separatorIndex == -1) { // no description
                separatorIndex = rowHtml.lastIndexOf('</A>')
            } else {
                endIndex = rowHtml.lastIndexOf(END_LOCATOR)
                announcement.description = rowHtml.substring(separatorIndex + TITLE_DESC_SEPARATOR.length(), endIndex)
            }
            announcement.title = rowHtml.substring(385, separatorIndex)
            announcement.created = systemDate
            entity = announcement as Entity
            entity.save()
        }
    }
}

println 'ok'


