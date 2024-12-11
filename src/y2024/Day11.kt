package y2024

import util.readInput
import util.timingStatistics

object Day11 {
    private fun parse(input: List<String>): List<Long> {
        return input.first().split(" ").map { it.toLong() }
    }

    fun replace(n: Long): List<Long> {
        return when {
            n == 0L -> listOf(1L)
            n.toString().length % 2 == 0 -> listOf(
                n.toString().take(n.toString().length / 2).toLong(),
                n.toString().drop(n.toString().length / 2).toLong()
            )

            else -> listOf(n * 2024)
        }
    }

    val cache: MutableMap<Pair<Long, Int>, Long?> = mutableMapOf()

    fun finalLength(
        n: Long,
        repeats: Int
    ): Long {
        if (repeats == 0) return 1L
        return cache[n to repeats] ?: run {
            val nextLevel = replace(n)
            val result = nextLevel.sumOf { finalLength(it, repeats - 1) }
            cache[n to repeats] = result
            result
        }
    }

    fun part1(input: List<String>): Long {
        val parsed = parse(input)
        val result = parsed.sumOf { finalLength(it, 25) }
        cache.replaceAll { _, _ -> null }
        return result
    }

    fun part2(input: List<String>): Long {
        val parsed = parse(input)
        val result = parsed.sumOf { finalLength(it, 75) }
        cache.replaceAll { _, _ -> null }
        return result
    }
}

fun main() {
    val testInput = """
        125 17
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day11.part1(testInput))
    println(Day11.part2(testInput))

    println("------Real------")
    val input = readInput(2024, 11)
    println("Part 1 result: ${Day11.part1(input)}")
    println("Part 2 result: ${Day11.part2(input)}")
    timingStatistics { Day11.part1(input) }
    timingStatistics { Day11.part2(input) }
}