package y2023

import util.Pos
import util.readInput
import util.timingStatistics
import util.transpose
import y2022.Day15.manhattanDist

object Day11 {
    private fun parse(input: List<String>): List<Pos> {
        val asChars = input.map { it.toCharArray().toList() }.transpose()
        val expandedCols = expanded(asChars).toList().transpose()
        val allExpanded = expanded(expandedCols).toList()
        return positions(allExpanded)
    }

    private fun positions(input: List<List<Char>>): List<Pos> {
        return input.mapIndexed { row, line ->
            line.mapIndexedNotNull { col, c ->
                if (c == '#') Pos(row, col) else null
            }
        }.flatten()
    }

    private fun expanded(input: List<List<Char>>)  : Sequence<List<Char>> = sequence {
        input.forEach {
            if ('#' in it) {
                yield(it)
            }
            else {
                yield(it)
                yield(it)
            }
        }
    }

    fun part1(input: List<String>): Int {
        val parsed = parse(input)
        return pairWiseDistances(parsed).sum()
    }

    private fun pairWiseDistances(parsed: List<Pos>) = parsed.flatMapIndexed { idx, pos ->
        parsed.drop(idx + 1).map { it.manhattanDist(pos) }
    }

    fun part2(input: List<String>): Long {
        val parsed = parse(input)
        val originals = positions(input.map { it.toCharArray().toList() })
        val expand1Distances = pairWiseDistances(parsed)
        val originalDistances = pairWiseDistances(originals)
        val differences = expand1Distances.zip(originalDistances).map { it.first - it.second }
        return originalDistances.zip(differences).sumOf {
            //it.first + it.second * 1_000_000L
            it.first + it.second * 999_999L
        }
    }
}

fun main() {
    val testInput = """
        ...#......
        .......#..
        #.........
        ..........
        ......#...
        .#........
        .........#
        ..........
        .......#..
        #...#.....
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day11.part1(testInput))
    println(Day11.part2(testInput))

    println("------Real------")
    val input = readInput(2023, 11)
    println("Part 1 result: ${Day11.part1(input)}")
    println("Part 2 result: ${Day11.part2(input)}")
    timingStatistics { Day11.part1(input) }
    timingStatistics { Day11.part2(input) }
}