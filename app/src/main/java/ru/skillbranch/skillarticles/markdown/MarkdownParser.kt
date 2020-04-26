package ru.skillbranch.skillarticles.markdown

import java.util.regex.Pattern

object MarkdownParser {

    private val LINE_SEPARATOR = "\n"//System.getProperty("line.separator") ?:

    //group regex
    private const val UNORDERED_LIST_ITEM_GROUP = "(^[*+-] .+)"
    private const val HEADER_GROUP = "(^#{1,6} .+?$)"
    private const val QUOTE_GROUP = "(^[>] .+)"
    private const val ITALIC_GROUP = "((?<!\\*)\\*[^*].*?[^*]?\\*(?!\\*)|(?<!_)_[^_].*?[^_]?_(?!_))"
    private const val BOLD_GROUP =
        "((?<!\\*)\\*{2}[^*].*?[^*]?\\*{2}(?!\\*)|(?<!_)_{2}[^_].*?[^_]?_{2}(?!_))"
    private const val STRIKE_GROUP = "((?<!~)~{2}[^~].*?[^~]?~{2}(?!~))"
    private const val RULE_GROUP = "(^[-_*]{3}$)"
    private const val INLINE_GROUP = "((?<!`)`[^`\\s].*?[^`\\s]?`(?!`))"
    private const val LINK_GROUP = "(\\[[^\\[\\]]*?]\\(.+?\\)|^\\[*?]\\(.+?\\))"
    private const val ORDER_LIST_GROUP = "(^\\d{1,2}\\.\\s.+?$)"
    private const val BLOCK_CODE_GROUP = "(^```(\\s|\\S)+?```$)"


    //result regex
    private const val MARKDOWN_GROUPS = "$UNORDERED_LIST_ITEM_GROUP|$HEADER_GROUP|$QUOTE_GROUP" +
            "|$ITALIC_GROUP|$BOLD_GROUP|$STRIKE_GROUP|$RULE_GROUP|$INLINE_GROUP|$LINK_GROUP"+
    "|$ORDER_LIST_GROUP|$BLOCK_CODE_GROUP"

    private val elementsPattern by lazy { Pattern.compile(MARKDOWN_GROUPS, Pattern.MULTILINE) }

    /**
     * parse markdown text to elements
     */
    fun parse(string: String): MarkdownText {
        val elements = mutableListOf<Element>()
        elements.addAll(findElements(string))
        return MarkdownText(elements)
    }

    /**
     * clear markdown text to string without markdown characters
    MarkdownParser.clear()
    Необходимо реализовать метод MarkdownParser.clear(text) очищающий markdown текст от markdown разметки
    +1
    Реализуй метод MarkdownParser.clear(string:String?) принимающий в качестве аргумента строку
    содержащую markdown разметку и возвращающую строку без markdown символов или null (если исходная строка null)


    Пример:
    before header text
    # Header1
    ---
    before `text
    after` text **bold**

    Результат:
    before header text
    Header1

    before `text
    after` text bold
     */

    fun getTextFromElements(list: List<Element>): String {
        var result = ""
        list.forEach {
            if (it.elements.isNotEmpty()) {
                //println("it.elements.isNotEmpty() >> ${it}")
                it.elements.forEach {
                    //println("it.elements.forEach find >> ${it}")
                    if(it.elements.isEmpty())
                        result += "${(it as Element).text}"
                    else result += getTextFromElements((it as Element).elements)
                }
            } else {
                //println("text find >> ${it}")
                result += "${(it as Element).text}"
            }
        }
        return result
    }


    fun clear(string: String?): String? {
        string ?: return null
        var clearString = ""
        val result = MarkdownParser.parse(string)
        //val actual = prepare<Element.UnorderedListItem>(result.elements)
        clearString = getTextFromElements(result.elements)

        return clearString as String
    }

