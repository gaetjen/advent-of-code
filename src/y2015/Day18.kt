package y2015

import util.getNeighbors
import util.printMatrix
import util.readInput

object Day18 {
    private fun parse(input: List<String>): List<List<Boolean>> {
        return input.map { line ->
            line.map { it == '#' }
        }
    }

    fun part1(input: List<String>, steps: Int): Int {
        var lights = parse(input)
        repeat(steps) {
            lights = nextLights(lights)
        }
        printMatrix(lights) {
            if (it) "##" else "  "
        }
        return lights.flatten().count { it }
    }

    private fun nextLights(lights: List<List<Boolean>>): List<List<Boolean>> {
        return lights.mapIndexed { row, ls ->
            ls.mapIndexed { col, on ->
                switch(on, getNeighbors(lights, row to col))
            }
        }
    }

    private fun switch(on: Boolean, neighbors: List<Boolean>): Boolean {
        val neighborsOn = neighbors.count { it }
        return if (on) {
            neighborsOn in listOf(2, 3)
        } else {
            neighborsOn == 3
        }
    }

    fun part2(input: List<String>, steps: Int): Int {
        var lights = withCornersOn(parse(input))
        repeat(steps) {
            lights = withCornersOn(nextLights(lights))
        }
        return lights.flatten().count { it }
    }

    private fun withCornersOn(lights: List<List<Boolean>>): List<List<Boolean>> {
        val corners = setOf(
            0 to 0,
            0 to lights.size - 1,
            lights.size - 1 to 0,
            lights.size - 1 to lights.size - 1
        )
        return lights.mapIndexed { rowIdx, row ->
            row.mapIndexed { col, on ->
                if(rowIdx to col in corners) true else on
            }
        }
    }
}

fun main() {
    val testInput = """
        .#.#.#
        ...##.
        #....#
        ..#...
        #.#..#
        ####..
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day18.part1(testInput, 4))
    println(Day18.part2(testInput, 5))

    println("------Real------")
    val input = readInput("resources/2015/day18")
    println(Day18.part1(input, 100))
    println(Day18.part2(input, 100))
}
