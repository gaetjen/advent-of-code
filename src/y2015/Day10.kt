package y2015

import java.lang.StringBuilder
import kotlin.math.min

object Day10 {

    fun part1(input: String): Int {
        var result = input
        repeat(40) {
            result = lookAndSay(result)
        }
        return result.length
    }

    private fun lookAndSay(string: String): String {
        val builder = StringBuilder()
        var idx = 0
        while (idx < string.length) {
            val match = string[idx]
            // if we take substring without end index this takes forever lol
            val repeats = string.substring(idx, min(string.length, idx + 3)).takeWhile { it == match }.length
            builder.append(repeats).append(match)
            idx += repeats
        }
        return builder.toString()
    }

    /*private val lookAndSay = DeepRecursiveFunction<String, String> { string ->
        // heap overflow
        val match = string.first()
        if (string.all { it == match }) {
            string.length.toString() + match
        } else {
            string.takeWhile { it == match }.length.toString() + match + callRecursive(string.dropWhile { it == match })
        }
    }*/

    fun part2(input: String): Int {
        var result = input
        repeat(50) {
            result = lookAndSay(result)
        }
        return result.length
    }
}

fun main() {
    println("------Real------")
    val input = "1321131112"
    println(Day10.part1(input))
    println(Day10.part2(input))
}
