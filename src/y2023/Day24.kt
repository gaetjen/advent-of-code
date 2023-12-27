package y2023

import io.ksmt.KContext
import io.ksmt.expr.KExpr
import io.ksmt.solver.z3.KZ3Solver
import io.ksmt.sort.KIntSort
import io.ksmt.utils.getValue
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
        val intersects = generateTakes(parsed, 2).filter { (stone1, stone2) ->
            val pos = intersectsXY(stone1, stone2)
            pos != null && testArea.contains(pos.first) && testArea.contains(pos.second)
        }
        return intersects.toList().size
    }

    fun LongRange.contains(value: BigDecimal): Boolean {
        return value >= this.first.toBigDecimal() && value <= this.last.toBigDecimal()
    }

    fun intersectsXY(stone1: Pair<Point3, Point3>, stone2: Pair<Point3, Point3>): Pair<BigDecimal, BigDecimal>? {
        val (p1, v1) = stone1
        val (p2, v2) = stone2
        if (v1.x.toDouble() / v1.y == v2.x.toDouble() / v2.y) {
            return null
        }
        val m2t =
            v1.x.toBigDecimal() * p1.y.toBigDecimal() + v1.y.toBigDecimal() * p2.x.toBigDecimal() - v1.x.toBigDecimal() * p2.y.toBigDecimal() - p1.x.toBigDecimal() * v1.y.toBigDecimal()
                .setScale(4)
        val m2b = v1.x.toBigDecimal() * v2.y.toBigDecimal() - v2.x.toBigDecimal() * v1.y.toBigDecimal()
        val m2 = m2t.divide(m2b, RoundingMode.DOWN)
        val m1 = (p2.x.toBigDecimal() + v2.x.toBigDecimal() * m2 - p1.x.toBigDecimal()).setScale(4)
            .divide(v1.x.toBigDecimal(), RoundingMode.DOWN)
        val x = p1.x.toBigDecimal() + v1.x.toBigDecimal() * m1
        val y = p1.y.toBigDecimal() + v1.y.toBigDecimal() * m1

        if (m1 < BigDecimal.ZERO || m2 < BigDecimal.ZERO) {
            return null
        }

        return x to y
    }

    fun part2(input: List<String>): KExpr<KIntSort> {
        val parsed = parse(input)
        val ctx = KContext()
        val (h1, h2, h3) = parsed.take(3)
        with(ctx) {
            val t1 by intSort
            val t2 by intSort
            val t3 by intSort
            val x by intSort
            val y by intSort
            val z by intSort
            val dx by intSort
            val dy by intSort
            val dz by intSort

            val constraints = listOf(
                t1 ge 0.expr,
                t2 ge 0.expr,
                t3 ge 0.expr,
                x + dx * t1 eq h1.first.x.expr + h1.second.x.expr * t1,
                x + dx * t2 eq h2.first.x.expr + h2.second.x.expr * t2,
                x + dx * t3 eq h3.first.x.expr + h3.second.x.expr * t3,

                y + dy * t1 eq h1.first.y.expr + h1.second.y.expr * t1,
                y + dy * t2 eq h2.first.y.expr + h2.second.y.expr * t2,
                y + dy * t3 eq h3.first.y.expr + h3.second.y.expr * t3,

                z + dz * t1 eq h1.first.z.expr + h1.second.z.expr * t1,
                z + dz * t2 eq h2.first.z.expr + h2.second.z.expr * t2,
                z + dz * t3 eq h3.first.z.expr + h3.second.z.expr * t3,
            ).reduce { acc, expr -> acc and expr }


            KZ3Solver(this).use { solver ->
                solver.assert(constraints)
                solver.check()
                val model = solver.model()
                return model.eval(x) + model.eval(y) + model.eval(z)
            }
        }
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

    println("------Real------")
    val input = readInput(2023, 24)
    val testRange = 200_000_000_000_000..400_000_000_000_000
    println("Part 1 result: ${Day24.part1(input, testRange)}")
    println("Part 2 result: ${Day24.part2(input)}")
    timingStatistics { Day24.part1(input, testRange) }
    timingStatistics { Day24.part2(input) }
}