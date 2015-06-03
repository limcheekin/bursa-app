import yahoofinance.YahooFinanceService

log.info "Setting attribute datetime"

request.setAttribute 'datetime', new Date().toString()

log.info "Forwarding to the template"

YahooFinanceService yahooFinanceService = new YahooFinanceService()
println yahooFinanceService.testYahooQuote()

