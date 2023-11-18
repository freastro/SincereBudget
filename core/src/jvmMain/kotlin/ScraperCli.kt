import com.sincerepost.budget.account.Account
import com.sincerepost.budget.jvm.Selenium
import com.sincerepost.budget.scraper.Scraper
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        val account = Account("Test", "Vendor", "http://example.com", "user")

        println("Starting Chrome...")
        val browser = Selenium()
        val scraper = Scraper(account, browser)
        println("Loading...")
        var result = scraper.scrape()
        println("Result: $result")
        browser.close()
    }
}
