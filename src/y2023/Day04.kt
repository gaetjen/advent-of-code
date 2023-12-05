package y2023

import util.readInput
import util.timingStatistics
import kotlin.math.pow

object Day04 {
    private fun parse(input: List<String>): List<Pair<List<Int>, List<Int>>> {
        return input.map { line ->
            val (winning, mine) = line.substringAfter(":").trim().split(" | ")
            winning.split(" ").mapNotNull { it.toIntOrNull() } to mine.split(" ").mapNotNull { it.toIntOrNull() }
        }
    }

    fun part1(input: List<String>): Double {
        val parsed = parse(input)
        return parsed.map { (winning, mine) ->
            mine.filter { it in winning }.size
        }.filter { it != 0 }.sumOf { 2.0.pow(it - 1) }
    }

    fun part2(input: List<String>): Int {
        val parsed = parse(input)
        val numberWonByCard = parsed.map { (winning, mine) ->
            mine.filter { it in winning }.size
        }
        val numberCards = MutableList(numberWonByCard.size) { 1 }
        numberWonByCard.mapIndexed { index, thisWin ->
            (1..thisWin).forEach {
                numberCards[index + it] += numberCards[index]
            }
        }
        return numberCards.sum()
    }
}

fun main() {
    val testInput = """
        Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
        Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19
        Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1
        Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83
        Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36
        Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day04.part1(testInput))
    println(Day04.part2(testInput))

    println("------Real------")
    val input = readInput("resources/2023/day04")
    println("Part 1 result: ${Day04.part1(input)}")
    println("Part 2 result: ${Day04.part2(input)}")
    timingStatistics { Day04.part1(input) }
    timingStatistics { Day04.part2(input) }
}
