package sorting

import java.util.*

private const val NATURAL = "natural"
private const val SORTING_TYPE = "-sortingType"
private const val BY_COUNT = "byCount"
private const val DATA_TYPE = "-dataType"
private const val WORD = "word"
private const val LINE = "line"
private const val LONG = "long"

private val SPACES_REGEX = "\\s+".toRegex()

open class Token(val value: String) :Comparable<Token>{
    open fun longValue() = value.length.toLong()
    override fun compareTo(other: Token): Int {
        return this.value.compareTo(other.value)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Token

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }


}

class IntToken(value: String) : Token(value) {
    override fun longValue() = value.toLong()
    override fun compareTo(other: Token): Int {
        return longValue().compareTo(other.longValue())
    }
}


fun getDataType(args: Array<String>): Pair<String, String> {


    var idx = args.indexOf(SORTING_TYPE)
    val sortBy = if (idx == -1 ) {
        NATURAL
    }
    else if(idx == args.lastIndex || args[idx + 1].startsWith("-")){
        throw IllegalArgumentException("No sorting type defined!")
    }
    else if (args[idx + 1] in listOf(NATURAL, BY_COUNT)) {
        args[idx + 1]
    } else // -sortingType is found but followed by anything other than "natural" or"byCount"
        throw IllegalArgumentException("invalid sorting type!! only \"natural\" or\"byCount\"")

    idx = args.indexOf(DATA_TYPE)
    val dataType = if (idx == -1 ) {
        WORD
    }
    else if (idx == args.lastIndex || args[idx + 1].startsWith("-")){
        throw IllegalArgumentException("No data type defined!")
    }
    else if (args[idx + 1] in listOf(WORD, LINE, LONG)) {
        args[idx + 1]
    } else // -sortingType is found but followed by anything other than "natural" or"byCount"
        throw IllegalArgumentException("invalid data type!! only \"$WORD\" or\"$LINE\" or \"$LONG\"")

    return dataType to sortBy
}


fun main(args: Array<String>) {
    try {
        val agruments = getDataType(args)
        val sc = Scanner(System.`in`)
        val list = mutableListOf<Token>()
        while (sc.hasNext()) {
            if (agruments.first == LINE) {
                val line = sc.nextLine()
                list.add(Token(line))
            } else if (agruments.first == WORD) {
                val lineSegments = sc.nextLine().split(SPACES_REGEX)
                list.addAll(lineSegments.map { Token(it) })
            } else if (agruments.first == LONG) {
                val lineSegments = sc.nextLine().split(SPACES_REGEX)
                list.addAll(lineSegments.map { IntToken(it) })
            }
        }
        if (agruments.second == NATURAL) {
            list.sortBy { it.longValue() }
        }
        printFormatedOutput(list, agruments)
    } catch (ex: IllegalArgumentException) {
        println("invalid arguments")
    }
}

fun printFormatedOutput(list: List<Token>, dataType: Pair<String, String>) {
//    val maxN = list.maxByOrNull { it.longValue() }!!

    if (dataType.second == BY_COUNT) {

        if (dataType.first == WORD) {
            println("Total words: ${list.size}.")
//            println("The longest word: ${maxN.value} ($repeatitions time(s), $prct%).")
        } else if (dataType.first == LINE) {
            println("Total lines: ${list.size}.")
//            println("The longest line:\n${maxN.value}\n($repeatitions time(s), $prct%).")
        } else if (dataType.first == LONG) {
            println("Total numbers: ${list.size}.")
//            println("The greatest number: ${maxN.value} ($repeatitions time(s), $prct%).")
        }
        val dataMap = list.groupBy { it }
            .map { it.key to it.value }
            .sortedBy { it.first }
            .sortedBy { it.second.size }
        for ((k, v) in dataMap) {
            val repeatitions = v.size
            val prct = repeatitions * 100 / list.size
            println("${k.value}: $repeatitions time(s), $prct%")
        }

    } else {
        println("Total ${dataType.first}s: ${list.size}.")
        println("Sorted data:${list.map { it.longValue() }.joinToString(" ")}")
    }

}
