object Day22 {
    sealed class Instruction {
        data class Walk(val distance: Int) : Instruction()
        data class Turn(val direction: Direction) : Instruction()
    }

    private fun parseInstructions(input: String): List<Instruction> {
        val distances = input.split("R", "L").map { Instruction.Walk(it.toInt()) }
        val turns = input.split(Regex("\\d+")).filter { it.isNotEmpty() }.map { Instruction.Turn(Direction.fromChar(it.first())) }
        return distances.zip(turns) { a, b -> listOf(a, b) }.flatten() + distances.last()
    }

    sealed class MapTile(val pos: Pos, val real: Boolean) {
        class Floor(pos: Pos) : MapTile(pos, true)
        class Wall(pos: Pos) : MapTile(pos, true)
        class Empty(pos: Pos) : MapTile(pos, false)
    }

    private fun parseMap(mapInput: List<String>): List<List<MapTile>> {
        val maxLength = mapInput.maxOf { it.length }
        return mapInput
            .map { it.padEnd(maxLength, ' ') }
            .mapIndexed { rowIdx, line ->
            line.mapIndexed { colIdx, c ->
                when (c) {
                    ' ' -> MapTile.Empty(rowIdx to colIdx)
                    '.' -> MapTile.Floor(rowIdx to colIdx)
                    '#' -> MapTile.Wall(rowIdx to colIdx)
                    else -> error("unknown map tile")
                }
            }
        }
    }

    fun part1(input: List<String>): Long {
        val (mapInput, walkInput) = input.split { it.isEmpty() }
        val tiles = parseMap(mapInput)
        val instructions = parseInstructions(walkInput.first())
        val map = Map(tiles)
        instructions.forEach {
            map.move(it)
        }
        return (map.pos.first + 1) * 1000L + (map.pos.second + 1) * 4 + dirNum(map.dir)
    }

    fun dirNum(dir: Direction): Int = when (dir) {
        Direction.RIGHT -> 0
        Direction.DOWN -> 1
        Direction.LEFT -> 2
        Direction.UP -> 3
    }

    class Map(val tiles: List<List<MapTile>>) {
        val leftEdge: List<MapTile> = tiles.map { it.first { tile -> tile.real } }
        val rightEdge: List<MapTile> = tiles.map { it.last { tile -> tile.real } }
        val topEdge: List<MapTile> = transpose(tiles).map { it.first { tile -> tile.real } }
        val bottomEdge: List<MapTile> = transpose(tiles).map { it.last { tile -> tile.real } }
        var pos: Pos = tiles.first().first { it.real }.pos
        var dir: Direction = Direction.RIGHT

        companion object {
            fun move(d: Direction, p: Pos) : Pos = when (d) {
                Direction.UP -> p.first - 1 to p.second
                Direction.RIGHT -> p.first to p.second + 1
                Direction.DOWN -> p.first + 1 to p.second
                Direction.LEFT -> p.first to p.second - 1
            }
        }

        fun moveOne() {
            val newPos = constrain(move(dir, pos))
            val newTile = realTile(newPos, dir)
            if (newTile is MapTile.Floor) {
                pos = newTile.pos
            }
        }

        fun walk(walk: Instruction.Walk) {
            repeat(walk.distance) { moveOne() }
        }

        fun turn(turn: Instruction.Turn) {
            val arr = Direction.values()
            dir = arr[(arr.indexOf(dir) + arr.indexOf(turn.direction)) % 4]
        }

        fun move(move: Instruction) {
            when (move) {
                is Instruction.Walk -> walk(move)
                is Instruction.Turn -> turn(move)
            }
        }

        private fun realTile(at: Pos, dir: Direction): MapTile {
            return if (tiles[at].real) {
                tiles[at]
            } else {
                when (dir) {
                    Direction.UP -> bottomEdge[at.second]
                    Direction.DOWN -> topEdge[at.second]
                    Direction.LEFT -> rightEdge[at.first]
                    Direction.RIGHT -> leftEdge[at.first]
                }
            }
        }

        fun constrain(pos: Pos): Pos {
            return pos.first.mod(tiles.size) to pos.second.mod(tiles.first().size)
        }

    }

    fun part2(input: List<String>): Long {

        return 0L
    }

}

fun main() {
    val testInput = """
        ...#
        .#..
        #...
        ....
...#.......#
........#...
..#....#....
..........#.
        ...#....
        .....#..
        .#......
        ......#.

10R5L5R10L4R5L5
    """.trimIndent().split("\n")
    println(testInput)
    println(listOf(1, 2, 3) + 4)
    println(listOf(1, 2, 3) + 4 + 5)

    println("------Tests------")
    println(Day22.part1(testInput))
    println(Day22.part2(testInput))

    println("------Real------")
    val input = readInput("resources/day22")
    println(Day22.part1(input))
    println(Day22.part2(input))
}