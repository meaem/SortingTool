package sorting

import java.util.*

open class Token(val value: String) {
    open fun longValue() = value.length.toLong()
}

class IntToken(value: String) : Token(value) {
    override fun longValue() = value.toLong()
}

fun getDataType(args: Array<String>): Pair<String, Boolean> {
    if ("-sortIntegers" in args) {
        return "long" to true
    }
    return if (args.size >= 2 && args[0] == "-dataType") args[1] to false else "word" to false
}

fun main(args: Array<String>) {

    val dataType = getDataType(args)
    val sc = Scanner(System.`in`)
    val list = mutableListOf<Token>()
    while (sc.hasNext()) {
        if (dataType.first == "line") {
            val line = sc.nextLine()
            list.add(Token(line))
        } else if (dataType.first == "word") {
            val lineSegments = sc.nextLine().split("\\s+".toRegex())
            list.addAll(lineSegments.map { Token(it) })
        } else if (dataType.first == "long") {
            val lineSegments = sc.nextLine().split("\\s+".toRegex())
            list.addAll(lineSegments.map { IntToken(it) })
        }
    }
    if (dataType.second) {
        list.sortBy { it.longValue() }
    }
    printFormatedOutput(list, dataType)
}

fun printFormatedOutput(list: List<Token>, dataType: Pair<String, Boolean>) {
    val maxN = list.maxByOrNull { it.longValue() }!!
    val repeatitions = list.filter { it.longValue() == maxN.longValue() }.size
    val prct = repeatitions * 100 / list.size
    if(!dataType.second) {
        if (dataType.first == "word") {
            println("Total words: ${list.size}.")
            println("The longest word: ${maxN.value} ($repeatitions time(s), $prct%).")
        } else if (dataType.first == "line") {
            println("Total lines: ${list.size}.")
            println("The longest line:\n${maxN.value}\n($repeatitions time(s), $prct%).")
        } else if (dataType.first == "long") {
            println("Total numbers: ${list.size}.")
            println("The greatest number: ${maxN.value} ($repeatitions time(s), $prct%).")
        }

    }else{
        println("Total numbers: ${list.size}.")
        println("Sorted data:${list.map { it.longValue() }. joinToString (" ")}")
    }

}
