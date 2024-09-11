package y2017

import util.Direction
import util.neighbors
import util.timingStatistics
import kotlin.math.floor
import kotlin.math.sqrt

object Day03 {
    fun part1(input: Int): Int {
        var previousSquareRoot = floor(sqrt(input.toDouble())).toInt()
        if (previousSquareRoot % 2 == 0) previousSquareRoot--
        val previousSquare = previousSquareRoot * previousSquareRoot
        val distanceFromCorner = input - previousSquare
        val sideStep = (previousSquareRoot + 1) / 2
        val howFarAlongHalfSide = distanceFromCorner % sideStep
        val distanceFromMidPoint = if ((distanceFromCorner / sideStep) % 2 == 0) sideStep - howFarAlongHalfSide else howFarAlongHalfSide
        return ((previousSquareRoot + 1) / 2) + distanceFromMidPoint
    }

    val spiralPath = sequence {
        var sideLength = 0
        var currentPos = 0 to 0
        yield(currentPos)
        while (true) {
            sideLength += 2
            currentPos = Direction.RIGHT.move(currentPos)
            yield(currentPos)
            repeat(sideLength - 1) {
                currentPos = Direction.UP.move(currentPos)
                yield(currentPos)
            }
            repeat(sideLength) {
                currentPos = Direction.LEFT.move(currentPos)
                yield(currentPos)
            }
            repeat(sideLength) {
                currentPos = Direction.DOWN.move(currentPos)
                yield(currentPos)
            }
            repeat(sideLength) {
                currentPos = Direction.RIGHT.move(currentPos)
                yield(currentPos)
            }
        }
    }


    fun part2(input: Int): Int {
        val grid = mutableMapOf((0 to 0) to 1)
        return spiralPath.drop(1).firstNotNullOf { pos ->
            val total = pos.neighbors().mapNotNull { grid[it] }.sum()
            if (total > input) {
                total
            } else {
                grid[pos] = total
                null
            }
        }
    }

}

fun main() {
    val testInput = 1024
    println("------Tests------")
    println(Day03.part1(testInput))
    println(Day03.part2(testInput))

    println("------Real------")
    val input = 277678
    println("Part 1 result: ${Day03.part1(input)}")
    println("Part 2 result: ${Day03.part2(input)}")
    timingStatistics { Day03.part1(input) }
    timingStatistics { Day03.part2(input) }
}