
get "/", forward: "/WEB-INF/pages/index.gtpl"
get "/datetime", forward: "/datetime.groovy"
get "/htmlParsing", forward: "/htmlParsing.groovy"
get "/feed", forward: "/feed.groovy"

get "/favicon.ico", redirect: "/images/gaelyk-small-favicon.png"
