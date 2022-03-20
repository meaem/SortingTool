package sorting

import java.io.File
import java.io.FileNotFoundException
import java.io.PrintStream
import java.util.*

private const val NATURAL = "natural"
private const val SORTING_TYPE = "-sortingType"
private const val INPUT_FILE = "-inputFile"
private const val OUT_FILE = "-outputFile"
private const val BY_COUNT = "byCount"
private const val DATA_TYPE = "-dataType"
private const val WORD = "word"
private const val LINE = "line"
private const val LONG = "long"

private val SPACES_REGEX = "\\s+".toRegex()

open class Token(val value: String) : Comparable<Token> {
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

class Printer(val file: File?) {
    val printStream = if (file == null) System.out else {
//        println(file.absoluteFile)
        PrintStream(file)
    }

    constructor() : this(null)

    fun print(msg: String) {
        printStream.print(msg)
    }

    fun println(msg: String) {
        printStream.println(msg)
    }

    fun close() {
        printStream.close()

    }

}

fun getDataType(args: Array<String>): Pair<String, String> {


    var idx = args.indexOf(SORTING_TYPE)
    val sortBy = if (idx == -1) {
        NATURAL
    } else if (idx == args.lastIndex || args[idx + 1].startsWith("-")) {
        throw IllegalArgumentException("No sorting type defined!")
    } else if (args[idx + 1] in listOf(NATURAL, BY_COUNT)) {
        args[idx + 1]
    } else // -sortingType is found but followed by anything other than "natural" or"byCount"
        throw IllegalArgumentException("invalid sorting type!! only \"natural\" or\"byCount\"")

    idx = args.indexOf(DATA_TYPE)
    val dataType = if (idx == -1) {
        WORD
    } else if (idx == args.lastIndex || args[idx + 1].startsWith("-")) {
        throw IllegalArgumentException("No data type defined!")
    } else if (args[idx + 1] in listOf(WORD, LINE, LONG)) {
        args[idx + 1]
    } else // -sortingType is found but followed by anything other than "natural" or"byCount"
        throw IllegalArgumentException("invalid data type!! only \"$WORD\" or\"$LINE\" or \"$LONG\"")

    return dataType to sortBy
}


fun main(args: Array<String>) {
    try {
        val agruments = getDataType(args)
        val inFile = getInFile(args)
        val outFile = getOutFile(args)
        val xfile = Printer(File("log.txt"))
        xfile.println(args.joinToString(" "))
        xfile.println(outFile?.absolutePath ?: "System out")
        val sc = if (inFile == null) Scanner(System.`in`) else Scanner(inFile)
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
        inFile?.let {
            sc.close()
        }

        if (agruments.second == NATURAL) {
            list.sortBy { it.longValue() }
        }
        printFormatedOutput(list, agruments, outFile)
    } catch (ex: IllegalArgumentException) {
        println("invalid arguments")
    }
        catch (ex: FileNotFoundException) {
        println("file not found\n${ex.message}")
    }
}

fun getOutFile(args: Array<String>): File? {
    val idx = args.indexOf(OUT_FILE)
    return if (idx != -1) {
        File(args[idx + 1])
    } else {
        null
    }
}

fun getInFile(args: Array<String>): File? {
    val idx = args.indexOf(INPUT_FILE)
    return if (idx != -1) {
        File(args[idx + 1])
    } else {
        null
    }
}

fun printFormatedOutput(list: List<Token>, dataType: Pair<String, String>, outFile: File?) {
//    val maxN = list.maxByOrNull { it.longValue() }!!
//    val formatter =

    val printer = Printer(outFile)
    if (dataType.second == BY_COUNT) {

        if (dataType.first == WORD) {
            printer.println("Total words: ${list.size}.")
//            println("The longest word: ${maxN.value} ($repeatitions time(s), $prct%).")
        } else if (dataType.first == LINE) {
            printer.println("Total lines: ${list.size}.")
//            println("The longest line:\n${maxN.value}\n($repeatitions time(s), $prct%).")
        } else if (dataType.first == LONG) {
            printer.println("Total numbers: ${list.size}.")
//            println("The greatest number: ${maxN.value} ($repeatitions time(s), $prct%).")
        }
        val dataMap = list.groupBy { it }
            .map { it.key to it.value }
            .sortedBy { it.first }
            .sortedBy { it.second.size }
        for ((k, v) in dataMap) {
            val repeatitions = v.size
            val prct = repeatitions * 100 / list.size
            printer.println("${k.value}: $repeatitions time(s), $prct%")
        }

    } else {
        printer.println("Total ${dataType.first}s: ${list.size}.")
        printer.println("Sorted data:${list.map { it.longValue() }.joinToString(" ")}")
    }
    outFile?.let {
        printer.close()
    }

}
