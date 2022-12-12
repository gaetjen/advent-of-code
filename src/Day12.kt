typealias Pos = Pair<Int, Int>

object Day12 {
    private fun parse(input: List<String>): Triple<List<List<Int>>, Pos, Pos> {
        var start: Pos = 0 to 0
        var end: Pos = 0 to 0
        val grid = input.mapIndexed { rowIdx, row ->
            row.mapIndexed { colIdx, char ->
                when (char) {
                    'S' -> {
                        start = rowIdx to colIdx
                        0
                    }

                    'E' -> {
                        end = rowIdx to colIdx
                        25
                    }

                    else -> (char - 'a')
                }
            }
        }
        return Triple(grid, start, end)
    }

    private fun getNeighbors(grid: List<List<Int>>, pos: Pos): List<Pos> {
        val height = grid[pos]
        return Direction.values().map {
            it.move(pos)
        }.filter { (r, c) -> r in grid.indices && c in grid[r].indices }
            .filter { grid[it] <= height + 1 }
    }

    private fun getReverseNeighbors(grid: List<List<Int>>, pos: Pos): List<Pos> {
        val height = grid[pos]
        return Direction.values().map {
            it.move(pos)
        }.filter { (r, c) -> r in grid.indices && c in grid[r].indices }
            .filter { grid[it] >= height - 1 }
    }

    fun part1(input: List<String>): Int {
        val (grid, start, end) = parse(input)
        val unvisited = grid.indices.map { rowIdx ->
            grid[rowIdx].indices.map { rowIdx to it }
        }.flatten().toMutableSet()
        unvisited.remove(start)
        var numSteps = 0
        var frontier = setOf(start)
        while (end in unvisited) {
            numSteps++
            // get all neighbors
            val allNeighbors = frontier.map { getNeighbors(grid, it) }.flatten().toSet()
            frontier = allNeighbors.intersect(unvisited)
            // remove from unvisited
            unvisited.removeAll(frontier)
        }
        return numSteps
    }

    operator fun List<List<Int>>.get(p: Pos): Int {
        return this[p.first][p.second]
    }

    fun part2(input: List<String>): Int {
        val (grid, _, end) = parse(input)
        val unvisited = grid.indices.map { rowIdx ->
            grid[rowIdx].indices.map { rowIdx to it }
        }.flatten().toMutableSet()
        unvisited.remove(end)
        var numSteps = 0
        var frontier = setOf(end)
        while (frontier.all { grid[it] > 0 }) {
            numSteps++
            // get all neighbors
            val allNeighbors = frontier.map { getReverseNeighbors(grid, it) }.flatten().toSet()
            frontier = allNeighbors.intersect(unvisited)
            // remove from unvisited
            unvisited.removeAll(frontier)
        }
        return numSteps
    }
}

fun main() {
    val testInput = """
        Sabqponm
        abcryxxl
        accszExk
        acctuvwj
        abdefghi
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day12.part1(testInput))
    println(Day12.part2(testInput))

    println("------Real------")
    val input = readInput("resources/day12")
    println(Day12.part1(input))
    println(Day12.part2(input))
}
