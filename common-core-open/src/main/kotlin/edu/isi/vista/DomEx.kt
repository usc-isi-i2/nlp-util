package edu.isi.vista

import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.StringReader
import java.util.*
import javax.xml.parsers.DocumentBuilder


fun Element.childElement(tag: String): Element? {
    val childrenWithRequestedTag = childElements(tag).toImmutableList()

    return when (childrenWithRequestedTag.size) {
        0 -> return null
        1 -> childrenWithRequestedTag[0]
        else -> throw RuntimeException("Expected a single element of tag $tag but got ${childrenWithRequestedTag.size}")
    }
}

fun Element.requiredChildElement(tag: String): Element {
    val childrenWithRequestedTag = childElements(tag).toImmutableList()

    return when (childrenWithRequestedTag.size) {
        0 -> throw RuntimeException("Expected a child element of tag $tag but found none. Available children: " +
                "${childElements().map { it.tagName }.toList()}")
        1 -> childrenWithRequestedTag[0]
        else -> throw RuntimeException("Expected a single element of tag $tag but got ${childrenWithRequestedTag.size}")
    }
}

fun Element.childElements(): Sequence<Element> = this.childNodes.asSequence().filterIsInstance(Element::class.java)
fun Element.childElements(tag: String): Sequence<Element> {
    val normalizedTag = tag.toLowerCase(Locale.ENGLISH)

    return childElements().filter { it.tagName.toLowerCase(Locale.ENGLISH) == normalizedTag }
}

fun Element.childElements(tags: Set<String>): Sequence<Element> {
    val normalizedTags = tags.map { it.toLowerCase(Locale.ENGLISH) }.toSet()

    return childElements().filter { it.tagName.toLowerCase(Locale.ENGLISH) in normalizedTags }
}

fun <T> Element.requiredChildElement(tag: String, parseFunction: (Element) -> T): T = parseFunction(requiredChildElement(tag))

fun <T> Element.childElement(tag: String, parseFunction: (Element) -> T) = childElement(tag)?.let(parseFunction)

fun <T> Element.childElements(tags: Set<String>, parseFunction: (Element) -> T) = this.childElements(tags).map(parseFunction).toImmutableList()

fun <T> Element.childElements(tag: String, parseFunction: (Element) -> T) = this.childElements(tag).map(parseFunction).toImmutableList()

fun Element.attribute(attributeName: String): String? {
    return if (this.hasAttribute(attributeName)) {
        this.getAttribute(attributeName)
    } else {
        null
    }
}

fun <T> Element.attribute(attributeName: String, parseFunction: (String) -> T): T? = attribute(attributeName)?.let(parseFunction)


fun Element.textOfChild(tag: String): String? = childElement(tag)?.textContent

fun NodeList.asSequence(): Sequence<Node> {
    val nodeList = this
    return sequence {
        for (i in 0..nodeList.length) {
            yield(nodeList.item(i))
        }
    }
}

fun DocumentBuilder.doNotLoadDtd(): DocumentBuilder {
    // see https://stackoverflow.com/a/155353/413345
    this.setEntityResolver { publicId, systemId ->
        if (systemId.contains(".dtd")) {
            InputSource(StringReader(""))
        } else {
            null
        }
    }
    return this
}
