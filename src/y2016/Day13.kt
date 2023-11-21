package y2016

import util.Cardinal
import util.Pos
import util.printMatrix

object Day13 {
    fun isWall(favorite: Int, x: Int, y: Int): Boolean {
        val n = x * x + 3 * x + 2 * x * y + y + y * y + favorite
        val binary = n.toString(2)
        return binary.count { it == '1' } % 2 == 1
    }

    fun Pos.neighborsManhattan(): List<Pos> {
        return Cardinal.entries.map { it.of(this) }
    }

    fun part1(favorite: Int, target: Pos): Int {
        val start: Pos = 1 to 1
        var steps = 0
        val explored = mutableSetOf(start)
        var current = setOf(start)
        while (target !in current) {
            current = current
                .flatMap { it.neighborsManhattan() }
                .filter { it.first >= 0 && it.second >= 0 }
                .filter { !isWall(favorite, it.first, it.second) }
                .filter { it !in explored }
                .toSet()
            explored.addAll(current)
            steps++
        }
        return steps
    }

    fun part2(favorite: Int): Int {
        val start: Pos = 1 to 1
        var steps = 0
        val explored = mutableSetOf(start)
        var current = setOf(start)
        while (steps < 50) {
            current = current
                .flatMap { it.neighborsManhattan() }
                .filter { it.first >= 0 && it.second >= 0 }
                .filter { !isWall(favorite, it.first, it.second) }
                .filter { it !in explored }
                .toSet()
            explored.addAll(current)
            steps++
        }
        return explored.size
    }
}

fun main() {
    val testFavorite = 10
    val testTargetPos = 7 to 4
    println("------Tests------")
    println(Day13.part1(testFavorite, testTargetPos))

    println("------Real------")
    val favorite = 1350
    val targetPos = 31 to 39
    println(Day13.part1(favorite, targetPos))
    println(Day13.part2(favorite))

    val officePlan = List(50) { y ->
        List(50) { x->
            Day13.isWall(favorite, x, y)
        }
    }
    printMatrix(officePlan) {
        if (it) "██" else "  "
    }
}