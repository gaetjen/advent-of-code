package y2016

import util.Direction
import util.Pos
import util.get
import util.inverse
import util.readInput

object Day02 {
    val keyPad = listOf(
        listOf(7, 8, 9),
        listOf(4, 5, 6),
        listOf(1, 2, 3)
    )

    val keyPad2 = listOf(
        "XX1XX".toList(),
        "X234X".toList(),
        "56789".toList(),
        "XABCX".toList(),
        "XXDXX".toList()
    ).reversed()

    private fun parse(input: List<String>): List<List<Direction>> {
        return input.map { line ->
            line.map {
                Direction.fromChar(it)
            }
        }
    }

    fun part1(input: List<String>): String {
        val parsed = parse(input)
        var currentPos = 1 to 1
        val code = parsed.map {
            it.forEach { dir ->
                currentPos = dir.move(currentPos)
                currentPos = coerce(currentPos)
            }
            keyPad[currentPos.inverse()]
        }
        return code.joinToString(separator = "") { it.toString() }
    }

    private fun coerce(pos: Pos, max: Int = 2): Pos {
        val first = pos.first.coerceIn(0, max)
        val second = pos.second.coerceIn(0, max)
        return first to second
    }

    fun part2(input: List<String>): String {
        val parsed = parse(input)
        var currentPos = 0 to 2
        val code = parsed.map {
            it.forEach { dir ->
                val newCandidate = coerce(dir.move(currentPos), 4)
                if (keyPad2[newCandidate.inverse()] != 'X') {
                    currentPos = newCandidate
                }
            }
            keyPad2[currentPos.inverse()]
        }
        return code.joinToString(separator = "") { it.toString() }
    }
}

fun main() {
    val testInput = """
        ULL
        RRDDD
        LURDL
        UUUUD
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day02.part1(testInput))
    println(Day02.part2(testInput))

    println("------Real------")
    val input = readInput("resources/2016/day02")
    println(Day02.part1(input))
    println(Day02.part2(input))
}