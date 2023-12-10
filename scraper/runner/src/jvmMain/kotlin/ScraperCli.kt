import com.sincerepost.scraper.Scraper
import com.sincerepost.scraper.JvmScrapeConfig
import com.sincerepost.scraper.jvm.Selenium
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.system.exitProcess

object ScraperCli {
    @JvmStatic
    fun main(args: Array<String>) {
        if (args.size != 1) {
            println("usage: ScraperCli <url>")
            exitProcess(1)
        }

        val config = JvmScrapeConfig("\\$\\d+\\.\\d{2}")
        val selenium = Selenium()
        val scraper = Scraper(args[0], config, selenium)
        runBlocking {
            println(">>> Config = ${Json.encodeToString(config)}")
            println(">>> Please login ...")
            scraper.login()
            Thread.sleep(60000)
            println(">>> Listing accounts ...")
            val accounts = scraper.listAccounts()
            println(accounts)
            println(">>> Cleaning up ....")
        }
        selenium.close()
    }
}
