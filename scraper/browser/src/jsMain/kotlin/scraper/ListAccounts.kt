package scraper

import com.sincerepost.scraper.AccountMatch
import com.sincerepost.scraper.ScrapeConfig
import dom.DomWalker
import org.w3c.dom.Node
import org.w3c.dom.asList
import scrape.EmptyNode
import scrape.ObjectNode
import scrape.ScrapeNode
import scrape.TextNode
import kotlin.js.RegExp

/**
 * Extracts a list of accounts.
 */
class ListAccounts(config: ScrapeConfig) : Scraper<List<AccountMatch>>(config) {

    /** Node for a list of accounts. */
    class AccountsNode(accounts: List<AccountMatch>, selector: String) :
        ObjectNode<List<AccountMatch>>(accounts, selector) {
        override fun toString() = "AccountsNode(accounts=${value}, selector=${selector})"
    }
    /** Node for an account balance. */
    class BalanceNode(value: String, val isCurrent: Boolean, selector: String) : TextNode(value, selector) {
        override fun toString() = "BalanceNode(value=${value}, isCurrent=${isCurrent}, selector=${selector})"
    }
    /** Node for a currency value. */
    class CurrencyNode(value: String, selector: String) : TextNode(value, selector) {
        override fun toString() = "CurrencyNode(value=${value}, selector=${selector})"
    }
    /** Node for a non-balance, non-currency text value. */
    class OtherTextNode(value: String, selector: String) : TextNode(value, selector) {
        override fun toString() = "OtherTextNode(value=${value}, selector=${selector})"
    }

    /**
     * Walk the DOM tree converting DOM nodes to `ScrapeNode` instances.
     */
    inner class DomScraper : DomWalker<ScrapeNode>() {

        override fun visitElement(el: Node, selector: String): ScrapeNode {
            var n = 1
            var result: ScrapeNode? = null
            val childNodes = el.childNodes.asList()
            console.log("${selector}: ${childNodes.map { it.nodeName }}")
            for (node in childNodes) {
                val b = if (node.nodeType == Node.ELEMENT_NODE) {
                    if (node.nodeName != "SCRIPT" && node.nodeName != "STYLE") {
                        visitElement(node, "${selector}>:nth-child(${n++})")
                    } else {
                        n++
                        EmptyNode
                    }
                } else if (node.nodeType == Node.TEXT_NODE) {
                    visitText(node, selector)
                } else {
                    EmptyNode
                }
                if (b is EmptyNode) {
                    continue
                }
                if (result == null) {
                    result = b
                    continue
                }

                val a = result
                result = if (a is AccountsNode || b is AccountsNode) {
                    val balance = if (a is BalanceNode) {
                        a.value
                    } else if (a is CurrencyNode) {
                        a.value
                    } else if (b is BalanceNode) {
                        b.value
                    } else if (b is CurrencyNode) {
                        b.value
                    } else {
                        null
                    }

                    val accounts = mutableListOf<AccountMatch>()
                    if (a is AccountsNode) {
                        if (balance != null && a.value.size == 1 && a.value[0].balance == null) {
                            val source = a.value[0]
                            accounts += AccountMatch(source.id, source.name, balance, source.selector)
                        } else {
                            accounts += a.value
                        }
                    }
                    if (b is AccountsNode) {
                        if (balance != null && b.value.size == 1 && b.value[0].balance == null) {
                            val source = b.value[0]
                            accounts += AccountMatch(source.id, source.name, balance, source.selector)
                        } else {
                            accounts += b.value
                        }
                    }
                    AccountsNode(accounts, selector)
                } else if (a is OtherTextNode && b is OtherTextNode) {
                    val account = AccountsNode(
                        listOf(AccountMatch(id = b.value, name = a.value, selector = a.selector)),
                        selector)
                    account
                } else if (a is OtherTextNode || b is OtherTextNode) {
                    val otherNode: ScrapeNode
                    val textNode: OtherTextNode
                    if (a is OtherTextNode) {
                        textNode = a
                        otherNode = b
                    } else {
                        textNode = b as OtherTextNode
                        otherNode = a
                    }

                    if (otherNode is BalanceNode) {
                        val account = AccountMatch(
                            name = textNode.value, balance = otherNode.value,
                            selector = textNode.selector
                        )
                        AccountsNode(accounts = listOf(account), selector = selector)
                    } else if (otherNode is CurrencyNode) {
                        if (textNode.value.contains("balance", true)) {
                            BalanceNode(
                                value = otherNode.value,
                                isCurrent = textNode.value.contains("current", true),
                                selector = otherNode.value
                            )
                        } else {
                            val account = AccountMatch(
                                name = textNode.value, balance = otherNode.value,
                                selector = textNode.value
                            )
                            AccountsNode(accounts = listOf(account), selector = selector)
                        }
                    } else {
                        EmptyNode
                    }
                } else if (a is BalanceNode && b is BalanceNode) {
                    if (a.isCurrent) a else b
                } else {
                    EmptyNode
                }
            }
            console.log("${selector}=${if (result == null || result is EmptyNode) "EmptyNode" else result}")
            return result ?: EmptyNode
        }

        override fun visitText(el: Node, selector: String): ScrapeNode {
            val text = el.textContent
            return if (text.isNullOrBlank()) {
                EmptyNode
            } else if (currencyRegex.test(text)) {
                CurrencyNode(text.trim(), selector)
            } else {
                OtherTextNode(text.trim(), selector)
            }
        }

        override fun visitUnknown(el: Node, selector: String) = EmptyNode
    }

    private val currencyRegex = RegExp(config.currencyRegex)

    override fun scrape(start: Node, selector: String): Result<List<AccountMatch>> {
        val node = DomScraper().visitElement(start, selector)
        return if (node is AccountsNode) {
            Result.success(node.value.filter { it.balance != null })
        } else {
            Result.success(emptyList())
        }
    }
}
