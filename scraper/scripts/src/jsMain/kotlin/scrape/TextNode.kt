package scrape

open class TextNode(val value: String, override val selector: String) : ScrapeNode {

    override val inputs = emptyList<ScrapeNode>()

    override fun <R> accept(visitor: ScrapeVisitor<R>) = visitor.visitText(this)
}
