package dom

import org.w3c.dom.Node

interface DomVisitor<R> {
    fun visitElement(el: Node, selector: String): R
    fun visitList(list: List<Node>, selector: String): List<R>
    fun visitText(el: Node, selector: String): R
    fun visitUnknown(el: Node, selector: String): R
}
