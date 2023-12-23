package y2023

import util.Cardinal
import util.Pos
import util.neighborsManhattan
import util.readInput
import util.timingStatistics

object Day23 {
    data class Tile(
        val pos: Pos,
        val slope: Cardinal?
    ) {
        fun neighbors(tiles: Map<Pos, Tile>, visited: Set<Pos>): List<Pair<Int, Int>> {
            val nexts = slope?.let {
                listOf(it.of(pos))
            } ?: pos.neighborsManhattan()
            return nexts.filter { it in tiles && it !in visited }
        }
    }

    private val slopeToCardinal = mapOf(
        '>' to Cardinal.EAST,
        '<' to Cardinal.WEST,
        '^' to Cardinal.NORTH,
        'v' to Cardinal.SOUTH
    )

    private fun parse(input: List<String>): List<Tile> {
        return input.mapIndexed { row, line ->
            line.mapIndexedNotNull { column, c ->
                when (c) {
                    '#' -> null
                    '.' -> Tile(row to column, null)
                    else -> Tile(row to column, slopeToCardinal[c])
                }
            }
        }.flatten()
    }

    fun part1(input: List<String>): Long {
        val parsed = parse(input)
        val start = parsed.first()
        val end = parsed.last()
        val tileLookup = parsed.associateBy { it.pos }
        var paths = listOf(setOf(start.pos) to start)
        val finished = mutableListOf<Set<Pos>>()
        while (paths.isNotEmpty()) {
            paths = paths.flatMap { (visited, last) ->
                val n = last.neighbors(tileLookup, visited).map { pos ->
                    visited + pos to tileLookup[pos]!!
                }
                if (n.isEmpty()) {
                    finished.add(visited)
                }
                n
            }
        }
        return finished.filter { end.pos in it }.maxOf { it.size }.toLong() - 1
    }

    fun neighbors(pos: Pos, tiles: Set<Pos>, visited: Set<Pos>): List<Pair<Int, Int>> {
        return pos.neighborsManhattan().filter { it in tiles && it !in visited }
    }

    fun part2(input: List<String>): Long {
        val parsed = parse(input)
        val start = parsed.first().pos
        val end = parsed.last().pos
        val tiles = parsed.map { it.pos }.toSet()
        val intersections = tiles.filter { tile ->
            neighbors(tile, tiles, emptySet()).size > 2 || tile == start || tile == end
        }.toSet()
        val intersectionNeighbors = intersections.associateWith { startIntersection ->
            neighbors(startIntersection, tiles, emptySet()).map { firstStep ->
                pathToNextIntersection(startIntersection, firstStep, tiles, intersections)
            }
        }
        var paths = listOf(Path(setOf(start), start, 0))
        val finished = mutableListOf<Path>()
        while (paths.isNotEmpty()) {
            paths = paths.flatMap { path ->
                val n = intersectionNeighbors[path.last]!!.map { (next, distance) ->
                    Path(path.visited + next, next, path.distance + distance)
                }
                n.firstOrNull { it.last == end }?.let {
                    finished.add(it)
                    emptyList()
                } ?: n.filter { it.last !in path.visited }
            }
        }
        return finished.maxOf { it.distance }.toLong()
    }

    data class Path(
        val visited: Set<Pos>,
        val last: Pos,
        val distance: Int
    )

    private fun pathToNextIntersection(
        startIntersection: Pos,
        firstStep: Pos,
        tiles: Set<Pos>,
        intersections: Set<Pos>
    ): Pair<Pos, Int> {
        val visited = mutableSetOf(startIntersection, firstStep)
        var last = firstStep
        while (last !in intersections) {
            val next = neighbors(last, tiles, visited).first { it !in visited }
            visited.add(next)
            last = next
        }
        return last to visited.size - 1
    }
}

fun main() {
    val testInput = """
        #.#####################
        #.......#########...###
        #######.#########.#.###
        ###.....#.>.>.###.#.###
        ###v#####.#v#.###.#.###
        ###.>...#.#.#.....#...#
        ###v###.#.#.#########.#
        ###...#.#.#.......#...#
        #####.#.#.#######.#.###
        #.....#.#.#.......#...#
        #.#####.#.#.#########v#
        #.#...#...#...###...>.#
        #.#.#v#######v###.###v#
        #...#.>.#...>.>.#.###.#
        #####v#.#.###v#.#.###.#
        #.....#...#...#.#.#...#
        #.#########.###.#.#.###
        #...###...#...#...#.###
        ###.###.#.###v#####v###
        #...#...#.#.>.>.#.>.###
        #.###.###.#.###.#.#v###
        #.....###...###...#...#
        #####################.#
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day23.part1(testInput))
    println(Day23.part2(testInput))

    println("------Real------")
    val input = readInput(2023, 23)
    println("Part 1 result: ${Day23.part1(input)}")
    println("Part 2 result: ${Day23.part2(input)}")
    timingStatistics { Day23.part1(input) }
    timingStatistics { Day23.part2(input) }
}