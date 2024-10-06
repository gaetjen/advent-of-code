package y2018

import util.Pos
import util.plus
import util.readInput
import util.timingStatistics

data class Rectangle(
    val id: Int,
    val origin: Pos,
    val size: Pos
) {
    companion object {
        private fun crosses(r1: Rectangle, r2: Rectangle): Boolean {
            return r1.origin.first + 1 in (r2.origin.first + 1)..(r2.origin.first + r2.size.first)
                    && r2.origin.second + 1 in (r1.origin.second + 1)..(r1.origin.second + r1.size.second)
        }
    }

    operator fun contains(p: Pos): Boolean {
        return p.first in (origin.first + 1)..(origin.first + size.first)
                && p.second in (origin.second + 1)..(origin.second + size.second)
    }

    fun intersects(other: Rectangle): Boolean {
        return corners().any { it in other } || other.corners().any { it in this } || crosses(other)
    }

    private fun crosses(other: Rectangle): Boolean {
        return Companion.crosses(this, other) || Companion.crosses(other, this)
    }

    private fun corners(): List<Pos> = listOf(
        origin + (1 to 1),
        origin + size,
        origin.first + 1 to origin.second + size.second,
        origin.first + size.first to origin.second + 1
    )
}


object Day03 {
    private fun parse(input: List<String>): List<Rectangle> {
        return input.map { line ->
            val (id, _, origin, size) = line.drop(1).split(" ")
            val (ox, oy) = origin.dropLast(1).split(",").map { it.toInt() }
            val (sx, sy) = size.split("x").map { it.toInt() }
            Rectangle(
                id.toInt(),
                ox to oy,
                sx to sy
            )
        }
    }

    fun part1(input: List<String>): Int {
        val claims = parse(input)
        return (0..1000)
            .flatMap { x -> (0..1000).map { y -> x to y } }
            .count { pos -> claims.count { pos in it } > 1 }
    }

    fun part2(input: List<String>): Int {
        val claims = parse(input).toSet()
        val noIntersect = claims.filter { rect ->
            (claims - rect).none { rect.intersects(it) }
        }
        if (noIntersect.size != 1) error("should only have one claim without intersections, but have ${noIntersect.size}!")
        return noIntersect.first().id
    }
}

fun main() {
    val testInput = """
        #1 @ 1,3: 4x4
        #2 @ 3,1: 4x4
        #3 @ 5,5: 2x2
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day03.part1(testInput))
    println(Day03.part2(testInput))

    println("------Real------")
    val input = readInput(2018, 3)
    println("Part 2 result: ${Day03.part2(input)}")
    println("Part 1 result: ${Day03.part1(input)}")
    timingStatistics { Day03.part1(input) }
    timingStatistics { Day03.part2(input) }
}