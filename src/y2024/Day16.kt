package y2024

import util.Cardinal
import util.Pos
import util.Turn
import util.readInput
import util.timingStatistics
import java.util.PriorityQueue

data class ReindeerMaze(
    val walls: Set<Pos>,
    val start: Pos,
    val end: Pos,
)

object Day16 {
    private fun parse(input: List<String>): ReindeerMaze {
        var start = 0 to 0
        var end = 0 to 0
        val walls = mutableSetOf<Pos>()
        input.forEachIndexed { row, line ->
            line.forEachIndexed { col, c ->
                when (c) {
                    '#' -> walls.add(row to col)
                    'S' -> start = row to col
                    'E' -> end = row to col
                }
            }
        }
        return ReindeerMaze(walls, start, end)
    }

    data class ReindeerState(
        val pos: Pos,
        val score: Int,
        val direction: Cardinal
    ) {
        fun next() = listOf(
            ReindeerState(direction.of(pos), score + 1, direction),
            ReindeerState(direction.turn(Turn.LEFT).of(pos), score + 1001, direction.turn(Turn.LEFT)),
            ReindeerState(direction.turn(Turn.RIGHT).of(pos), score + 1001, direction.turn(Turn.RIGHT))
        )
    }

    fun part1(input: List<String>): Int {
        val maze = parse(input)
        val start = ReindeerState(maze.start, 0, Cardinal.EAST)
        return lowestScore(maze, start)
    }

    private fun lowestScore(
        maze: ReindeerMaze,
        start: ReindeerState
    ): Int {
        val visited = mutableSetOf(start.pos)
        val queue = PriorityQueue<ReindeerState>(compareBy { it.score })
        queue.add(start)
        while (queue.isNotEmpty()) {
            val nextToExpand = queue.poll()!!
            nextToExpand.next().filter { it.pos !in maze.walls && it.pos !in visited }.forEach {
                if (it.pos == maze.end) {
                    return it.score
                }
                queue.add(it)
                visited.add(it.pos)
            }
        }
        error("path not found")
    }

    fun part2(input: List<String>): Int {
        val maze = parse(input)
        val start = ReindeerState(maze.start, 0, Cardinal.EAST)
        return validTiles(maze, start)
    }

    private fun validTiles(
        maze: ReindeerMaze,
        start: ReindeerState
    ): Int {
        val bestScores = mutableMapOf((start.pos to start.direction) to start.score)
        val bestParents = mutableMapOf<Pair<Pos, Cardinal>, MutableSet<Pair<Pos, Cardinal>>>()
        val queue = PriorityQueue<ReindeerState>(compareBy { it.score })
        queue.add(start)
        var endScore: ReindeerState? = null
        while (queue.isNotEmpty()) {
            val nextToExpand = queue.poll()!!
            if (endScore != null && nextToExpand.score > endScore.score) {
                break
            }
            nextToExpand.next().filter { it.pos !in maze.walls }.forEach { newState ->
                when (val bestScore = bestScores[newState.pos to newState.direction]) {
                    null -> {
                        bestScores[newState.pos to newState.direction] = newState.score
                        queue.add(newState)
                        if (newState.pos == maze.end) {
                            endScore = newState
                        }
                        bestParents[newState.pos to newState.direction] = mutableSetOf(nextToExpand.pos to nextToExpand.direction)
                    }

                    else -> {
                        if (newState.score <= bestScore) {
                            bestParents[newState.pos to newState.direction]?.let { it.add(nextToExpand.pos to nextToExpand.direction) }
                        }
                    }
                }
            }
        }
        var frontier = setOf(endScore!!.pos to endScore!!.direction)
        val onBestPaths = mutableSetOf(endScore!!.pos to endScore!!.direction)
        while (frontier.isNotEmpty()) {
            frontier = frontier.flatMap { f ->
                bestParents[f] ?: emptyList()
            }.toSet() - onBestPaths
            onBestPaths.addAll(frontier)
        }
        return onBestPaths.distinctBy { it.first }.count()
    }
}

fun main() {
    val testInput = """
        #################
        #...#...#...#..E#
        #.#.#.#.#.#.#.#.#
        #.#.#.#...#...#.#
        #.#.#.#.###.#.#.#
        #...#.#.#.....#.#
        #.#.#.#.#.#####.#
        #.#...#.#.#.....#
        #.#.#####.#.###.#
        #.#.#.......#...#
        #.#.###.#####.###
        #.#.#...#.....#.#
        #.#.#.#####.###.#
        #.#.#.........#.#
        #.#.#.#########.#
        #S#.............#
        #################
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day16.part1(testInput))
    println(Day16.part2(testInput))

    println("------Real------")
    val input = readInput(2024, 16)
    println("Part 1 result: ${Day16.part1(input)}")
    println("Part 2 result: ${Day16.part2(input)}")
    timingStatistics { Day16.part1(input) }
    timingStatistics { Day16.part2(input) }
}