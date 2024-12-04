package y2024

import util.Cardinal
import util.getRange
import util.readInput
import util.timingStatistics

object Day04 {

    /**
     * get diagonal going in northeast direction
     */
    private fun getDiagonal(
        n: Int,
        grid: List<String>
    ): String {
        val start = if (n < grid.size) {
            n to 0
        } else {
            (grid.size - 1) to (n - grid.size + 1)
        }
        var current = start
        return buildString {
            do {
                append(grid[current.first][current.second])
                current = Cardinal.NORTH.of(Cardinal.EAST.of(current))
            } while (current.first >= 0 && current.second < grid.size)
        }
    }

    private fun allDirections(input: List<String>): List<List<String>> {
        val diagonalsNE = (0 until (2 * input.size) - 1).map { getDiagonal(it, input) }
        val rotated = (input.indices.reversed()).map { col -> input.indices.map { row -> input[row][col] }.joinToString("") }
        val diagonalsSE = (0 until (2 * input.size) - 1).map { getDiagonal(it, rotated) }

        val variants = listOf(
            input,
            diagonalsNE,
            rotated,
            diagonalsSE,
        )
        return variants
    }

    fun part1(grid: List<String>): Int {
        val variants = allDirections(grid)
        return variants.flatten().sumOf { countWords(it) }
    }


    private const val TO_FIND = "XMAS"
    private const val TO_FIND_REVERSED = "SAMX"

    private fun countWords(
        line: String
    ): Int {
        return line.windowed(TO_FIND.length, step = 1)
            .count { it == TO_FIND || it == TO_FIND_REVERSED }
    }

    fun part2(input: List<String>): Int {
        val grid = input.map { it.toList() }
        val stuff = (0..grid.size - 3).flatMap { r ->
            (0..grid.size - 3).map { c ->
                getRange(grid, r, c, r + 2, c + 2).joinToString(separator = "")
            }
        }
        val re = Regex("(M.M.A.S.S)|(M.S.A.M.S)|(S.M.A.S.M)|(S.S.A.M.M)")
        return stuff.count {
            re.matches(it)
        }
    }
}

fun main() {
    val testInput = """
        MMMSXXMASM
        MSAMXMSMSA
        AMXSXMAAMM
        MSAMASMSMX
        XMASAMXAMM
        XXAMMXXAMA
        SMSMSASXSS
        SAXAMASAAA
        MAMMMXMMMM
        MXMXAXMASX
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day04.part1(testInput))
    println(Day04.part2(testInput))

    println("------Real------")
    val input = readInput(2024, 4)
    println("Part 1 result: ${Day04.part1(input)}")
    //println("Part 2 result: ${Day04.part2(input)}")
    timingStatistics { Day04.part1(input) }
    //timingStatistics { Day04.part2(input) }
}