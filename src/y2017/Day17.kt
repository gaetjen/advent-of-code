package y2017

import util.timingStatistics

object Day17 {


    fun part1(input: Int): Int {
        val buffer = mutableListOf(0)
        var iteration = 0
        var currentPosition = 0
        while (iteration < 2017) {
            currentPosition = (currentPosition + input) % buffer.size + 1
            buffer.add(currentPosition, iteration + 1)
            iteration++
        }
        return buffer[currentPosition + 1]
    }

    fun part2(input: Int): Int {
        val buffer = mutableListOf(0)
        var iteration = 0
        var currentPosition = 0
        while (iteration < 50_000_000) {
            currentPosition = (currentPosition + input) % (iteration + 1) + 1
            if (currentPosition == 1) {
                buffer.add(currentPosition, iteration + 1)
            }
            iteration++
        }
        return buffer[buffer.indexOf(0) + 1]
    }
}

fun main() {
    val testInput = 3
    println("------Tests------")
    println(Day17.part1(testInput))
    println(Day17.part2(testInput))

    println("------Real------")
    val input = 376
    println("Part 1 result: ${Day17.part1(input)}")
    println("Part 2 result: ${Day17.part2(input)}")
    timingStatistics { Day17.part1(input) }
    timingStatistics { Day17.part2(input) }
}