package y2016

object Day18 {
    fun part1(input: String, rows: Int): Int {
        val map = mutableListOf(input)
        repeat(rows - 1) {
            map.add(nextRow(map.last()))
        }
        return map.sumOf { row -> row.count { it == '.' } }
    }

    val dangerous = setOf(
        "^^.",
        ".^^",
        "^..",
        "..^"
    )

    private fun nextRow(row: String): String {
        return ".$row.".windowed(3, 1).joinToString(separator = "") {
            if (it in dangerous) "^" else "."
        }
    }
}

fun main() {
    val testInput = ".^^.^.^^^^"
    val testRows = 10
    println("------Tests------")
    println(Day18.part1(testInput, testRows))

    println("------Real------")
    val input = ".^^.^^^..^.^..^.^^.^^^^.^^.^^...^..^...^^^..^^...^..^^^^^^..^.^^^..^.^^^^.^^^.^...^^^.^^.^^^.^.^^.^."
    val rows = 40
    println(Day18.part1(input, rows))
    println(Day18.part1(input, 400000))
}