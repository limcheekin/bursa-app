class Constants {
    static final String BURSA_ANNOUNCEMENTS_URL = 'http://announcements.bursamalaysia.com/'
    static final String KLSE_SCREENER_URL = 'http://www.klsescreener.com/v2/screener/quote_results/'
    static final String URL_PREFIX = 'http://announcements.bursamalaysia.com/EDMS%5CEdmsWeb.nsf/dfDisplayForm?Openform&Count=-1&form=dfDisplayForm&viewname=LsvAnnsAll&category='
    static final int ANNOUNCEMENTS_LIMIT = 10000
    // Ref URL: https://cloud.google.com/appengine/docs/java/urlfetch/#Java_Quotas_and_limits
    static final int CONNECT_TIMEOUT = 15000
    static final int READ_TIMEOUT = 5000

}