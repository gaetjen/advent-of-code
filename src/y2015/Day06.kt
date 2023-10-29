package y2015

import util.getRange
import util.printMatrix
import util.readInput
import kotlin.math.max


data class Light(var on: Boolean = false, var brightness: Int = 0) {
    fun toggle() {
        on = !on
    }

    fun turnOn() {
        on = true
    }

    fun turnOff() {
        on = false
    }

    fun increase() {
        brightness += 1
    }

    fun decrease() {
        brightness = max(brightness - 1, 0)
    }

    fun jump() {
        increase()
        increase()
    }
}

data class Switch(
    val instruction: String,
    val startRow: Int,
    val stopRow: Int,
    val startCol: Int,
    val stopCol: Int
)

object Day06 {
    private fun parse(input: List<String>): List<Switch> {
        return input.map { line ->
            val (ins, start, _, stop) =  line.split(' ').takeLast(4)
            val (rowStart, colStart) = start.split(',').map { it.toInt() }
            val (rowStop, colStop) = stop.split(',').map { it.toInt() }
            Switch(
                ins,
                rowStart,
                rowStop,
                colStart,
                colStop
            )
        }
    }

    fun part1(input: List<String>): Int {
        val parsed = parse(input)
        val lightGrid = List(1000) { List(1000) {Light()} }
        parsed.forEach {
            getRange(lightGrid, it.startRow, it.startCol, it.stopRow, it.stopCol).map { light ->
                when (it.instruction) {
                    "on" -> light.turnOn()
                    "off" -> light.turnOff()
                    else -> light.toggle()
                }
            }
        }
        printMatrix(lightGrid) {
            if (it.on) "*" else " "
        }
        return lightGrid.flatten().filter { it.on }.size
    }

    fun part2(input: List<String>): Int {
        val parsed = parse(input)
        val lightGrid = List(1000) { List(1000) {Light()} }
        parsed.forEach {
            getRange(lightGrid, it.startRow, it.startCol, it.stopRow, it.stopCol).map { light ->
                when (it.instruction) {
                    "on" -> light.increase()
                    "off" -> light.decrease()
                    else -> light.jump()
                }
            }
        }
        printMatrix(lightGrid) {
            it.brightness.toString()
        }
        return lightGrid.flatten().sumOf { it.brightness }
    }
}

fun main() {
    val testInput = """
        turn on 0,0 through 999,999
        toggle 0,0 through 999,0
        turn off 499,499 through 500,500
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day06.part1(testInput))
    println(Day06.part2(testInput))

    println("------Real------")
    val input = readInput("resources/2015/day06")
    println(Day06.part1(input))
    println(Day06.part2(input))
}
