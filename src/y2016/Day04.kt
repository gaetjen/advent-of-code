package y2016

import util.readInput

object Day04 {
    private fun parse(input: List<String>): List<Triple<String, Int, String>> {
        return input.map {
            Triple(
                it.substringBeforeLast('-'),
                it.substringAfterLast('-').substringBefore('[').toInt(),
                it.substringAfter('[').substring(0 until 5)
            )
        }
    }

    fun part1(input: List<String>): Int {
        val parsed = parse(input)
        return parsed.filter{ validRoom(it.first, it.third) }.sumOf { it.second }
    }

    private fun validRoom(name: String, check: String): Boolean {
        val counts = name.replace("-", "").groupingBy { it }.eachCount()
        return counts.entries
            .sortedBy { it.key }
            .sortedByDescending { it.value }
            .take(5)
            .joinToString(separator = "") { it.key.toString() } == check
    }

    fun part2(input: List<String>): Long {
        val rooms = parse(input).filter { validRoom(it.first, it.third) }
        rooms.forEach { (name, sector, _) ->
            println(decode(name, sector) + ": $sector")
        }
        return 0L
    }

    private fun decode(name: String, sector: Int): String {
        val shift = sector % 26
        return name.map {
            if (it == '-') {
                ' '
            } else {
                (((it.code + shift - 'a'.code) % 26) + 'a'.code).toChar()
            }
        }.joinToString(separator = "") { it.toString() }
    }
}

fun main() {
    val testInput = """
        aaaaa-bbb-z-y-x-123[abxyz]
        a-b-c-d-e-f-g-h-987[abcde]
        not-a-real-room-404[oarel]
        totally-real-room-200[decoy]
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day04.part1(testInput))
    println(Day04.part2(testInput))

    println("------Real------")
    val input = readInput("resources/2016/day04")
    println(Day04.part1(input))
    println(Day04.part2(input))
}