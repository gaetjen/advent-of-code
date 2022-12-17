import java.lang.Long.max

object Day17 {
    const val CHAMBER_WIDTH = 7

    enum class Shape(private val grid: List<String>) {
        FLOOR(listOf("████")),
        PLUS(listOf(" █ ", "███", " █ ")),
        CORNER(listOf("███", "  █", "  █")), // ! reverse order 0, 0 is bottom left
        COLUMN(listOf("█", "█", "█", "█")),
        SQUARE(listOf("██", "██"));

        val width = grid.first().length
        val height = grid.size.toLong()
        val blockPositions = grid.mapIndexed { colIdx, line ->
            line.mapIndexed { rowIdx, chr ->
                if (chr == ' ') null else rowIdx.toLong() to colIdx.toLong()
            }
        }.flatten().filterNotNull().toSet()
    }

    class FallingShape(var pos: PosL, val shape: Shape) {
        fun blockPositions() : Set<PosL> {
            return shape.blockPositions.map { it + pos }.toSet()
        }
        fun overlaps(blocks: Set<PosL>): Boolean {
            if (pos.second < 0) {
                return true
            }
            if (pos.first < 0) {
                return true
            }
            if (pos.first + shape.width > CHAMBER_WIDTH) {
                return true
            }
            return blockPositions().intersect(blocks).isNotEmpty()
        }
        fun move(dir: Direction) {
            pos = dir.moveL(pos)
        }
        fun moved(dir: Direction): FallingShape {
            return FallingShape(dir.moveL(pos), shape)
        }
    }


    class Chamber(val blocks: MutableSet<PosL>, val jets: List<Direction>) {
        fun addRock(numRocks: Long) {
            val rock = FallingShape(2L to maxHeight + 3, Shape.values()[(numRocks % 5).toInt()])
            while (true) {
                val jetDir = jets[numSteps]
                numSteps = (numSteps + 1) % jets.size
                if (!rock.moved(jetDir).overlaps(blocks)) {
                    rock.move(jetDir)
                }
                if (rock.moved(Direction.DOWN).overlaps(blocks)) {
                    blocks.addAll(rock.blockPositions())
                    maxHeight = max(maxHeight, rock.pos.second + rock.shape.height)
                    return
                } else {
                    rock.move(Direction.DOWN)
                }
            }
        }
        var numSteps = 0
        var maxHeight = 0L
    }

    private fun parse(input: String): List<Direction> {
        return input.map { if (it == '>') Direction.RIGHT else Direction.LEFT }
    }

    fun part1(input: String): Long {
        val jets = parse(input)
        val chamber = Chamber(mutableSetOf(), jets)
        var numRocks = 0L
        while (numRocks < 2022) {
            chamber.addRock(numRocks)
            numRocks++
        }
        return chamber.maxHeight
    }

    fun part2(input: String): Long {
        val jets = parse(input)
        val chamber = Chamber(mutableSetOf(), jets)
        var numRocks = 0L
        while (numRocks < 1_000_000_000_000) {
            chamber.addRock(numRocks)
            numRocks++
        }
        return chamber.maxHeight
    }
}

private operator fun PosL.plus(pos: PosL) = this.first + pos.first to this.second + pos.second


fun main() {
    val testInput = """
        >>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>
    """.trimIndent()
    println(Day17.Shape.values().joinToString("\n"))
    println(Day17.Shape.values().joinToString("\n") {it.blockPositions.toString()})
    println("------Tests------")
    println(Day17.part1(testInput))
    //println(Day17.part2(testInput))

    println("------Real------")
    val input = readInput("resources/day17").first()
    println(Day17.part1(input))
    //println(Day17.part2(input))
}
