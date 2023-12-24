package y2023

import util.generateTakes
import util.readInput
import util.timingStatistics
import java.math.BigDecimal
import java.math.RoundingMode

object Day24 {
    data class Point3(
        val x: Long,
        val y: Long,
        val z: Long
    )

    private fun parse(input: List<String>): List<Pair<Point3, Point3>> {
        return input.map { line ->
            val (p1, p2) = line.split("@").map { it.trim() }
            val (x1, y1, z1) = p1.split(",").map { it.trim().toLong() }
            val (x2, y2, z2) = p2.split(",").map { it.trim().toLong() }
            Point3(x1, y1, z1) to Point3(x2, y2, z2)
        }
    }

    fun part1(input: List<String>, testArea: LongRange): Int {
        val parsed = parse(input)
        //println("pairs: ${generateTakes(parsed, 2).count()}")
        val intersects = generateTakes(parsed, 2).filter { (stone1, stone2) ->
            //println("$stone1, $stone2")
            val pos = intersectsXY(stone1, stone2)
            pos != null && testArea.contains(pos.first) && testArea.contains(pos.second)
        }
        //intersects.forEach { println(it) }
        return intersects.toList().size
    }

    fun LongRange.contains(value: BigDecimal): Boolean {
        return value >= this.first.toBigDecimal() && value <= this.last.toBigDecimal()
    }

    fun intersectsXY(stone1: Pair<Point3, Point3>, stone2: Pair<Point3, Point3>): Pair<BigDecimal, BigDecimal>? {
        val (p1, v1) = stone1
        val (p2, v2) = stone2
        if (v1.x.toDouble() / v1.y == v2.x.toDouble() / v2.y) {
            println("parallels: $v1, $v2")
            return null
        }
        val m2t = v1.x.toBigDecimal() * p1.y.toBigDecimal() + v1.y.toBigDecimal() * p2.x.toBigDecimal() - v1.x.toBigDecimal() * p2.y.toBigDecimal() - p1.x.toBigDecimal() * v1.y.toBigDecimal().setScale(4)
        val m2b = v1.x.toBigDecimal() * v2.y.toBigDecimal() - v2.x.toBigDecimal() * v1.y.toBigDecimal()
        val m2 = m2t.divide(m2b, RoundingMode.DOWN)
        val m1 = (p2.x.toBigDecimal() + v2.x.toBigDecimal() * m2 - p1.x.toBigDecimal()).setScale(4).divide(v1.x.toBigDecimal(), RoundingMode.DOWN)
        val x = p1.x.toBigDecimal() + v1.x.toBigDecimal() * m1
        val y = p1.y.toBigDecimal() + v1.y.toBigDecimal() * m1
        val checkX = p2.x.toBigDecimal() + v2.x.toBigDecimal() * m2
        val checkY = p2.y.toBigDecimal() + v2.y.toBigDecimal() * m2
        //check(abs(x - checkX) < 0.00001) { "x mismatch: $x, $checkX" }
        //check(abs(y - checkY) < 0.00001) { "y mismatch: $y, $checkY, $stone1, $stone2, $m1, $m2" }
        println("Diffs: ${x - checkX}, ${y - checkY}")

        if (m1 < BigDecimal.ZERO || m2 < BigDecimal.ZERO) {
            //println(abs(x - checkX))
            //println(abs(y - checkY))
            //println("in past: $m1, $m2")
            return null
        }
        /*if (abs(x - checkX) > 10) {
            println("x mismatch: $x, $checkX")
            return null
        }*/

        return x to y
    }

    fun part2(input: List<String>): Long {
        val parsed = parse(input)
        return 0L
    }
}

fun main() {
    val testInput = """
        18, 19, 22 @ -1, -1, -2
        19, 13, 30 @ -2,  1, -2
        20, 25, 34 @ -2, -2, -4
        12, 31, 28 @ -1, -2, -1
        20, 19, 15 @  1, -5, -3
    """.trimIndent().split("\n")
    val testTestRange = 7..27L
    println("------Tests------")
    println(Day24.part1(testInput, testTestRange))
    println(Day24.part2(testInput))

    val g = listOf(1, 2, 3, 4, 5)
    generateTakes(g, 2).forEach {
        println(it)
    }

    val h = listOf("a", "b")
    generateTakes(h, 2).forEach {
        println(it)
    }

    println("------Real------")
    val input = readInput(2023, 24)
    val testRange = 200_000_000_000_000..400_000_000_000_000
    println("Part 1 result: ${Day24.part1(input, testRange)}")
    println("Part 2 result: ${Day24.part2(input)}")
    //timingStatistics { Day24.part1(input, testRange) }
    timingStatistics { Day24.part2(input) }

    /*val numbers = readInput(2023, 25).map { it.toDouble() }.sorted()
    println(numbers.take(10).joinToString("\n"))*/
}