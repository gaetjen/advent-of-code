package y2023

import util.Pos
import util.readInput
import y2022.Day23.neighbors

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
            val newline = line.replace(".", "a")
            val symbols = Regex("[^a\\d]").findAll(newline)
            symbols.map {
                row to it.range.first
            }
        }
    }

    fun part1(input: List<String>): Long {
        val numbers = numbers(input)
        val symbols = symbols(input)
        val neighbors = symbols.flatMap {
            it.neighbors()
        }.toSet()
        val neighborNumbers = numbers.filter {( poss, n) ->
            poss.any { it in neighbors }
        }
        return neighborNumbers.sumOf { it.second.toLong() }
    }

    fun gearCandidates(input: List<String>): List<Pos> {
        return input.flatMapIndexed { row: Int, line: String ->
            val gears = Regex("\\*").findAll(line)
            gears.map {
                row to it.range.first
            }
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
    println(Day03.part1(input))
    println(Day03.part2(input))
}