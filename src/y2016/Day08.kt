package y2016

import util.getCol
import util.printMatrix
import util.readInput
import util.set

sealed class Instruction {
    companion object {
        fun parse(string: String): Instruction {
            return when {
                string.startsWith("rect") -> Rect.fromString(string)
                string.startsWith("rotate column") -> RotateColumn.fromString(string)
                string.startsWith("rotate row") -> RotateRow.fromString(string)
                else -> error("invalid instruction")
            }
        }
    }

    abstract fun execute(display: MutableList<MutableList<Boolean>>)
}

data class Rect(val columns: Int, val rows: Int) : Instruction() {
    companion object {
        fun fromString(str: String): Rect {
            val (columns, rows) = str.split(' ').last().split('x').map { it.toInt() }
            return Rect(columns, rows)
        }
    }

    override fun execute(display: MutableList<MutableList<Boolean>>) {
        (0 until rows).forEach { row ->
            (0 until columns).forEach { col ->
                display[row to col] = true
            }
        }
    }
}

data class RotateRow(val row: Int, val distance: Int) : Instruction() {
    companion object {
        fun fromString(str: String): RotateRow {
            val splits = str.split(' ')
            return RotateRow(
                splits[2].substringAfter('=').toInt(),
                splits.last().toInt()
            )
        }
    }

    override fun execute(display: MutableList<MutableList<Boolean>>) {
        display[row] = (display[row].takeLast(distance) + display[row].dropLast(distance)).toMutableList()
    }
}

data class RotateColumn(val column: Int, val distance: Int) : Instruction() {
    companion object {
        fun fromString(str: String): RotateColumn {
            val splits = str.split(' ')
            return RotateColumn(
                splits[2].substringAfter('=').toInt(),
                splits.last().toInt()
            )
        }
    }

    override fun execute(display: MutableList<MutableList<Boolean>>) {
        val col = getCol(display, column)
        val rotatedColumn = col.takeLast(distance) + col.dropLast(distance)
        (0 until display.size).forEach {
            display[it to column] = rotatedColumn[it]
        }
    }
}

object Day08 {
    private fun parse(input: List<String>): List<Instruction> {
        return input.map {
            Instruction.parse(it)
        }
    }

    fun part1(input: List<String>): Int {
        val parsed = parse(input)
        val display = MutableList(6) {
            MutableList(50) { false }
        }
        parsed.forEach {
            it.execute(display)
        }
        printMatrix(display) {
            if (it) "██" else "  "
        }
        val totalRect = parsed.sumOf {
            if (it is Rect) {
                it.columns * it.rows
            } else {
                0
            }
        }
        println("total rect: $totalRect")
        return display.flatten().count { it }
    }
}

fun main() {
    val testInput = """
        rect 3x2
        rotate column x=1 by 1
        rotate row y=0 by 4
        rotate column x=1 by 1
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day08.part1(testInput))

    println("------Real------")
    val input = readInput("resources/2016/day08")
    println(Day08.part1(input))
}
