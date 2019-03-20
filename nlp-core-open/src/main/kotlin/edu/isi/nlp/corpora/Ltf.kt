package edu.isi.nlp.corpora

import com.google.common.collect.ImmutableList
import com.google.common.io.CharSource
import edu.isi.nlp.IntCounter
import edu.isi.nlp.strings.LocatedString
import edu.isi.vista.*
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.xml.sax.InputSource
import java.io.IOException
import java.security.MessageDigest
import java.util.*
import javax.xml.bind.annotation.adapters.HexBinaryAdapter
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.math.max

/**
 * A collection of LTF documents.
 *
 * Usually LDC LTF files contain only a single document, but the format supports several.
 */
data class LctlText(val documents: ImmutableList<LtfDocument>,
                    val lang: String? = null, val sourceFile: String? = null, val sourceType: String? = null,
                    val author: String? = null, val encoding: String? = null)

/**
 * The content of an LDC LTF document.
 *
 * Please see the LDC's LTF documentation for details.
 */
data class LtfDocument(val text: LtfDocumentText, val lang: String? = null, val tokenization: String? = null,
                       val grammar: String? = null, val rawTextCharLength: Int? = null,
                       val rawTextMd5: String? = null, val headline: String? = null,
                       val dateline: String? = null, val authorLine: String? = null) {

    val originalText by lazy<LocatedString> {
        val ret = StringBuilder()
        val lastOffset = IntCounter.of(0)

        toOriginalText(ret, lastOffset)

        val originalText = ret.toString()

        val messageDigester = MessageDigest.getInstance("MD5")
        messageDigester.update(originalText.toByteArray())
        val md5sumOfReconstructedText = HexBinaryAdapter().marshal(messageDigester.digest())
                .toLowerCase(Locale.ENGLISH)

        if (md5sumOfReconstructedText != rawTextMd5) {
            throw IOException("MD5 sum of reconstructed raw text $md5sumOfReconstructedText does not match" +
                    " checksum $rawTextMd5")
        }

        LocatedString.fromReferenceString(originalText)
    }

    val segments  = sequence {
        text.documentElements.forEach {
            val docElement = it
            @Suppress("USELESS_CAST")
            when (docElement) {
            // IntelliJ says the cast here is useless, and it ought to be, but IntelliJ also gives me red lines
            // if I remove it :-)
                is LtfSegment -> yield(docElement as LtfSegment)
                is LtfParagraph -> docElement.segments.forEach { yield(it) }
            }
        }
    }.toList()

    val tokens = text.documentElements.map {
        when (it) {
            is LtfParagraph -> it.segments.map { it.tokens }.flatten()
            is LtfSegment -> it.tokens
        }
    }.flatten()


    private fun toOriginalText(buffer: StringBuilder, lastOffset: IntCounter) {
        requireNotNull(rawTextCharLength) { "Cannot get document original text if raw text length not specified" }
        text.documentElements.forEach { toOriginalText(it, buffer, lastOffset) }
        // pick up any trailing new lines after the last segment
        padWithNewLines(buffer, rawTextCharLength!!, lastOffset)
    }

    private fun toOriginalText(documentElement: LtfDocumentElement, buffer: StringBuilder, lastOffset: IntCounter) {
        when (documentElement) {
            is LtfParagraph -> documentElement.segments.forEach { toOriginalText(it, buffer, lastOffset) }
            is LtfSegment -> {
                requireNotNull(documentElement.originalText) {
                    "Cannot get document original text " +
                            "when one or more segments lacks original text"
                }
                requireNotNull(documentElement.startOffset) {
                    "Cannot get document original text when one or more segments lacks offsets"
                }
                requireNotNull(documentElement.endOffset) {
                    "Cannot get document original text when one or more segments lacks offsets"
                }

                val unescapedOriginalText = documentElement.originalText!!
                        .replace("&lt;", "<")
                        .replace("&gt;", ">")
                        .replace("&amp;", "&")
                padWithNewLines(buffer, documentElement.startOffset!!, lastOffset)
                buffer.append(unescapedOriginalText)
                lastOffset.setValue(documentElement.endOffset!! + 1)
            }
        }
    }

    private fun padWithNewLines(buffer: StringBuilder, targetOffset: Int, lastOffset: IntCounter) {
        val newlinesNeeded = max(0, targetOffset - lastOffset.value())
        buffer.append("\n".repeat(newlinesNeeded))
    }
}


data class LtfDocumentText(val documentElements: ImmutableList<LtfDocumentElement>)

sealed class LtfDocumentElement

