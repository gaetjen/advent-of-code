package y2016

import util.readInput
import util.transpose

object Day06 {
    fun part1(input: List<String>): String {
        return transpose(input.map {
            it.toList()
        }).map { chars ->
            chars.groupingBy { it }.eachCount().maxBy { it.value }.key
        }.joinToString(separator = "") { it.toString() }
    }

    fun part2(input: List<String>): String {
        return transpose(input.map {
            it.toList()
        }).map { chars ->
            chars.groupingBy { it }.eachCount().minBy { it.value }.key
        }.joinToString(separator = "") { it.toString() }
    }
}

fun main() {
    val testInput = """
        eedadn
        drvtee
        eandsr
        raavrd
        atevrs
        tsrnev
        sdttsa
        rasrtv
        nssdts
        ntnada
        svetve
        tesnvt
        vntsnd
        vrdear
        dvrsen
        enarar
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day06.part1(testInput))
    println(Day06.part2(testInput))

    println("------Real------")
    val input = readInput("resources/2016/day06")
    println(Day06.part1(input))
    println(Day06.part2(input))
}