    /**
     * find markdown elements in markdown text
     */
    private fun findElements(string: CharSequence): List<Element> {
        val parents = mutableListOf<Element>()
        val matcher = elementsPattern.matcher(string)
        var lastStartIndex = 0

        loop@ while (matcher.find(lastStartIndex)) {
            val startIndex = matcher.start()
            val endIndex = matcher.end()

            if (lastStartIndex < startIndex) {
                parents.add(Element.Text(string.subSequence(lastStartIndex, startIndex)))
            }

            var text: CharSequence
            val groups = 1..11
            var group = -1
            for (gr in groups) {
                if (matcher.group(gr) != null) {
                    group = gr
                    break
                }
            }
            when (group) {
                //NOT FOUND -> BREAK
                -1 -> break@loop

                //UNORDERED LIST
                1 -> {
                    //text without "*. "
                    text = string.subSequence(startIndex.plus(2), endIndex)
                    //find inner elements
                    val subs = findElements(text)
                    val element = Element.UnorderedListItem(text, subs)
                    parents.add(element)

                    //next find start from position "endIndex" (last regexp character)
                    lastStartIndex = endIndex
                }
                //HEADER
                2 -> {
                    val reg = "^#{1,6}".toRegex().find(string.subSequence(startIndex, endIndex))
                    val level = reg!!.value.length
                    //text without "{#} "
                    text = string.subSequence(startIndex.plus(level.inc()), endIndex)
                    val element = Element.Header(level, text)
                    parents.add(element)
                    lastStartIndex = endIndex
                }
                //QUOTE
                3 -> {
                    //text without "> "
                    text = string.subSequence(startIndex.plus(2), endIndex)
                    //find inner elements
                    val subs = findElements(text)
                    val element = Element.Quote(text, subs)
                    parents.add(element)
                    //next find start from position "endIndex" (last regexp character)
                    lastStartIndex = endIndex
                }
                //ITALIC
                4 -> {
                    //text without "*{}*"
                    text = string.subSequence(startIndex.inc(), endIndex.dec())
                    //find inner elements
                    val subs = findElements(text)
                    val element = Element.Italic(text, subs)
                    parents.add(element)
                    //next find start from position "endIndex" (last regexp character)
                    lastStartIndex = endIndex
                }
                //BOLD
                5 -> {
                    //text without "**{}**"
                    text = string.subSequence(startIndex.inc().inc(), endIndex.dec().dec())
                    //find inner elements
                    val subs = findElements(text)
                    val element = Element.Bold(text, subs)
                    parents.add(element)
                    //next find start from position "endIndex" (last regexp character)
                    lastStartIndex = endIndex
                }
                //UNDERSCORE
                6 -> {
                    //text without "~~{}~~"
                    text = string.subSequence(startIndex.inc().inc(), endIndex.dec().dec())
                    //find inner elements
                    val subs = findElements(text)
                    val element = Element.Strike(text, subs)
                    parents.add(element)
                    //next find start from position "endIndex" (last regexp character)
                    lastStartIndex = endIndex
                }
                //RULE
                7 -> {
                    //text without "***" insert empty character
                    val element = Element.Rule()
                    parents.add(element)
                    //next find start from position "endIndex" (last regexp character)
                    lastStartIndex = endIndex
                }
                //INLINE
                8 -> {
                    //text without "`{}`"
                    text = string.subSequence(startIndex.inc(), endIndex.dec())
                    //find inner elements
                    val element = Element.InlineCode(text)
                    parents.add(element)
                    //next find start from position "endIndex" (last regexp character)
                    lastStartIndex = endIndex
                }
                //LINK
                9 -> {
                    //full text for regex
                    text = string.subSequence(startIndex, endIndex)
                    val (title: String, link: String) = "\\[(.*)]\\((.*)\\)".toRegex().find(text)!!.destructured
                    //find inner elements
                    val element = Element.Link(link, title)
                    parents.add(element)
                    //next find start from position "endIndex" (last regexp character)
                    lastStartIndex = endIndex
                }
                //ORDERED LIST
                10 -> {
                    //text without "\\d{1,2}. "
                    var reg = "(^\\d{1,2}.)".toRegex().find(string.substring(startIndex, endIndex))
                    val order = reg!!.value
                    text = string.subSequence(startIndex.plus(order.length.inc()), endIndex).toString()

                    //find inner elements
                    val subs = findElements(text)
                    val element = Element.OrderedListItem(order, text, subs)
                    parents.add(element)

                    //next find start from position "endIndex" (last regexp character)
                    lastStartIndex = endIndex
                }
                //BLOCKCODE
                11 -> {
                    //text without "```{}```"
                    text = string.subSequence(startIndex.plus(3), endIndex.plus(-3)).toString()

                    if (text.contains(LINE_SEPARATOR)){
                        for ((index, line) in text.lines().withIndex()){
                            when(index){
                                text.lines().lastIndex -> parents.add(
                                    Element.BlockCode(
                                        Element.BlockCode.Type.END,
                                        line
                                    )
                                )
                                0 -> parents.add(
                                    Element.BlockCode(
                                        Element.BlockCode.Type.START,
                                        line + LINE_SEPARATOR
                                    )
                                )
                                else -> parents.add(
                                    Element.BlockCode(
                                        Element.BlockCode.Type.MIDDLE,
                                        line + LINE_SEPARATOR
                                    )
                                )
                            }
                        }
                    } else parents.add(Element.BlockCode(Element.BlockCode.Type.SINGLE, text))
                    lastStartIndex = endIndex
                }
            }
        }

        if (lastStartIndex < string.length) {
            val text = string.subSequence(lastStartIndex, string.length)
            parents.add(Element.Text(text))
        }
/*
        loop@ while () {
            //TODO implement me
            //groups range for iterate by groups (1..9) or (1..11) optionally
            val groups = 1..11
            when () {
                //NOT FOUND -> BREAK
                -1 -> break@loop

                //UNORDERED LIST
                1 -> {
                    //text without "*. "
                    //TODO implement me
                }

                //HEADER
                2 -> {
                    //text without "{#} "
                    //TODO implement me
                }

                //QUOTE
                3 -> {
                    //text without "> "
                    //TODO implement me
                }

                //ITALIC
                4 -> {
                    //text without "*{}*"
                    //TODO implement me
                }

                //BOLD
                5 -> {
                    //text without "**{}**"
                    //TODO implement me
                }

                //STRIKE
                6 -> {
                    //text without "~~{}~~"
                    //TODO implement me
                }

                //RULE
                7 -> {
                    //text without "***" insert empty character
                    //TODO implement me
                }

                //RULE
                8 -> {
                    //text without "`{}`"
                    //TODO implement me
                }

                //LINK
                9 -> {
                    //full text for regex
                    //TODO implement me
                }
                //10 -> BLOCK CODE - optionally
                10 -> {
                    //TODO implement me
                }

                //11 -> NUMERIC LIST
                11 -> {
                    //TODO implement me
                }
            }

        }
*/
        return parents
    }
}

data class MarkdownText(val elements: List<Element>)

sealed class Element() {
    abstract val text: CharSequence
    abstract val elements: List<Element>

    data class Text(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class UnorderedListItem(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Header(
        val level: Int = 1,
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Quote(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Italic(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Bold(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Strike(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Rule(
        override val text: CharSequence = " ", //for insert span
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class InlineCode(
        override val text: CharSequence, //for insert span
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Link(
        val link: String,
        override val text: CharSequence, //for insert span
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class OrderedListItem(
        val order: String,
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class BlockCode(
        val type: Type = Type.MIDDLE,
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element() {
        enum class Type { START, END, MIDDLE, SINGLE }
    }
}