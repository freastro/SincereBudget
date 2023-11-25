package scrape

interface ScrapeVisitor<R> {
    fun visitEmpty(): R

    fun visitList(node: ListNode): R

    fun visitObject(node: ObjectNode<*>): R

    fun visitText(node: TextNode): R
}
