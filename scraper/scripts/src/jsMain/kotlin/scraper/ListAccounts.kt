package scraper

import com.sincerepost.scraper.AccountMatch
import com.sincerepost.scraper.ScrapeConfig
import dom.DomNotFound
import dom.DomResult
import dom.DomVisitor
import org.w3c.dom.*
import kotlin.js.RegExp

class ListAccounts(config: ScrapeConfig) : Scraper<List<AccountMatch>>(config), DomVisitor<DomResult> {

    data class AccountsNode(val accounts: List<AccountMatch>, val selector: String) : DomResult
    data class BalanceNode(val text: String, val isCurrent: Boolean, val selector: String) : DomResult
    data class CurrencyNode(val text: String, val selector: String) : DomResult
    data class TextNode(val text: String, val selector: String) : DomResult

    private val currencyRegex = RegExp(config.currencyRegex)

    override fun scrape(body: Element): List<AccountMatch> {
        val node = visit(body)
        return if (node is AccountsNode) node.accounts else emptyList()
    }

    override fun visitElement(el: Node): DomResult {
        return el.childNodes.asList()
            .map { visit(it) }
            .filter { it != DomNotFound }
            .reduce { a, b ->
                return if (a is AccountsNode || b is AccountsNode) {
                    val balance = if (a is BalanceNode) {
                        a.text
                    } else if (a is CurrencyNode) {
                        a.text
                    } else if (b is BalanceNode) {
                        b.text
                    } else if (b is CurrencyNode) {
                        b.text
                    } else {
                        null
                    }

                    val accounts = mutableListOf<AccountMatch>()
                    if (a is AccountsNode) {
                        if (balance != null && a.accounts.size == 1 && a.accounts[0].balance == null) {
                            val source = a.accounts[0]
                            accounts += AccountMatch(source.id, source.name, balance, source.selector)
                        } else {
                            accounts += a.accounts
                        }
                    }
                    if (b is AccountsNode) {
                        if (balance != null && b.accounts.size == 1 && b.accounts[0].balance == null) {
                            val source = b.accounts[0]
                            accounts += AccountMatch(source.id, source.name, balance, source.selector)
                        } else {
                            accounts += b.accounts
                        }
                    }
                    AccountsNode(accounts, "")
                } else if (a is TextNode && b is TextNode) {
                    AccountsNode(listOf(AccountMatch(id = b.text, name = a.text, selector = "")), "")
                } else if (a is TextNode || b is TextNode) {
                    val otherNode: DomResult
                    val textNode: TextNode
                    if (a is TextNode) {
                        textNode = a
                        otherNode = b
                    } else {
                        textNode = b as TextNode
                        otherNode = a
                    }

                    if (otherNode is BalanceNode) {
                        val account = AccountMatch(name = textNode.text, balance = otherNode.text, selector = "")
                        AccountsNode(accounts = listOf(account), selector = "")
                    } else if (otherNode is CurrencyNode) {
                        if (textNode.text.contains("balance", true)) {
                            BalanceNode(
                                text = otherNode.text,
                                isCurrent = textNode.text.contains("current", true),
                                selector = ""
                            )
                        } else {
                            val account = AccountMatch(name = textNode.text, balance = otherNode.text, selector = "")
                            AccountsNode(accounts = listOf(account), selector = "")
                        }
                    } else {
                        DomNotFound
                    }
                } else if (a is BalanceNode && b is BalanceNode) {
                    if (a.isCurrent) a else b
                } else {
                    DomNotFound
                }
            }
    }

    override fun visitText(el: Node): DomResult {
        val text = el.textContent
        return if (text.isNullOrBlank()) {
            DomNotFound
        } else if (currencyRegex.test(text)) {
            CurrencyNode(text.trim(), "")
        } else {
            TextNode(text.trim(), "")
        }
    }

    override fun visitUnknown(el: Node): DomResult = DomNotFound
}
