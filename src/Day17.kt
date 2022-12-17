import java.lang.Long.max

object Day17 {
    const val CHAMBER_WIDTH = 7

    enum class Shape(grid: List<String>) {
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

    fun part2(input: String, maxRocks: Long): Long {
        val jets = parse(input)
        val chamber = Chamber(mutableSetOf(), jets)
        var numRocks = 0L
        val steps = mutableListOf(0)
        val heights = mutableListOf(0L)
        var heightSkip = 0L
        var numRepeats = 0L
        while (numRocks < maxRocks) {
            chamber.addRock(numRocks)
            numRocks++
            steps.add(chamber.numSteps)
            heights.add(chamber.maxHeight)
            if (heightSkip == 0L) {
                val repeatingLength = getDuplicateLength(steps.reversed())
                if (repeatingLength != -1) {
                    println("found repeat of length after $numRocks: $repeatingLength")
                    heightSkip = checkHeights(heights.reversed(), repeatingLength)
                    numRepeats = (maxRocks - numRocks) / repeatingLength
                    numRocks += numRepeats * repeatingLength
                    println("same pattern will repeat $numRepeats times, for a total of $numRocks rocks and a height of ${chamber.maxHeight + numRepeats * heightSkip}")
                }
            }
        }
        return chamber.maxHeight + (numRepeats * heightSkip)
    }

    private fun checkHeights(heights: List<Long>, length: Int) : Long{
        val first = heights.subList(0, length).map { it - heights[0] }
        val second = heights.subList(length, length * 2).map { it - heights[length] }
        if (first == second) {
            println("height differences are matching! height difference: ${-first.last()}")
            return heights.first() - heights[length]
        } else {
            error("diffs don't match")
        }
    }

    private fun getDuplicateLength(steps: List<Int>): Int {
        val start = steps.first()
        val secondIdx = steps
            .mapIndexed { idx, i -> idx to i }
            .slice(steps.indices step 5)
            .firstOrNull { it.second == start && it.first != 0 }?.first
        return if (secondIdx != null
            && secondIdx * 2 < steps.size
            && steps.subList(0, secondIdx) == steps.subList(secondIdx, secondIdx * 2)) {
            secondIdx
        } else {
            -1
        }
    }
}

private operator fun PosL.plus(pos: PosL) = this.first + pos.first to this.second + pos.second


fun main() {

    val testInput = """
        >>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>
    """.trimIndent()
    println(Day17.Shape.values().joinToString("\n"))
    println(Day17.Shape.values().joinToString("\n") {it.blockPositions.toString()})
    println(testInput.length)
    println("------Tests------")
    println(Day17.part2(testInput, 2022))
    println(Day17.part2(testInput, 1_000_000_000_000))
    println("------Real------")
    val input = readInput("resources/day17").first()
    println(Day17.part2(input, 2022))
    // wrong: 1536416184990 (too low)
    println(Day17.part2(input, 1_000_000_000_000))
}
