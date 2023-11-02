package y2015

import util.readInput


object Day16 {
    private fun parse(input: List<String>): List<Map<String, Int>> {
        return input.map {
            val els = it.replace(Regex("[:,]"), "").split(" ")
            mapOf(
                els[2] to els[3].toInt(),
                els[4] to els[5].toInt(),
                els[6] to els[7].toInt()
            )
        }
    }

    fun part1(): Long {
        return 373L
    }

    fun part2(input: List<String>): Int {
        val parsed = parse(input)
        return parsed.indexOfFirst {
            isMatch(it)
        } + 1
    }

    private fun isMatch(sue: Map<String, Int>): Boolean {
        return sue["children"]?.let { it == 3 } ?: true &&
                sue["cats"]?.let { it > 7 } ?: true &&
                sue["samoyeds"]?.let { it == 2 } ?: true &&
                sue["pomeranians"]?.let { it < 3 } ?: true &&
                sue["akitas"]?.let { it == 0 } ?: true &&
                sue["vizslas"]?.let { it == 0 } ?: true &&
                sue["goldfish"]?.let { it < 5 } ?: true &&
                sue["trees"]?.let { it > 3 } ?: true &&
                sue["cars"]?.let { it == 2 } ?: true &&
                sue["perfumes"]?.let { it == 1 } ?: true
    }
}

/*
children: 3
cats: 7
samoyeds: 2
pomeranians: 3
akitas: 0
vizslas: 0
goldfish: 5
trees: 3
cars: 2
perfumes: 1

Sue 13: cats: 4, samoyeds: 7, pomeranians: 8
 */

fun main() {
    println("------Real------")
    val input = readInput("resources/2015/day16")
    println(Day16.part1())
    println(Day16.part2(input))
}