data class LtfParagraph(val segments: ImmutableList<LtfSegment>) : LtfDocumentElement()

data class LtfSegment(val tokens: ImmutableList<LtfToken> = ImmutableList.of(),
                      val originalText: String? = null,
                      val startToken: LtfToken? = null, val endToken: LtfToken? = null,
                      val startOffset: Int? = null, val endOffset: Int? = null) : LtfDocumentElement()

data class LtfToken(val content: String, val tokenAttachment: LtfTokenAttachment? = null,
                    val partOfSpeech: String? = null, val morphology: String? = null,
                    val gloss: String? = null, val startOffset: Int? = null,
                    val endOffset: Int? = null)

enum class LtfTokenAttachment { LEFT, RIGHT, BOTH }

/**
 * Loads LDC LTF files into {@link LctlText} objects.
 *
 * LTF is a format used to encodetext files together with sentence breaking and tokenization for
 * programs like LORELEI and AIDA.
 */
class LtfReader {
    fun read(inp: CharSource): LctlText {
        val xmlDoc: Document = inp.openStream().use {
            DocumentBuilderFactory.newInstance().newDocumentBuilder().doNotLoadDtd().parse(InputSource(it))
        }

        return LtfReading().parseLctlText(xmlDoc.documentElement)
    }

    private inner class LtfReading {
        var idToItemMap: MutableMap<String, LtfToken> = mutableMapOf()

        fun parseLctlText(root: Element) = LctlText(
                documents = root.childElements("DOC", this::parseDocument),
                lang = root.attribute("lang"),
                sourceFile = root.attribute("source_file"),
                sourceType = root.attribute("source_type"),
                author = root.attribute("author"),
                encoding = root.attribute("encoding"))

        private fun parseDocument(e: Element): LtfDocument = LtfDocument(
                text = e.requiredChildElement("text", this::parseText),
                lang = e.attribute("lang"),
                tokenization = e.attribute("tokenization"),
                grammar = e.attribute("grammar"),
                rawTextCharLength = e.attribute("raw_text_char_length")?.toInt(),
                rawTextMd5 = e.attribute("raw_text_md5"),
                headline = e.textOfChild("headline"),
                dateline = e.textOfChild("dateline"),
                authorLine = e.textOfChild("authorline"))


        private fun parseText(e: Element): LtfDocumentText = LtfDocumentText(
                documentElements = e.childElements(setOf("P", "SEG"), this::parseDocumentElement))

        private fun parseDocumentElement(e: Element): LtfDocumentElement = when (e.tagName.toLowerCase(Locale.ENGLISH)) {
            "p" -> parseParagraph(e)
            "seg" -> parseSegment(e)
            else -> throw RuntimeException("Can't happen")
        }

        private fun parseParagraph(e: Element): LtfDocumentElement = LtfParagraph(
                segments = e.childElements("seg", this::parseSegment))

        private fun parseSegment(e: Element): LtfSegment = LtfSegment(
                // tokens must be parsed before startToken/endToken lookup below
                tokens = e.childElements("token", this::parseToken),
                originalText = e.childElement("original_text", this::parseOriginalText),
                startToken = registeredToken(e.attribute("start_token")),
                endToken = registeredToken(e.attribute("end_token")),
                startOffset = e.attribute("start_char")?.toInt(),
                endOffset = e.attribute("end_char")?.toInt())

        private fun parseOriginalText(e: Element): String = e.textContent

        private fun parseToken(e: Element): LtfToken {
            val token = LtfToken(content = e.textContent,
                    tokenAttachment = e.attribute("attach", this::parseAttachment),
                    partOfSpeech = e.attribute("pos"), morphology = e.attribute("morph"),
                    gloss = e.attribute("gloss"),
                    startOffset = e.attribute("start_char")?.toInt(),
                    endOffset = e.attribute("end_char")?.toInt())
            val id = e.attribute("id")
                    ?: throw RuntimeException("Element ${e.tagName} missing required attribute 'id'")

            idToItemMap[id] = token
            return token
        }

        private fun parseAttachment(attachmentString: String): LtfTokenAttachment {
            return when (attachmentString.toLowerCase(Locale.ENGLISH)) {
                "left" -> LtfTokenAttachment.LEFT
                "right" -> LtfTokenAttachment.RIGHT
                "both" -> LtfTokenAttachment.BOTH
                else -> throw RuntimeException("Unknown token attachment string $attachmentString")
            }
        }

        private fun registeredToken(id: String?): LtfToken? = id?.let {
            idToItemMap[id] ?: throw RuntimeException("Unknown token $id")
        }
    }
}

