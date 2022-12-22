import Direction.DOWN
import Direction.LEFT
import Direction.RIGHT
import Direction.UP

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
        val map = FlatMap(tiles)
        instructions.forEach {
            map.move(it)
        }
        return (map.pos.first + 1) * 1000L + (map.pos.second + 1) * 4 + dirNum(map.dir)
    }

    fun dirNum(dir: Direction): Int = when (dir) {
        RIGHT -> 0
        DOWN -> 1
        LEFT -> 2
        UP -> 3
    }

    abstract class Map(val tiles: List<List<MapTile>>) {
        var pos: Pos = tiles.first().first { it.real }.pos
        var dir: Direction = RIGHT

        companion object {
            fun move(d: Direction, p: Pos): Pos = when (d) {
                UP -> p.first - 1 to p.second
                RIGHT -> p.first to p.second + 1
                DOWN -> p.first + 1 to p.second
                LEFT -> p.first to p.second - 1
            }
        }

        fun moveOne() {
            val newPos = constrain(move(dir, pos))
            val (newTile, newDir) = realTile(newPos, dir)
            if (newTile is MapTile.Floor) {
                pos = newTile.pos
                dir = newDir
            }
            if (newTile is MapTile.Empty) {
                error("landed on empty tile!")
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

        abstract fun realTile(at: Pos, dir: Direction): Pair<MapTile, Direction>

        fun constrain(pos: Pos): Pos {
            return pos.first.mod(tiles.size) to pos.second.mod(tiles.first().size)
        }

    }

    class FlatMap(tiles: List<List<MapTile>>) : Map(tiles) {
        val leftEdge: List<MapTile> = tiles.map { it.first { tile -> tile.real } }
        val rightEdge: List<MapTile> = tiles.map { it.last { tile -> tile.real } }
        val topEdge: List<MapTile> = transpose(tiles).map { it.first { tile -> tile.real } }
        val bottomEdge: List<MapTile> = transpose(tiles).map { it.last { tile -> tile.real } }

        override fun realTile(at: Pos, dir: Direction): Pair<MapTile, Direction> {
            return if (tiles[at].real) {
                tiles[at] to dir
            } else {
                when (dir) {
                    UP -> bottomEdge[at.second]
                    DOWN -> topEdge[at.second]
                    LEFT -> rightEdge[at.first]
                    RIGHT -> leftEdge[at.first]
                } to dir
            }
        }
    }

    class CubeMap(tiles: List<List<MapTile>>) : Map(tiles) {
        override fun realTile(at: Pos, dir: Direction): Pair<MapTile, Direction> {
            if (tiles[at].real) {
                return tiles[at] to dir
            }
            return when {
                at.first == 50 && at.second >= 100 && dir == DOWN -> tiles[at.second to 99] to LEFT
                at.first in 50 until 100 && at.second == 100 -> tiles[49 to at.first] to UP
                at.first == 99 && at.second in 0 until 50 && dir == UP -> tiles[50 + at.second to 50] to RIGHT
                at.first in 50 until 100 && at.second == 49 && dir == LEFT -> tiles[100 to at.first - 50] to DOWN
                at.first == 150 && at.second in 50 until 100 && dir == DOWN -> tiles[at.second + 100 to 49] to LEFT
                at.first in 150 until 200 && at.second == 50 && dir == RIGHT -> tiles[149 to at.first - 100] to UP
                at.first == 199 && at.second in 50 until 100 && dir == UP -> tiles[at.second + 100 to 0] to RIGHT
                at.first in 150 until 200 && at.second == 149 && dir == LEFT -> tiles[0 to at.first - 100] to DOWN
                at.first == 199 && at.second in 100 until 150 && dir == UP -> tiles[199 to at.second - 100] to UP
                at.first == 0 && at.second in 0 until 50 && dir == DOWN -> tiles[0 to at.second + 100] to DOWN
                at.first in 0 until 50 && at.second == 0 && dir == RIGHT -> tiles[149 - at.first to 99] to LEFT
                at.first in 100 until 150 && at.second == 100 && dir == RIGHT -> tiles[149 - at.first to 149] to LEFT
                at.first in 0 until 50 && at.second == 49 && dir == LEFT -> tiles[149 - at.first to 0] to RIGHT
                at.first in 100 until 150 && at.second == 149 && dir == LEFT -> tiles[149 - at.first to 50] to RIGHT

                else -> error("unexpected coordinates and dir: $pos, $dir")
            }
        }
    }

    fun part2(input: List<String>): Long {
        val (mapInput, walkInput) = input.split { it.isEmpty() }
        val tiles = parseMap(mapInput)
        val instructions = parseInstructions(walkInput.first())
        val map = CubeMap(tiles)
        instructions.forEach {
            //println("${map.pos}, ${map.dir}, $it")
            map.move(it)
        }
        return (map.pos.first + 1) * 1000L + (map.pos.second + 1) * 4 + dirNum(map.dir)
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
    //println(Day22.part2(testInput))

    println("------Real------")
    val input = readInput("resources/day22")
    println(Day22.part1(input))
    // 13383
    println(Day22.part2(input))
}