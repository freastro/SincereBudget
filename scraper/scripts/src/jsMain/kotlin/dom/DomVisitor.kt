package dom

import org.w3c.dom.Node

interface DomVisitor<R> {

    fun visit(el: Node): R =
        when (el.nodeType) {
            Node.ELEMENT_NODE -> visitElement(el)
            Node.TEXT_NODE -> visitText(el)
            else -> visitUnknown(el)
        }

    fun visitElement(el: Node): R = visitUnknown(el)
    fun visitText(el: Node): R = visitUnknown(el)
    fun visitUnknown(el: Node): R = TODO("Not implemented")
}
