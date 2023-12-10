package scrape

interface ScrapeNode {

    val inputs: List<ScrapeNode>

    val selector: String

    fun <R> accept(visitor: ScrapeVisitor<R>): R
}
