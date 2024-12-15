package y2024

import util.Cardinal
import util.Pos
import util.printGrid
import util.readInput
import util.split
import util.timingStatistics

data class Warehouse(
    val walls: Set<Pos>,
    val boxes: MutableSet<Pos>,
    var robot: Pos
) {
    fun moveRobot(direction: Cardinal) {
        val (pushedBoxes, canMove) = getBoxesInDirection(direction)
        if (canMove) {
            robot = direction.of(robot)
            pushedBoxes.reversed().forEach {
                boxes.remove(it)
                boxes.add(direction.of(it))
            }
        }
    }

    private fun getBoxesInDirection(direction: Cardinal): Pair<List<Pos>, Boolean> {
        var current = robot
        val pushedBoxes = mutableListOf<Pos>()
        do {
            current = direction.of(current)
            if (current in boxes) {
                pushedBoxes.add(current)
            }
            if (current in walls) {
                return pushedBoxes to false
            }
        } while (current in boxes)
        return pushedBoxes to true
    }
}

data class WideWarehouse(
    val walls: Set<Pos>,
    var robot: Pos,
    val boxes: MutableMap<Pos, WideBox>
) {
    fun moveRobot(direction: Cardinal) {
        val (pushedBoxes, canMove) = getBoxesInDirection(direction)
        if (canMove) {
            robot = direction.of(robot)
            val newBoxes = pushedBoxes.map { box ->
                box.positions().forEach { boxes.remove(it) }
                WideBox(direction.of(box.leftPos))
            }
            newBoxes.forEach { box ->
                box.positions().forEach { boxes[it] = box }
            }
        }
    }

    private fun getBoxesInDirection(direction: Cardinal): Pair<Set<WideBox>, Boolean> {
        var front = setOf(robot)
        val pushedBoxes = mutableSetOf<WideBox>()
        while (front.isNotEmpty()) {
            val frontAdjacent = front.map { direction.of(it) }
            if (frontAdjacent.any { it in walls }) {
                return emptySet<WideBox>() to false
            }
            val newBoxes = frontAdjacent.mapNotNull { boxes[it] }.toSet() - pushedBoxes
            pushedBoxes.addAll(newBoxes)
            front = newBoxes.flatMap { it.positions() }.toSet()
        }
        return pushedBoxes to true
    }

    data class WideBox(
        val leftPos: Pos
    ) {
        fun positions() = listOf(leftPos, Cardinal.EAST.of(leftPos))
    }

    fun print() {
        val printMap = buildMap {
            walls.forEach { this[it] = "#" }
            this[robot] = "@"
            boxes.keys.forEach { this[it] = "O" }
        }
        printGrid(printMap)
    }
}

object Day15 {
    private fun parse(input: List<String>): Pair<Warehouse, List<Cardinal>> {
        val (warehouse, directions) = input.split { it.isBlank() }
        val walls = mutableSetOf<Pos>()
        val boxes = mutableSetOf<Pos>()
        var robot = Pos(0, 0)
        warehouse.withIndex().forEach { (row, line) ->
            line.withIndex().forEach { (col, c) ->
                when (c) {
                    '#' -> walls.add(row to col)
                    'O' -> boxes.add(row to col)
                    '@' -> robot = row to col
                }
            }
        }

        return Warehouse(walls, boxes, robot) to robotDirections(directions)
    }

    private fun robotDirections(directions: List<String>) = directions.joinToString("").map { c ->
        when (c) {
            '^' -> Cardinal.NORTH
            'v' -> Cardinal.SOUTH
            '<' -> Cardinal.WEST
            '>' -> Cardinal.EAST
            else -> error("Invalid direction: $c")
        }
    }

    fun part1(input: List<String>): Int {
        val (warehouse, movements) = parse(input)
        movements.forEach { warehouse.moveRobot(it) }
        return warehouse.boxes.sumOf {
            it.first * 100 + it.second
        }
    }

    private fun parse2(input: List<String>): Pair<WideWarehouse, List<Cardinal>> {
        val (warehouse, directions) = input.split { it.isBlank() }
        val walls = mutableSetOf<Pos>()
        var robot = Pos(0, 0)
        val boxes = mutableMapOf<Pos, WideWarehouse.WideBox>()
        warehouse.withIndex().forEach { (row, line) ->
            line.withIndex().forEach { (col, c) ->
                when (c) {
                    '#' -> {
                        walls.add(row to col * 2)
                        walls.add(row to col * 2 + 1)
                    }
                    'O' -> {
                        val box = WideWarehouse.WideBox(row to col * 2)
                        box.positions().forEach { boxes[it] = box }
                    }
                    '@' -> robot = row to col * 2
                }
            }
        }
        return WideWarehouse(walls, robot, boxes) to robotDirections(directions)
    }

    fun part2(input: List<String>): Int {
        val (warehouse, movements) = parse2(input)
        //warehouse.print()
        movements.forEach { warehouse.moveRobot(it) }
        //warehouse.print()
        return warehouse.boxes.values.distinct().sumOf {
            it.leftPos.first * 100 + it.leftPos.second
        }
    }
}

fun main() {
    val testInput = """
        ##########
        #..O..O.O#
        #......O.#
        #.OO..O.O#
        #..O@..O.#
        #O#..O...#
        #O..O..O.#
        #.OO.O.OO#
        #....O...#
        ##########

        <vv>^<v^>v>^vv^v>v<>v^v<v<^vv<<<^><<><>>v<vvv<>^v^>^<<<><<v<<<v^vv^v>^
        vvv<<^>^v^^><<>>><>^<<><^vv^^<>vvv<>><^^v>^>vv<>v<<<<v<^v>^<^^>>>^<v<v
        ><>vv>v^v^<>><>>>><^^>vv>v<^^^>>v^v^<^^>v^^>v^<^v>v<>>v^v^<v>v^^<^^vv<
        <<v<^>>^^^^>>>v^<>vvv^><v<<<>^^^vv^<vvv>^>v<^^^^v<>^>vvvv><>>v^<<^^^^^
        ^><^><>>><>^^<<^^v>>><^<v>^<vv>>v>>>^v><>^v><<<<v>>v<v<v>vvv>^<><<>^><
        ^>><>^v<><^vvv<^^<><v<<<<<><^v<<<><<<^^<v<^^^><^>>^<v^><<<^>>^v<v^v<v^
        >^>>^v>vv>^<<^v<>><<><<v<<v><>v<^vv<<<>^^v^>^^>>><<^v>>v^v><^^>>^<>vv^
        <><^^>^^^<><vvvvv^v<v<<>^v<v>v<<^><<><<><<<^^<<<^<<>><<><^^^>^^<>^>v<>
        ^^>vv<^v^v<vv>^<><v<^v>^^^>>>^^vvv^>vvv<>>>^<^>>>>>^<<^v>^vvv<>^<><<v>
        v^^>>><<^^<>>^v^<v^vv<>v^<<>^<^v^v><^<<<><<^<v><v<>vv>>v><v^<vv<>v^<<^
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day15.part1(testInput))
    println(Day15.part2(testInput))

    println("------Real------")
    val input = readInput(2024, 15)
    println("Part 1 result: ${Day15.part1(input)}")
    println("Part 2 result: ${Day15.part2(input)}")
    timingStatistics { Day15.part1(input) }
    timingStatistics { Day15.part2(input) }
}