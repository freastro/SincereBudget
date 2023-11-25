import com.sincerepost.scraper.ScrapeConfig
import kotlinx.browser.document
import org.w3c.dom.Element
import scraper.ListAccounts

@JsExport
fun listAccounts(config: ScrapeConfig, start: Element? = document.body, selector: String = "body"): Result<Any?> =
    if (start != null) ListAccounts(config).scrape(start, selector)
    else Result.success(null)
