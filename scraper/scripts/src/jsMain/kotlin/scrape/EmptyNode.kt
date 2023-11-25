package scrape

object EmptyNode : ScrapeNode {

    override val inputs = emptyList<ScrapeNode>()

    override val selector = ""

    override fun <R> accept(visitor: ScrapeVisitor<R>) = visitor.visitEmpty()
}
