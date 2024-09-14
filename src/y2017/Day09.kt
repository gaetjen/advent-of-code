package y2017

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import util.readInput
import util.timingStatistics

object Day09 {
    private fun parse(input: List<String>): JsonArray {
        val line = input.first()
            .replace(Regex("!."), "")
            .replace(Regex("<.*?>"), "")
            .replace("{", "[")
            .replace("}", "]")
            .replace("[,", "[")
            .replace(",]", "]")
        val json = Json.parseToJsonElement(line)
        return if (json is JsonArray) {
            json
        } else {
            throw IllegalArgumentException()
        }
    }

    private fun score(jsonArray: JsonArray, level: Int = 1) : Int {
        return level + jsonArray.sumOf { score(it as JsonArray, level + 1) }
    }

    fun part1(input: List<String>): Int {
        val parsed = parse(input)
        return score(parsed)
    }

    fun part2(input: List<String>): Int {
        val cancelled = input.first()
            .replace(Regex("!."), "")
        val clean = cancelled
            .replace(Regex("<.*?>"), "<>")
        return cancelled.length - clean.length
    }
}

fun main() {
    val testInput = """
        {{{},{},{{}}}}
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day09.part1(testInput))
    println(Day09.part2(testInput))

    println("------Real------")
    val input = readInput(2017, 9)
    println("Part 1 result: ${Day09.part1(input)}")
    println("Part 2 result: ${Day09.part2(input)}")
    timingStatistics { Day09.part1(input) }
    timingStatistics { Day09.part2(input) }
}