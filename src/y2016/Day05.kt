package y2016

import util.md5

object Day05 {

    fun part1(input: String): String {
        var index = 0
        val characters = mutableListOf<Char>()
        while (characters.size < 8) {
            val hash = (input + index.toString()).md5()
            if (hash.startsWith("00000")) {
                characters.add(hash[5])
            }
            index++
        }
        return characters.joinToString(separator = "") { it.toString() }
    }

    fun part2(input: String): String {
        var index = 0
        val characters = "________".toMutableList()
        while (characters.contains('_')) {
            val hash = (input + index.toString()).md5()
            if (hash.startsWith("00000")) {
                val pwIndex = hash[5].code - '0'.code
                if (pwIndex in 0..7 && characters[pwIndex] == '_') {
                    characters[pwIndex] = hash[6]
                    println(characters.joinToString(separator = "") { it.toString() })
                }
            }
            index++
        }
        return characters.joinToString(separator = "") { it.toString() }
    }
}

fun main() {
    val testInput = "abc"
    println("------Tests------")
    println(Day05.part1(testInput))
    println(Day05.part2(testInput))

    println("------Real------")
    val input = "ojvtpuvg"
    println(Day05.part1(input))
    println(Day05.part2(input))
}