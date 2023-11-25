package dom

import org.w3c.dom.Node
import org.w3c.dom.asList

abstract class DomWalker<R> : DomVisitor<R> {

    override fun visitElement(el: Node, selector: String) = visitUnknown(el, selector)

    override fun visitList(list: List<Node>, selector: String) =
        list.mapIndexed { index, node ->
            when (node.nodeType) {
                Node.ELEMENT_NODE -> {
                    val id = node.childNodes.asList()
                        .filter { it.nodeType == Node.ATTRIBUTE_NODE }
                        .filter { it.nodeName == "id" }
                        .map { it.nodeValue }
                        .firstOrNull()
                    val childSelector = if (id != null) "#${id}" else "${selector}>:nth-child(${index})"
                    visitElement(node, childSelector)
                }
                Node.TEXT_NODE -> visitText(node, selector)
                else -> visitUnknown(node, selector)
            }
        }

    override fun visitText(el: Node, selector: String) = visitUnknown(el, selector)

    override fun visitUnknown(el: Node, selector: String): R = throw Exception("Not implemented")
}
