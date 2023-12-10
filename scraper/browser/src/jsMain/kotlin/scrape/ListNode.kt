package scrape

open class ListNode(override val inputs: List<ScrapeNode>, override val selector: String) : ScrapeNode {

    override fun <R> accept(visitor: ScrapeVisitor<R>) = visitor.visitList(this)
    override fun toString() = "ListNode(selector=$selector)"
}
