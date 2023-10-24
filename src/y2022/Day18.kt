package y2022

import util.readInput

typealias Vector = Triple<Int, Int, Int>

object Day18 {
    private fun parse(input: List<String>): Set<Vector> {
        return input.map { line ->
            val (x, y, z) = line.split(",").map { it.toInt() }
            Vector(x, y, z)
        }.toSet()
    }

    fun Vector.neighbors(): List<Vector> {
        return listOf(
            Vector(first - 1, second, third),
            Vector(first + 1, second, third),
            Vector(first, second - 1, third),
            Vector(first, second + 1, third),
            Vector(first, second, third - 1),
            Vector(first, second, third + 1),
        )
    }

    fun part1(input: List<String>): Long {
        val positions = parse(input)
        return positions.map { it.neighbors() }.flatten().filter { it !in positions }.size.toLong()
    }

    fun part2(input: List<String>): Long {
        val lava = parse(input)
        val neighboring = lava.map { it.neighbors() }.flatten().filter { it !in lava }
        val minX = neighboring.minOf { it.first }
        val minY = neighboring.minOf { it.second }
        val minZ = neighboring.minOf { it.third }
        val maxX = neighboring.maxOf { it.first }
        val maxY = neighboring.maxOf { it.second }
        val maxZ = neighboring.maxOf { it.third }
        val air = (minX - 1..maxX + 1).flatMap { x ->
            (minY - 1..maxY + 1).flatMap { y ->
                (minZ - 1..maxZ + 1).map { Vector(x, y, it) }
            }
        }.toSet() - lava
        val outside = connected(Triple(minX - 1, minY - 1, minZ - 1), air)
        return neighboring.filter { it in outside }.size.toLong()
    }

    private fun connected(seed: Vector, others: Set<Vector>): Set<Vector> {
        val frontier = mutableSetOf(seed)
        val result = mutableSetOf<Vector>()
        while (frontier.isNotEmpty()) {
            val current = frontier.first()
            frontier.remove(current)
            result.add(current)
            frontier.addAll(current.neighbors().filter { it in others && it !in result })
        }
        return result
    }
}

fun main() {
    val testInput = """
        2,2,2
        1,2,2
        3,2,2
        2,1,2
        2,3,2
        2,2,1
        2,2,3
        2,2,4
        2,2,6
        1,2,5
        3,2,5
        2,1,5
        2,3,5
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day18.part1(testInput))
    println(Day18.part2(testInput))

    println("------Real------")
    val input = readInput("resources/2022/day18")
    println(Day18.part1(input))
    println(Day18.part2(input))
}

