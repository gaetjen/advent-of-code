package y2024

import util.Pos
import util.minus
import util.neighborsManhattan
import util.readInput
import util.timingStatistics
import y2022.Day15.manhattanDist

data class Plot(
    val pos: Pos,
    val crop: Char
)

object Day12 {
    private fun toRegions(input: List<String>): Set<Set<Plot>> {
        val plots = input.withIndex().flatMap { (row, line) ->
            line.withIndex().map { (col, crop) -> Plot(row to col, crop) }
        }.toSet()
        val lookup = plots.associateBy { it.pos }
        return y2017.Day12.connectedComponents(plots) { plot ->
            plot.pos.neighborsManhattan().mapNotNull { lookup[it] }.filter { it.crop == plot.crop }
        }
    }

    private fun circumference(region: Set<Pos>): Int {
        return region.sumOf { pos ->
            pos.neighborsManhattan().filter { it !in region }.size
        }
    }

    fun part1(input: List<String>): Int {
        val regions = toRegions(input)
        return regions.sumOf { region -> region.size * circumference(region.map { it.pos }.toSet()) }
    }

    data class FenceSection(
        val outsidePosition: Pos,
        val direction: Pos
    )

    private fun numberSides(region: Set<Pos>): Int {
        val fencedNeighbors = region
            .flatMap { pos ->
                pos.neighborsManhattan()
                    .filter { it !in region }
                    .map {
                        FenceSection(
                            it,
                            (it - pos)
                        )
                    }
            }.groupBy { it.direction }
        val fenceStretches = fencedNeighbors.flatMap { (neighborDirection, fenceSections) ->
            if (neighborDirection.first == 0) {
                fenceSections.groupBy { it.outsidePosition.second }.values
            } else {
                fenceSections.groupBy { it.outsidePosition.first }.values
            }
        }
        return fenceStretches.sumOf { stretch ->
            countConnected(stretch)
        }
    }

    private fun countConnected(stretch: List<FenceSection>): Int {
        return stretch
            .sortedBy { it.outsidePosition.first }
            .sortedBy { it.outsidePosition.second }
            .zipWithNext()
            .count { (a, b) -> a.outsidePosition.manhattanDist(b.outsidePosition) > 1 } + 1
    }

    fun part2(input: List<String>): Int {
        val regions = toRegions(input)
        return regions.sumOf { region -> region.size * numberSides(region.map { it.pos }.toSet()) }
    }
}

fun main() {
    val testInput = """
        RRRRIICCFF
        RRRRIICCCF
        VVRRRCCFFF
        VVRCCCJFFF
        VVVVCJJCFE
        VVIVCCJJEE
        VVIIICJJEE
        MIIIIIJJEE
        MIIISIJEEE
        MMMISSJEEE
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day12.part1(testInput))
    println(Day12.part2(testInput))

    println("------Real------")
    val input = readInput(2024, 12)
    println("Part 1 result: ${Day12.part1(input)}")
    println("Part 2 result: ${Day12.part2(input)}")
    timingStatistics { Day12.part1(input) }
    timingStatistics { Day12.part2(input) }
}