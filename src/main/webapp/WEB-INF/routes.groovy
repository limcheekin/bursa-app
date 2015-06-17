
get "/", forward: "/WEB-INF/pages/index.gtpl"
get "/datetime", forward: "/datetime.groovy"
get "/htmlParsing", forward: "/htmlParsing.groovy"
get "/feed", forward: "/feed.groovy"
get "/createStock", forward: "/createStock.groovy"
get "/createStockIndex", forward: "/createStockIndex.groovy"
//get "/getAnnouncements", forward: "/getAnnouncements.groovy"
get "/cron/extractKlseStocks", forward: "/extractKlseStockValuations.groovy"
get "/cron/calcKlseMarketValuations", forward: "/calcKlseMarketValuations.groovy"
get "/showKlseMarketValuations", forward: "/showKlseMarketValuations.groovy"

get "/favicon.ico", redirect: "/images/gaelyk-small-favicon.png"
