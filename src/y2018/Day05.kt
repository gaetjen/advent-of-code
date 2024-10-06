package y2018

import util.readInput
import util.timingStatistics

object Day05 {
    fun part1(input: List<String>): Int {
        val parsed = input.first()
        return shrink(parsed).length
    }

    private fun shrink(parsed: String): String {
        var shrunk = parsed
        while (true) {
            val shrunkEven = shrunk.chunked(2).filter { !reacts(it) }.joinToString("")
            val newShrunk = shrunkEven.first() + shrunkEven.drop(1).chunked(2).filter { !reacts(it) }.joinToString("")
            if (shrunk == newShrunk) {
                return shrunk
            } else {
                shrunk = newShrunk
            }
        }
    }

    private fun reacts(str: String) : Boolean {
        if (str.length == 1) return false
        return str[0].uppercase() == str[1].uppercase() && str[0] != str[1]
    }

    fun part2(input: List<String>): Int {
        val parsed = input.first()
        val preShrunk = shrink(parsed)
        return ('a'..'z').minOf { c ->
            shrink(preShrunk.replace("$c", "", ignoreCase = true)).length
        }
    }
}

fun main() {
    val testInput = """
        dabAcCaCBAcCcaDA
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day05.part1(testInput))
    println(Day05.part2(testInput))

    println("------Real------")
    val input = readInput(2018, 5)
    println("Part 1 result: ${Day05.part1(input)}")
    println("Part 2 result: ${Day05.part2(input)}")
    timingStatistics { Day05.part1(input) }
    timingStatistics { Day05.part2(input) }
}