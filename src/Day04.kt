object Day04 {
    fun part1(input: List<String>): Int {
        return input.count { fullyContains(it) }
    }

    private fun inputToRanges(input: String): Pair<IntRange, IntRange> {
        val (p1, p2) = input.split(",")
            .map {
                it.split("-")
                    .map { x -> x.toInt() }
            }
        return (p1[0]..p1[1]) to (p2[0]..p2[1])
    }

    private fun fullyContains(input: String): Boolean {
        val (range1, range2) = inputToRanges(input)
        return (range1.first in range2 && range1.last in range2) || (range2.first in range1 && range2.last in range1)
    }

    fun part2(input: List<String>): Int {
        return input.count { partlyContains(it) }
    }

    private fun partlyContains(input: String): Boolean {
        val (range1, range2) = inputToRanges(input)
        return listOf(
            range1.first in range2,
            range1.last in range2,
            range2.first in range1
        ).any { it }
    }
}

fun main() {
    val input = readInput("resources/day04")
    println(Day04.part1(input))
    println(Day04.part2(input))
}