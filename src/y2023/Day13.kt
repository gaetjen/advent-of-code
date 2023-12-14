package y2023

import util.readInput
import util.split
import util.timingStatistics
import util.transpose

object Day13 {
    private fun parse(input: List<String>): List<List<String>> {
        return input.split { it.isBlank() }
    }

    fun part1(input: List<String>): Int {
        val parsed = parse(input)
        return parsed.sumOf { block ->
            val row = reflectionRow(block)
            val col = reflectionRow(block.map { it.toList() }.transpose())
            if (row == null && col != null) {
                col
            } else if (col == null && row != null) {
                row * 100
            } else if (col != null && row != null) {
                println("found both: $col, $row")
                col
            } else {
                error("no solution found")
            }
        }
    }

    fun <T> reflectionRow(rows: List<T>): Int? {
        val candidates = rows.zipWithNext().mapIndexedNotNull { idx, (row1, row2) ->
            if (row1 == row2) idx else null
        }
        return candidates.firstNotNullOfOrNull { verify(it + 1, rows) }
    }

    fun <T> verify(rowNumber: Int, rows: List<T>): Int? {
        val zip = rows.take(rowNumber).reversed().zip(rows.drop(rowNumber))
        return if (zip.all { (row1, row2) -> row1 == row2 }) rowNumber else null
    }

    fun part2(input: List<String>): Int {
        val parsed = parse(input)
        return parsed.sumOf { block ->
            val chars = block.map { it.toList() }
            val row = offByOneReflectionRow(chars)
            val col = offByOneReflectionRow(chars.transpose())
            if (row == null && col != null) {
                col
            } else if (col == null && row != null) {
                row * 100
            } else if (col != null && row != null) {
                println("found both: $col, $row")
                col
            } else {
                error("no solution found")
            }
        }
    }

    fun offByOneReflectionRow(rows: List<List<Char>>): Int? {
        return rows.indices.firstOrNull { idx ->
            countErrors(rows.take(idx).reversed().zip(rows.drop(idx))) == 1
        }
    }

    private fun countErrors(zip: List<Pair<List<Char>, List<Char>>>): Int {
        return zip.sumOf { (row1, row2) ->
            row1.zip(row2).count { (c1, c2) -> c1 != c2 }
        }
    }

}

fun main() {
    val testInput = """
        #.##..##.
        ..#.##.#.
        ##......#
        ##......#
        ..#.##.#.
        ..##..##.
        #.#.##.#.

        #...##..#
        #....#..#
        ..##..###
        #####.##.
        #####.##.
        ..##..###
        #....#..#
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day13.part1(testInput))
    println(Day13.part2(testInput))

    println("------Real------")
    val input = readInput(2023, 13)
    println("Part 1 result: ${Day13.part1(input)}")
    println("Part 2 result: ${Day13.part2(input)}")
    timingStatistics { Day13.part1(input) }
    timingStatistics { Day13.part2(input) }
}