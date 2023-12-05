package y2023

import util.readInput
import util.timingStatistics

val digitStrings = mapOf(
    "one" to 1,
    "two" to 2,
    "three" to 3,
    "four" to 4,
    "five" to 5,
    "six" to 6,
    "seven" to 7,
    "eight" to 8,
    "nine" to 9,
    "1" to 1,
    "2" to 2,
    "3" to 3,
    "4" to 4,
    "5" to 5,
    "6" to 6,
    "7" to 7,
    "8" to 8,
    "9" to 9,
    "0" to 0
)

val re = Regex(
    digitStrings.keys.joinToString(separator = "|")
)

val nonConsumingRe = Regex("(?=${re.pattern})")

object Day01 {
    private fun parse(input: List<String>): List<Int> {
        return input.map {
            val ds = it.replace(Regex("[a-z]"), "")
            (ds.first().toString() + ds.last().toString()).toInt()
        }
    }

    fun part1(input: List<String>): Int {
        val parsed = parse(input)
        return parsed.sum()
    }

    private fun parse2(input: List<String>): List<Int> {
        return input.map { line ->
            val matches = nonConsumingRe.findAll(line)
            val first = re.find(line, matches.first().range.first)
            val last = re.find(line, matches.last().range.first)
            digitStrings[first?.value]!! * 10 + digitStrings[last?.value]!!
        }
    }

    fun part2(input: List<String>): Int {
        val parsed = parse2(input)
        return parsed.sum()
    }
}

fun main() {
    val x = "3oneight"
    val matches = re.findAll(x)
    matches.forEach { println(it.value) }
    val testInput = """
        1abc2
        pqr3stu8vwx
        a1b2c3d4e5f
        treb7uchet
    """.trimIndent().split("\n")

    val testInput2 = """
        two1nine
        eightwothree
        abcone2threexyz
        xtwone3four
        4nineeightseven2
        zoneight234
        7pqrstsixteen
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day01.part1(testInput))
    println(Day01.part2(testInput2))

    println("------Real------")
    val input = readInput("resources/2023/day01")
    println("Part 1 result: ${Day01.part1(input)}")
    println("Part 2 result: ${Day01.part2(input)}")
    timingStatistics { Day01.part1(input) }
    timingStatistics { Day01.part2(input) }
}