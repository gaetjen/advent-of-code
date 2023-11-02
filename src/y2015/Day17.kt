package y2015

import util.readInput
import kotlin.math.pow

object Day17 {
    private fun parse(input: List<String>): List<Int> {
        return input.map { it.toInt() }
    }

    fun part1(input: List<String>, total: Int): Int {
        val parsed = parse(input)
        val variations = 2.0.pow(parsed.size.toDouble()).toInt()
        return (0 until variations).count { bitMask ->
            val bits = Integer.toBinaryString(bitMask).padStart(parsed.size, '0')
            parsed.zip(bits.toList()).filter { it.second == '1' }.sumOf { it.first } == total
        }
    }

    fun part2(input: List<String>, total: Int): Int {
        val parsed = parse(input)
        val variations = 2.0.pow(parsed.size.toDouble()).toInt()
        val containers =  (0 until variations).mapNotNull { bitMask ->
            val bits = Integer.toBinaryString(bitMask).padStart(parsed.size, '0')
            if (parsed.zip(bits.toList()).filter { it.second == '1' }.sumOf { it.first } == total) {
                bits.count { it == '1' }
            } else {
                null
            }
        }
        val min = containers.min()
        return containers.count { it == min }
    }
}

fun main() {
    val testInput = """
        20
        15
        10
        5
        5
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day17.part1(testInput, 25))
    println(Day17.part2(testInput, 25))

    println("------Real------")
    val input = readInput("resources/2015/day17")
    println(Day17.part1(input, 150))
    println(Day17.part2(input, 150))
}
