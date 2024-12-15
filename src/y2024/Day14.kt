package y2024

import util.Pos
import util.plus
import util.printGrid
import util.product
import util.readInput
import util.times
import util.timingStatistics

data class Robot(
    val pos: Pos,
    val velocity: Pos
)

object Day14 {
    private fun parse(input: List<String>): List<Robot> {
        val regex = """p=(\d+),(\d+) v=(-?\d+),(-?\d+)""".toRegex()
        return input.map { line ->
            val (px, py, vx, vy) = regex.matchEntire(line)!!.groupValues.drop(1).map { it.toInt() }
            Robot(
                px to py,
                vx to vy
            )
        }
    }

    fun part1(
        input: List<String>,
        areaSize: Pos
    ): Int {
        val robots = parse(input)
        val endPositions = robots.map {
            val plain = it.pos + it.velocity * 100
            plain.first.mod(areaSize.first) to plain.second.mod(areaSize.second)
        }
        return endPositions
            .groupingBy { quadrant(it, areaSize) }
            .eachCount()
            .filterKeys { it != null }
            .values.product()
    }

    private fun quadrant(
        pos: Pos,
        areaSize: Pos
    ): Int? {
        return when {
            pos.first < areaSize.first / 2 && pos.second < areaSize.second / 2 -> 1
            pos.first > areaSize.first / 2 && pos.second < areaSize.second / 2 -> 2
            pos.first < areaSize.first / 2 && pos.second > areaSize.second / 2 -> 3
            pos.first > areaSize.first / 2 && pos.second > areaSize.second / 2 -> 4
            else -> null
        }
    }

    fun part2(input: List<String>): Int {
        val robots = parse(input)
        var seconds = 7709
        printRobotsAtSecond(robots, seconds)
        /*while (true) {
            seconds += 101
            printRobotsAtSecond(robots, seconds)
            Thread.sleep(350)
        }*/
        return seconds
    }

    private fun printRobotsAtSecond(
        robots: List<Robot>,
        seconds: Int
    ) {
        val movedRobots = robots.map {
            val newPos = it.pos + it.velocity * seconds
            Robot(
                newPos.first.mod(101) to newPos.second.mod(103),
                it.velocity
            )
        }
        println("Seconds: $seconds")
        printGrid(
            positions = movedRobots.associate { (it.pos.second to it.pos.first) to "#" }
        )
    }
}

/*
......2..1.
...........
1..........
.11........
.....1.....
...12......
.1....1....
 */

fun main() {
    val testInput = """
        p=0,4 v=3,-3
        p=6,3 v=-1,-3
        p=10,3 v=-1,2
        p=2,0 v=2,-1
        p=0,0 v=1,3
        p=3,0 v=-2,-2
        p=7,6 v=-1,-3
        p=3,0 v=-1,-2
        p=9,3 v=2,3
        p=7,3 v=-1,2
        p=2,4 v=2,-3
        p=9,5 v=-3,-3
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day14.part1(testInput, 11 to 7))

    println("------Real------")
    val input = readInput(2024, 14)
    println("Part 1 result: ${Day14.part1(input, 101 to 103)}")
    println("Part 2 result: ${Day14.part2(input)}")
    timingStatistics { Day14.part1(input, 101 to 103) }
}

// vert: 87, 190
// hor: 33, 134