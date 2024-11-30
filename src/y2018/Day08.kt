package y2018

import util.readInput
import util.timingStatistics

data class Tree(
    val children: List<Tree>,
    val metaData: List<Int>
) {
    companion object {
        fun fromList(list: List<Int>): Pair<Tree, List<Int>> {
            require(list.isNotEmpty())
            val (childCount, metadataCount) = list
            return if (childCount == 0) {
                Tree(
                    listOf(),
                    list.drop(2).take(metadataCount)
                ) to list.drop(2 + metadataCount)
            } else {
                val (children, tail) = children(childCount, list.drop(2))
                Tree(
                    children,
                    tail.take(metadataCount)
                ) to tail.drop(metadataCount)
            }
        }

        fun children(
            n: Int,
            list: List<Int>
        ): Pair<List<Tree>, List<Int>> {
            require(n > 0)
            var remaining = list
            val result = (1..n).map {
                val foo = fromList(remaining)
                remaining = foo.second
                foo.first
            }
            return result to remaining
        }
    }

    fun allMetaData(): List<Int> {
        return metaData + children.flatMap { it.allMetaData() }
    }

    fun value(): Int {
        return if (children.isEmpty()) {
            metaData.sum()
        } else {
            metaData
                .filter { it in 1..(children.size) }
                .sumOf { children[it - 1].value() }
        }
    }
}

object Day08 {
    private fun parse(input: List<String>): Tree {
        val inputNumbers = input.first().split(" ").map { it.toInt() }

        return Tree.fromList(inputNumbers).first
    }

    fun part1(input: List<String>): Int {
        val parsed = parse(input)
        return parsed.allMetaData().sum()
    }

    fun part2(input: List<String>): Int {
        val parsed = parse(input)
        return parsed.value()
    }
}

fun main() {
    val testInput = """
        2 3 0 3 10 11 12 1 1 0 1 99 2 1 1 2
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day08.part1(testInput))
    println(Day08.part2(testInput))

    println("------Real------")
    val input = readInput(2018, 8)
    println("Part 1 result: ${Day08.part1(input)}")
    println("Part 2 result: ${Day08.part2(input)}")
    timingStatistics { Day08.part1(input) }
    timingStatistics { Day08.part2(input) }
}