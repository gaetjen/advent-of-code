package y2015

import kotlin.math.ceil
import kotlin.math.sqrt

object Day20 {
    fun factors(n: Int): List<Int> {
        return (1..ceil(sqrt(n.toDouble())).toInt()).mapNotNull {
            if (n % it == 0) listOf(it, n / it) else null
        }.flatten()
    }

    fun part1(input: Int): Int {
        val target = input / 10
        var x = 1
        while (true) {
            if (factors(x).sum() >= target) {
                return x
            }
            x++
        }
    }

    private fun factors50(n: Int): List<Int> {
        return factors(n).filter { 50 * it >= n }
    }

    fun part2(input: Int): Int {
        var x = 1
        while (true) {
            if (factors50(x).sum() * 11 >= input) {
                return x
            }
            x++
        }
    }
}

fun main() {
    val testInput = 140
    println("------Tests------")
    println(Day20.part1(testInput))
    println(Day20.part2(testInput))

    println("------Real------")
    val input = 33100000
    println(Day20.part1(input))
    println(Day20.part2(input))
}
