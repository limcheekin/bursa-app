import org.htmlparser.Parser
import org.htmlparser.filters.TagNameFilter
import org.htmlparser.util.NodeList
import org.htmlparser.util.NodeIterator
import org.htmlparser.Node

def urlString = 'http://announcements.bursamalaysia.com/EDMS%5CEdmsWeb.nsf/dfDisplayForm?Openform&Count=-1&form=dfDisplayForm&viewname=LsvAnnsAll&category=SUNWAY+REAL+ESTATE+INVESTMENT+TRUST'
Parser parser = new Parser (urlString)
def tableNode = parser.parse (new TagNameFilter('table')).elementAt(0)

if (tableNode) {
    NodeList rowNodes = tableNode.getChildren()
    Node rowNode
    String rowHtml
    int separatorIndex
    int endIndex
    final String TITLE_DESC_SEPARATOR = '</A></FONT><B><FONT color=e01f25></FONT></B><FONT SIZE=2 FACE=ARIAL Color=#000000><br><br>'
    final String END_LOCATOR = '</FONT></TD></TR></a></font></td></tr>'
    for (NodeIterator e = rowNodes.elements(); e.hasMoreNodes ();) {
        rowNode = e.nextNode()
        rowHtml = rowNode.toHtml()
        if (rowHtml.trim()) {
            println "<p>date: #${rowHtml.substring(270, 280)}#"
            println "<p>link: #${rowHtml.substring(310, 384)}#"
            separatorIndex = rowHtml.indexOf(TITLE_DESC_SEPARATOR)

            if (separatorIndex == -1) { // no description
                separatorIndex = rowHtml.lastIndexOf('</A>')
                println "<p>title: #${rowHtml.substring(385, separatorIndex)}#"
            } else {
                println "<p>title: #${rowHtml.substring(385, separatorIndex)}#"
                endIndex = rowHtml.lastIndexOf(END_LOCATOR)
                println "<p>description: #${rowHtml.substring(separatorIndex + TITLE_DESC_SEPARATOR.length(), endIndex)}#"
            }
        }
    }
}