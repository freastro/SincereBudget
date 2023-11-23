import com.sincerepost.scraper.ScrapeConfig
import scraper.ListAccounts
import kotlinx.browser.document

@JsExport
fun listAccounts(config: ScrapeConfig): Any? =
    if (document.body != null) ListAccounts(config).scrape(document.body!!)
    else null
