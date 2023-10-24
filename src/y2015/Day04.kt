package y2015

import util.readInput
import util.md5

object Day04 {
    fun part1(input: String): Long {
        var result = 0L
        while (true) {
            val hash = (input + result).md5()
            if (hash.startsWith("00000")) {
                return result
            }
            result++
        }
    }

    fun part2(input: String): Long {
        var result = 0L
        while (true) {
            val hash = (input + result).md5()
            if (hash.startsWith("000000")) {
                return result
            }
            result++
        }
    }
}

fun main() {
    val testInput = "abcdef"
    println("------Tests------")
    println(Day04.part1(testInput))
    println(Day04.part2(testInput))

    println("------Real------")
    val input = "bgvyzdsv"
    println(Day04.part1(input))
    println(Day04.part2(input))
}
