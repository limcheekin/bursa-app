import com.google.appengine.api.datastore.*

Date systemDate = new Date()
def e = new Stock(shortName: params.shortName, name: params.name, created: systemDate, updated: currentDate) as Entity
e.save()
println "${params.shortName} created."