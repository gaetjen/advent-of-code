package y2023

import util.Cardinal
import util.Pos
import util.get
import util.plus
import util.printGrid
import util.readInput
import y2022.Day15.manhattanDist
import y2023.Day14.coerce
import java.util.PriorityQueue

object Day17 {
    data class Block(
        val pos: Pos,
        val heatLoss: Int,
    )

    private fun parse(input: List<String>): List<List<Block>> {
        return input.mapIndexed { row, line ->
            line.mapIndexedNotNull { col, c ->
                Block(
                    pos = Pos(row, col),
                    heatLoss = c.digitToInt()
                )
            }
        }
    }

    data class Step(
        val direction: Cardinal,
        val totalHeatLoss: Int,
        val pos: Pos,
        val consecutiveCount: Int,
        val history: List<Pos>
    ) {
        fun heuristic(target: Pos): Int {
            return totalHeatLoss + pos.manhattanDist(target)
        }
    }

    data class SearchState(
        val pos: Pos,
        val consecutiveCount: Int,
        val direction: Cardinal
    )

    fun part1(input: List<String>): Long {
        val parsed = parse(input)
        val target = parsed.last().last().pos
        val queue = PriorityQueue<Step>(compareBy {
            it.heuristic(target)
        })
        queue.add(Step(Cardinal.EAST, 0, Pos(0, 0), 0, listOf(0 to 0)))
        var end: Step? = null
        val minsSoFar = mutableMapOf(
            SearchState(
                pos = Pos(0, 0),
                consecutiveCount = 0,
                direction = Cardinal.EAST
            ) to 0
        )
        while (queue.isNotEmpty()) {
            val nextToExpand = queue.poll()!!

            if (nextToExpand.pos == target) {
                end = nextToExpand
                break
            }
            val nexts = expand(nextToExpand, parsed, target).filter {
                val searchState = SearchState(
                    pos = it.pos,
                    consecutiveCount = it.consecutiveCount,
                    direction = it.direction
                )
                if (minsSoFar[searchState] == null || minsSoFar[searchState]!! > it.totalHeatLoss) {
                    if (minsSoFar[searchState] != null) {
                        println("updating optimum")
                    }
                    minsSoFar[searchState] = it.totalHeatLoss
                    true
                } else {
                    false
                }
            }
            queue.addAll(nexts)
        }
        if (end != null) {
            printGrid(
                end.history.associateWith { "#" }
            )
        }
        return end?.totalHeatLoss?.toLong() ?: error("didn't find a path")
    }

    fun expand(step: Step, blocks: List<List<Block>>, target: Pos): List<Step> {
        return Cardinal.entries.filter {
            it.relativePos + step.direction.relativePos != 0 to 0 &&
                    (step.consecutiveCount < 3 || it != step.direction) &&
                    it.of(step.pos).coerce(target) == it.of(step.pos)
        }.map {
            val newPos = it.of(step.pos)
            Step(
                direction = it,
                totalHeatLoss = step.totalHeatLoss + blocks[newPos].heatLoss,
                pos = newPos,
                consecutiveCount = if (it == step.direction) step.consecutiveCount + 1 else 1,
                history = step.history + newPos
            )
        }
    }

    fun part2(input: List<String>): Long {
        val parsed = parse(input)
        val target = parsed.last().last().pos
        val queue = PriorityQueue<Step>(compareBy {
            it.heuristic(target)
        })
        queue.add(Step(Cardinal.EAST, 0, Pos(0, 0), 0, listOf(0 to 0)))
        var end: Step? = null
        val minsSoFar = mutableMapOf(
            SearchState(
                pos = Pos(0, 0),
                consecutiveCount = 0,
                direction = Cardinal.EAST
            ) to 0
        )
        while (queue.isNotEmpty()) {
            val nextToExpand = queue.poll()!!

            if (nextToExpand.pos == target) {
                end = nextToExpand
                break
            }
            val nexts = expandUltra(nextToExpand, parsed, target).filter {
                val searchState = SearchState(
                    pos = it.pos,
                    consecutiveCount = it.consecutiveCount,
                    direction = it.direction
                )
                if (minsSoFar[searchState] == null || minsSoFar[searchState]!! > it.totalHeatLoss) {
                    if (minsSoFar[searchState] != null) {
                        println("updating optimum")
                    }
                    minsSoFar[searchState] = it.totalHeatLoss
                    true
                } else {
                    false
                }
            }
            queue.addAll(nexts)
        }
        if (end != null) {
            printGrid(
                end.history.associateWith { "#" }
            )
        }
        return end?.totalHeatLoss?.toLong() ?: error("didn't find a path")
    }

    fun expandUltra(step: Step, blocks: List<List<Block>>, target: Pos): List<Step> {
        return Cardinal.entries.filter {
            it.relativePos + step.direction.relativePos != 0 to 0 &&
                    it.of(step.pos).coerce(target) == it.of(step.pos) &&
                    (it == step.direction && step.consecutiveCount < 10  || it != step.direction && step.consecutiveCount >= 4)
        }.map {
            val newPos = it.of(step.pos)
            Step(
                direction = it,
                totalHeatLoss = step.totalHeatLoss + blocks[newPos].heatLoss,
                pos = newPos,
                consecutiveCount = if (it == step.direction) step.consecutiveCount + 1 else 1,
                history = step.history + newPos
            )
        }
    }
}

fun main() {
    val testInput = """
        2413432311323
        3215453535623
        3255245654254
        3446585845452
        4546657867536
        1438598798454
        4457876987766
        3637877979653
        4654967986887
        4564679986453
        1224686865563
        2546548887735
        4322674655533
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day17.part1(testInput))
    println(Day17.part2(testInput))

    println("------Real------")
    val input = readInput(2023, 17)
    println("Part 1 result: ${Day17.part1(input)}")
    println("Part 2 result: ${Day17.part2(input)}")
    //timingStatistics { Day17.part1(input) }
    //timingStatistics { Day17.part2(input) }
}