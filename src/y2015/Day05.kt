package y2015

import util.readInput

object Day05 {
    fun part1(input: List<String>): Int {
        val nice = input.filter {
            hasVowels(it)
        }.filter {
            hasDouble(it)
        }.filter {
            !hasNaughty(it)
        }
        return nice.size
    }

    private fun hasVowels(string: String): Boolean {
        return string.filter {
            it in "aeiou"
        }.length >= 3
    }

    private fun hasDouble(string: String): Boolean {
        return string.windowed(2, 1).any { it.first() == it[1] }
    }

    private fun hasNaughty(string: String): Boolean {
        return string.contains(Regex("ab|cd|pq|xy"))
    }

    fun part2(input: List<String>): Int {
        val nice = input.filter {
            hasDoublePair(it)
        }.filter {
            hasOneRepeating(it)
        }
        return nice.size
    }

    private fun hasDoublePair(string: String): Boolean {
        for (i in 0..(string.length-4)) {
            if (string.substring(i, i+2) in string.substring(i+2)) {
                return true
            }
        }
        return false
    }

    private fun hasOneRepeating(string: String): Boolean {
        return string.windowed(3, 1).any { it.first() == it.last() }
    }
}

fun main() {
    val testInput = """
        qjhvhtzxzqqjkmpb
        xxyxx
        uurcxstgmygtbstg
        ieodomkazucvgmuy
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day05.part1(testInput))
    println(Day05.part2(testInput))

    println("------Real------")
    val input = readInput("resources/2015/day05")
    println(Day05.part1(input))
    println(Day05.part2(input))
}
