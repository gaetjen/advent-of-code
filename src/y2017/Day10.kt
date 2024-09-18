package y2017

import util.readInput
import util.timingStatistics
import kotlin.experimental.xor

object Day10 {
    private fun parse(input: List<String>): List<Int> {
        return input.first().split(",").map { it.toInt() }
    }

    fun part1(
        input: List<String>,
        length: Int
    ): Int {
        val lengths = parse(input)
        var list = List(length) { it }
        var position = 0
        for ((skipSize, l) in lengths.withIndex()) {
            list = twist(list, l, position)
            position = (position + l + skipSize) % length
        }
        return list[0] * list[1]
    }

    private fun <T> twist(
        list: List<T>,
        length: Int,
        start: Int
    ): List<T> {
        val elements = ((list + list).subList(start, start + length)).reversed()
        val rtn = list.toMutableList()
        for ((idx, n) in (start until start + length).zip(elements)) {
            rtn[idx % rtn.size] = n
        }
        return rtn
    }

    fun part2(input: List<String>): String {
        val line = input.first()
        val list = hash(line)
        return list.joinToString(separator = "") { "%02x".format(it) }
    }

    fun hash(line: String): List<Byte> {
        val lengths = line.map { it.code } + listOf(17, 31, 73, 47, 23)
        val all = List(64) { lengths }.flatten()
        var list = List(256) { it.toByte() }
        var position = 0
        for ((skipSize, l) in all.withIndex()) {
            list = twist(list, l, position)
            position = (position + l + skipSize) % 256
        }
        return list.chunked(16).map { block ->
            block.reduce { acc, i -> acc.xor(i)}
        }
    }
}

fun main() {
    val testInput = """
        3,4,1,5
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day10.part1(testInput, 5))
    println(Day10.part2(listOf("")))

    println("------Real------")
    val input = readInput(2017, 10)
    println("Part 1 result: ${Day10.part1(input, 256)}")
    println("Part 2 result: ${Day10.part2(input)}")
    timingStatistics { Day10.part1(input, 256) }
    timingStatistics { Day10.part2(input) }
}