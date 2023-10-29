package y2015

import util.readInput

object Day08 {
    fun part1(input: List<String>): Int {
        val quotes = input.sumOf { str ->
            str.count { it == '"' }
        }
        val backs = input.sumOf {
            it.split(Regex("\\\\\\\\")).count() - 1
        }
        val hex = input.sumOf {
            (it.replace(Regex("\\\\\\\\"), "")
                .split(Regex("\\\\x..")).count() - 1) * 3
        }
        println("qs, backs, hex: $quotes, $backs, $hex")
        return quotes + backs + hex
    }

    fun part2(input: List<String>): Int {
        val quotes = input.sumOf { str ->
            str.count { it == '"' }
        }
        val backs = input.sumOf { str ->
            str.count { it == '\\' }
        }
        println("qs, backs: $quotes, $backs")
        return quotes + backs + (input.size * 2)
    }
}

fun main() {
    val testInput = """
        ""
        "abc"
        "aaa\"aaa"
        "\x27"
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day08.part1(testInput))
    println(Day08.part2(testInput))

    println("------Real------")
    val input = readInput("resources/2015/day08")
    println(Day08.part1(input))
    println(Day08.part2(input))
}
