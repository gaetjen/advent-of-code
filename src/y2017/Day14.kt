package y2017

import io.ksmt.utils.toBinary
import util.neighborsManhattan
import util.timingStatistics

object Day14 {
    private fun hashes(input: String): List<List<Byte>> {
        return (0..127).map {
            Day10.hash("$input-$it")
        }
    }

    fun part1(input: String): Int {
        val hashes = hashes(input)
        return hashes.flatten().sumOf { it.countOneBits() }
    }

    fun part2(input: String): Int {
        val hashes = hashes(input)
        val ones = hashes.flatMapIndexed { row, line ->
            line.flatMapIndexed { block, byte ->
                byte.toBinary().mapIndexedNotNull { column, c ->
                    if (c == '1') {
                        row to (block * 8 + column)
                    } else null
                }
            }
        }.toSet()
        val components = Day12.connectedComponents(ones) { pos ->
            pos.neighborsManhattan().filter { it in ones }
        }
        return components.size
    }
}

fun main() {
    val testInput = "flqrgnkx"
    println("------Tests------")
    println(Day14.part1(testInput))
    println(Day14.part2(testInput))

    println("------Real------")
    val input = "jxqlasbh"
    println("Part 1 result: ${Day14.part1(input)}")
    println("Part 2 result: ${Day14.part2(input)}")
    timingStatistics { Day14.part1(input) }
    timingStatistics { Day14.part2(input) }
}