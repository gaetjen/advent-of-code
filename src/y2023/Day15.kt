package y2023

import util.readInput
import util.timingStatistics

object Day15 {
    private fun parse(input: List<String>): List<String> {
        return input.first().split(",")
    }

    fun part1(input: List<String>): Int {
        val parsed = parse(input)
        return parsed.sumOf { hash(it) }
    }

    fun hash(str: String): Int {
        return str.fold(0) { acc, c ->
            ((acc + c.code) * 17) % 256
        }
    }

    data class Operation(
        val label: String,
        val focalLength: Int?,
        val opChar: Char,
        val box: Int
    )

    private fun operations(input: List<String>): List<Operation> {
        return input.map { ins ->
            val (label, focalLengthStr) = ins.split('-', '=')
            val box = hash(label)
            if (ins.last() == '-') {
                Operation(
                    label = label,
                    focalLength = null,
                    opChar = '-',
                    box = box
                )
            } else {
                Operation(
                    label = label,
                    focalLength = focalLengthStr.toInt(),
                    opChar = '=',
                    box = box
                )
            }
        }
    }

    fun part2(input: List<String>): Long {
        val parsed = parse(input)
        val operations = operations(parsed)
        val boxes = List(256) { mutableMapOf<String, Int>() }
        operations.forEach { op ->
            if (op.opChar == '-') {
                boxes[op.box].remove(op.label)
            } else {
                boxes[op.box][op.label] = op.focalLength!!
            }
        }
        return focusingPower(boxes)
    }

    private fun focusingPower(boxes: List<Map<String, Int>>): Long {
        return boxes.withIndex().sumOf { (boxNumber, box) ->
            box.values.withIndex().sumOf { (idx, focalLength) ->
                (1L + boxNumber) * (idx + 1L) * focalLength
            }
        }
    }
}

fun main() {
    val testInput = """
        rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day15.part1(testInput))
    println(Day15.part2(testInput))

    println("------Real------")
    val input = readInput(2023, 15)
    println("Part 1 result: ${Day15.part1(input)}")
    println("Part 2 result: ${Day15.part2(input)}")
    timingStatistics { Day15.part1(input) }
    timingStatistics { Day15.part2(input) }
}