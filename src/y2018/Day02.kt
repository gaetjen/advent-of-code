package y2018

import util.readInput
import util.timingStatistics

object Day02 {
    fun part1(input: List<String>): Int {
        val counts = input.map { id ->
            id.groupingBy { it }.eachCount()
        }
        val twos = counts.filter { idCounts -> idCounts.values.any { it == 2 } }.size
        val threes = counts.filter { idCounts -> idCounts.values.any { it == 3 } }.size
        return twos * threes
    }

    fun part2(input: List<String>): String {
        val (id1, id2) = match(input)
        return id1.zip(id2).filter { (c1, c2) -> c1 == c2 }.joinToString("") { it.first.toString() }
    }

    fun match(boxIds: List<String>): Pair<String, String> {
        boxIds.indices.forEach { idx ->
            boxIds.drop(1 + idx).forEach {
                if (editDistance(it, boxIds[idx]) == 1) return it to boxIds[idx]
            }
        }
        throw IllegalArgumentException("no match found")
    }

    fun editDistance(str1: String, str2: String): Int {
        return str1.zip(str2).count { (c1, c2) -> c1 != c2 }
    }
}

fun main() {
    val testInput = """
        abcdef
        bababc
        abbcde
        abcccd
        aabcdd
        abcdee
        ababab
    """.trimIndent().split("\n")
    val testInput2 = """
        abcde
        fghij
        klmno
        pqrst
        fguij
        axcye
        wvxyz
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day02.part1(testInput))
    println(Day02.part2(testInput2))

    println("------Real------")
    val input = readInput(2018, 2)
    println("Part 1 result: ${Day02.part1(input)}")
    println("Part 2 result: ${Day02.part2(input)}")
    timingStatistics { Day02.part1(input) }
    timingStatistics { Day02.part2(input) }
}