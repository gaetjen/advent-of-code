package y2016

import util.Pos
import util.measuredTime
import util.neighborsManhattan
import util.readInput

object Day24 {
    data class Ducts(
        val start: Pos,
        val targets: List<Pos>,
        val passages: Set<Pos>
    ) {
        fun distance(p1: Pos, p2: Pos): Int {
            var steps = 0
            var frontier = setOf(p1)
            val closed = mutableSetOf<Pos>()
            while (p2 !in frontier) {
                closed.addAll(frontier)
                frontier = frontier.flatMap { s ->
                    s.neighborsManhattan().filter { it in passages && it !in closed }
                }.toSet()
                steps++
            }
            return steps
        }
    }

    private fun parse(input: List<String>): Ducts {
        var start = -1 to -1
        val targets = mutableListOf<Pos>()
        val passages = input.flatMapIndexed { row: Int, s: String ->
            s.mapIndexed { col, c ->
                if (c == '.' || c.isDigit()) {
                    if (c == '0') {
                        start = row to col
                    } else if (c.isDigit()) {
                        targets.add(row to col)
                    }
                    row to col
                } else {
                    null
                }
            }
        }.filterNotNull().toSet()
        return Ducts(
            start,
            targets,
            passages
        )
    }

    fun part1(input: List<String>): Int {
        val ducts = parse(input)
        val pairDistances = getPairDistances(ducts)

        return ducts.targets.flatMap {
            val startDistance = pairDistances[ducts.start to it]!!
            allVisitDistances(
                startDistance,
                it,
                ducts.targets.toSet() - it,
                pairDistances
            )
        }.min()
    }

    private fun getPairDistances(ducts: Ducts): MutableMap<Pair<Pos, Pos>, Int> {
        val pairDistances = mutableMapOf<Pair<Pos, Pos>, Int>()
        val allTargets = ducts.targets + ducts.start
        allTargets.forEachIndexed { idx, p1 ->
            allTargets.drop(idx + 1).forEach { p2 ->
                val d = ducts.distance(p1, p2)
                pairDistances[p1 to p2] = d
                pairDistances[p2 to p1] = d
            }
        }
        return pairDistances
    }

    fun allVisitDistances(
        distanceSoFar: Int,
        current: Pos,
        unvisited: Set<Pos>,
        distances: Map<Pair<Pos, Pos>, Int>,
        returnTo: Pos? = null
    ): List<Int> {
        if (unvisited.isEmpty()) {
            return if (returnTo != null) {
                listOf(distanceSoFar + distances[current to returnTo]!!)
            } else {
                listOf(distanceSoFar)
            }
        }
        return unvisited.flatMap {
            allVisitDistances(
                distanceSoFar = distanceSoFar + distances[current to it]!!,
                current = it,
                unvisited = unvisited - it,
                distances = distances,
                returnTo = returnTo
            )
        }
    }

    fun part2(input: List<String>): Int {
        val ducts = parse(input)
        val pairDistances = getPairDistances(ducts)

        return ducts.targets.flatMap {
            val startDistance = pairDistances[ducts.start to it]!!
            allVisitDistances(
                startDistance,
                it,
                ducts.targets.toSet() - it,
                pairDistances,
                returnTo = ducts.start
            )
        }.min()
    }
}

fun main() {
    val testInput = """
        ###########
        #0.1.....2#
        #.#######.#
        #4.......3#
        ###########
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day24.part1(testInput))
    println(Day24.part2(testInput))

    println("------Real------")
    val input = readInput("resources/2016/day24")
    measuredTime { Day24.part1(input) }
    measuredTime { Day24.part2(input) }
}