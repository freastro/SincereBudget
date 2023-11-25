package scrape

open class ObjectNode<T>(val value: T, override val selector: String) : ScrapeNode {

    override val inputs = emptyList<ScrapeNode>()

    override fun <R> accept(visitor: ScrapeVisitor<R>) = visitor.visitObject(this)
}
