import com.google.appengine.api.datastore.*
import com.google.appengine.api.datastore.EntityNotFoundException

try {
    Entity entity = datastore.get('Stock', params.stockName)
    assert entity
    if (entity) {
      Stock stock = entity as Stock
      println stock.name
    }
} catch (EntityNotFoundException e) {
    log.warning e.message
    response.sendError(response.SC_FORBIDDEN)
}