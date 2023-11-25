import com.sincerepost.scraper.AccountMatch
import com.sincerepost.scraper.ScrapeConfig
import kotlinx.browser.document
import kotlinx.html.*
import kotlinx.html.dom.create
import scraper.ListAccounts
import kotlin.test.Test
import kotlin.test.assertContentEquals

class ListAccountsTest {

    companion object {
        private val DEFAULT_CONFIG = object : ScrapeConfig {
            override val currencyRegex = "\\$\\d+\\.\\d{2}"
        }
    }

    @Test
    fun testList01() {
        val result = scrape {
            ul {
                li {
                    div {
                        span { +"Savings" }
                        span { +"1234" }
                    }
                    div {
                        span { +"Current Balance" }
                        span { +"$2.00" }
                    }
                    div {
                        span { +"Available Balance" }
                        span { +"$1.00" }
                    }
                }
            }
            div {
                script { + "var ModuleA = 1;" }
                script { + "var ModuleB = 1;" }
            }
        }
        assertContentEquals(listOf(AccountMatch("1234", "Savings", "$2.00",
            "body>:nth-child(0)>:nth-child(0)>:nth-child(0)>:nth-child(0)")), result)
    }

    private fun scrape(config: ScrapeConfig = DEFAULT_CONFIG, body: BODY.() -> Unit): List<AccountMatch> {
        val instance = ListAccounts(config)
        return instance.scrape(document.create.body(block = body), "body").getOrDefault(emptyList())
    }
}
