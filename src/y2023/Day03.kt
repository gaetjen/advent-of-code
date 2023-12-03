package y2023

import util.Pos
import util.indexOfAll
import util.measuredTime
import util.neighbors
import util.readInput

object Day03 {
    private fun numbers(input: List<String>): List<Pair<Set<Pos>, Int>> {
        return input.flatMapIndexed { row, line ->
            val numbers = Regex("\\d+").findAll(line)
            numbers.map {
                val positions = it.range.map { col -> row to col }.toSet()
                positions to it.value.toInt()
            }
        }
    }

    private fun symbols(input: List<String>): List<Pos> {
        return input.flatMapIndexed { row: Int, line: String ->
            val symbolPositions = line.toList().indexOfAll { it != '.' && !it.isDigit() }
            symbolPositions.map { row to it }
        }
    }

    fun part1(input: List<String>): Long {
        val numbers = numbers(input)
        val symbols = symbols(input)
        val neighbors = symbols.flatMap {
            it.neighbors()
        }.toSet()
        return numbers.filter { (poss, _) ->
            poss.any { it in neighbors }
        }.sumOf { it.second.toLong() }
    }

    private fun gearCandidates(input: List<String>): List<Pos> {
        return input.flatMapIndexed { row: Int, line: String ->
            val gearPositions = line.toList().indexOfAll { it == '*' }
            gearPositions.map { row to it }
        }
    }

    fun part2(input: List<String>): Int {
        val numbers = numbers(input)
        val gearCandidates = gearCandidates(input)
        return gearCandidates.map {
            it.neighbors()
        }.sumOf { gearNeighbors ->
            val numberNeighbors = numbers.filter { (poss, _) ->
                poss.any { it in gearNeighbors }
            }
            if (numberNeighbors.size == 2) {
                numberNeighbors[0].second * numberNeighbors[1].second
            } else {
                0
            }
        }
    }
}

fun main() {
    val testInput = """
        467..114..
        ...*......
        ..35..633.
        ......#...
        617*......
        .....+.58.
        ..592.....
        ......755.
        ...${'$'}.*....
        .664.598..
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day03.part1(testInput))
    println(Day03.part2(testInput))

    println("------Real------")
    val input = readInput("resources/2023/day03")
    measuredTime { Day03.part1(input) }
    measuredTime { Day03.part2(input) }
}
