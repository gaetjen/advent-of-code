package y2024

import util.Cardinal
import util.Pos
import util.Turn
import util.readInput
import util.timingStatistics

data class MapState(
    val size: Pos,
    val guard: Guard,
    val blockedTiles: Set<Pos>
) {
    fun outsideMap(p: Pos): Boolean {
        return p.first < 0 || p.second < 0 || p.first > size.first - 1 || p.second > size.second - 1
    }

    fun walkGuard(): MapState {
        val inFrontOfGuard = guard.direction.of(guard.pos)
        val newDirection = if (inFrontOfGuard in blockedTiles) {
            guard.direction.turn(Turn.RIGHT)
        } else {
            guard.direction
        }
        val newPosition = newDirection.of(guard.pos)
        return if (newPosition in blockedTiles) {
            this.copy(
                guard = Guard(
                    guard.direction.backwardsOf(guard.pos),
                    newDirection.turn(Turn.RIGHT)
                )
            )
        } else {
            this.copy(guard = Guard(newPosition, newDirection))
        }
    }
}

data class Guard(
    val pos: Pos,
    val direction: Cardinal
)

object Day06 {
    private fun parse(input: List<String>): MapState {
        val size = Pos(input.size, input[0].length)
        var guard: Guard? = null
        val blocked = input.flatMapIndexed { row: Int, s: String ->
            s.mapIndexedNotNull { col, c ->
                when (c) {
                    '#' -> {
                        row to col
                    }
                    '^' -> {
                        guard = Guard(row to col, Cardinal.NORTH)
                        null
                    }
                    else -> null
                }
            }
        }.toSet()
        return MapState(
            size,
            guard!!,
            blocked
        )
    }

    fun part1(input: List<String>): Int {
        val mapState = parse(input)
        val visited = visitAll(mapState)
        return visited.distinctBy { it.pos }.size
    }

    fun part2(input: List<String>): Int {
        val mapState = parse(input)
        val original = mapState.copy()
        val visited = visitAll(mapState)
        val newObstructions =  visited.filter { candidate ->
            addObstructionLeadsToLoop(original, candidate)
        }
        return newObstructions.distinctBy { it.pos }.size
    }

    private fun visitAll(mapState: MapState): MutableSet<Guard> {
        var walkingMapState = mapState
        val visited = mutableSetOf<Guard>()
        while (true) {
            walkingMapState = walkingMapState.walkGuard()
            if (!walkingMapState.outsideMap(walkingMapState.guard.pos)) {
                visited.add(walkingMapState.guard)
            } else {
                break
            }
        }
        return visited
    }

    private fun addObstructionLeadsToLoop(
        mapState: MapState,
        candidate: Guard
    ): Boolean {
        var newMap = mapState.copy(
            blockedTiles = mapState.blockedTiles + setOf(candidate.pos),
        )
        val newVisited = mutableSetOf<Guard>()
        while (true) {
            newMap = newMap.walkGuard()
            if (!newMap.outsideMap(newMap.guard.pos)) {
                if (newMap.guard in newVisited) {
                    return true
                } else {
                    newVisited.add(newMap.guard)
                }
            } else {
                return false
            }
        }
    }
}

fun main() {
    val testInput = """
        ....#.....
        .........#
        ..........
        ..#.......
        .......#..
        ..........
        .#..^.....
        ........#.
        #.........
        ......#...
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day06.part1(testInput))
    println(Day06.part2(testInput))

    println("------Real------")
    val input = readInput(2024, 6)
    println("Part 1 result: ${Day06.part1(input)}")
    println("Part 2 result: ${Day06.part2(input)}")
    timingStatistics { Day06.part1(input) }
    timingStatistics { Day06.part2(input) }
}