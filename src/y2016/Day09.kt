package y2016

import util.readInput

object Day09 {
    fun part1(input: List<String>) {
        input.forEach {
            println("${decompressedLength(it)} $it")
        }
    }

    private fun decompressedLength(str: String): Int {
        val firstStart = str.indexOf('(')
        if (firstStart == -1) {
            return str.length
        }
        val firstEnd = str.indexOf(')', startIndex = firstStart)
        val (len, repeats) = str.substring(firstStart + 1, firstEnd).split('x').map { it.toInt() }
        return firstStart + len * repeats + decompressedLength(str.drop(firstEnd + 1 + len))
    }

    private fun decompressedLengthV2(str: String): Long {
        val firstStart = str.indexOf('(')
        if (firstStart == -1) {
            return str.length.toLong()
        }
        val firstEnd = str.indexOf(')', startIndex = firstStart)
        val (len, repeats) = str.substring(firstStart + 1, firstEnd).split('x').map { it.toInt() }
        val subExpanded = decompressedLengthV2(str.drop(firstEnd + 1).take(len))
        return firstStart + repeats * subExpanded + decompressedLengthV2(str.drop(firstEnd + 1 + len))
    }

    fun part2(input: List<String>) {
        input.forEach {
            println("${decompressedLengthV2(it)} $it")
        }
    }
}

fun main() {
    val testInput = """
        ADVENT
        A(1x5)BC
        (3x3)XYZ
        A(2x2)BCD(2x2)EFG
        (6x1)(1x3)A
        X(8x2)(3x3)ABCY
        (25x3)(3x3)ABC(2x3)XY(5x2)PQRSTX(18x9)(3x2)TWO(5x7)SEVEN
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day09.part1(testInput))
    println(Day09.part2(testInput))

    println("------Real------")
    val input = readInput("resources/2016/day09")
    println(Day09.part1(input))
    println(Day09.part2(input))
}