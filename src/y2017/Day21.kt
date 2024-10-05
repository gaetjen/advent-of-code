package y2017

import util.readInput
import util.timingStatistics

object Day21 {
    val start = listOf(".#.", "..#", "###")
    private fun parse(input: List<String>): Map<List<String>, List<String>> {
        return input.flatMap { line ->
            val (lh, rh) = line.split(" => ")
            val output = rh.split("/")
            flipAndRotate(lh).map { it to output }
        }.toMap()
    }

    private fun flipAndRotate(input: String): List<List<String>> {
        val inputGrid = input.split("/")
        val variants = listOf(
            inputGrid,
            inputGrid.reversed(),
            inputGrid.map { it.reversed() },
            inputGrid.reversed().map { it.reversed() },
            (inputGrid.indices.reversed()).map { col -> inputGrid.indices.map { row -> inputGrid[row][col] }.joinToString("") },
            (inputGrid.indices).map { col -> inputGrid.indices.map { row -> inputGrid[row][col] }.joinToString("") },
            (inputGrid.indices).map { col -> inputGrid.indices.reversed().map { row -> inputGrid[row][col] }.joinToString("") },
            (inputGrid.indices.reversed()).map { col -> inputGrid.indices.reversed().map { row -> inputGrid[row][col] }.joinToString("") }
        )
        return variants
    }

    fun part1(
        input: List<String>,
        iterations: Int
    ): Int {
        val rules = parse(input)
        var image = start
        repeat(iterations) {
            val chunks = if (image.size % 2 == 0) {
                imageChunks(2, image)
            } else {
                imageChunks(3, image)
            }
            val newChunks = chunks.map { chunkRow ->
                chunkRow.map {
                    rules[it]
                        ?: error("missing rule for $it")
                }
            }
            image = stitch(newChunks)

        }
        return image.joinToString("").count { it == '#' }
    }

    private fun stitch(imageChunks: List<List<List<String>>>): List<String> {
        return imageChunks.flatMap { row ->
            row.first().indices.map { rowOffset ->
                row.joinToString("") {
                    it[rowOffset]
                }
            }
        }
    }

    private fun imageChunks(
        chunkSize: Int,
        imageGrid: List<String>
    ): List<List<List<String>>> {
        return (0..<imageGrid.size / chunkSize).map { row ->
            (0..<imageGrid.size / chunkSize).map { col ->
                (0..<chunkSize).map { rowOffset ->
                    (0..<chunkSize).map { colOffset ->
                        imageGrid[row * chunkSize + rowOffset][col * chunkSize + colOffset]
                    }.joinToString("")
                }
            }
        }
    }
}

fun main() {
    val testInput = """
        ../.# => ##./#../...
        .#./..#/### => #..#/..../..../#..#
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day21.part1(testInput, 2))

    println("------Real------")
    val input = readInput(2017, 21)
    println("Part 1 result: ${Day21.part1(input, 5)}")
    println("Part 2 result: ${Day21.part1(input, 18)}")
    timingStatistics { Day21.part1(input, 5) }
    timingStatistics { Day21.part1(input, 18) }
}