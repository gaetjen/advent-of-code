package y2016

import util.Pos
import util.printGrid
import util.readInput

data class DiskNode(
    val pos: Pos,
    val size: Int,
    val used: Int,
    val avail: Int,
    val percent: Int
) {
    companion object {
        fun fromString(string: String): DiskNode {
            val els = string.split(Regex("\\s+"))
            val (posX, posY) = els[0].replace("x", "").replace("y", "").split('-').takeLast(2)
            val nums = els.drop(1).map { it.dropLast(1).toInt() }
            return DiskNode(
                posX.toInt() to posY.toInt(),
                nums[0],
                nums[1],
                nums[2],
                nums[3]
            )
        }
    }
}

object Day22 {
    private fun parse(input: List<String>): List<DiskNode> {
        return input.drop(2).map {
            DiskNode.fromString(it)
        }
    }

    fun part1(input: List<String>): Int {
        val parsed = parse(input)
        val toRemove = parsed.count {
            it.avail >= it.used && it.used > 0
        }
        val useds = parsed.map { it.used }.sorted().dropWhile { it == 0 }
        var avails = parsed.map { it.avail }.sorted()
        println(useds)
        println(avails)
        println(parsed.map { it.size }.sorted())
        var viables = 0
        useds.forEach {  used ->
            avails = avails.dropWhile { it < used }
            viables += avails.size
        }
        return viables - toRemove
    }

    fun part2(input: List<String>): Int {
        val parsed = parse(input)
        printGrid(
            parsed.associate {
                it.pos to if(it.size <= 500) "_" else "#"
            }
        )
        val blockers = parsed.filter { it.size > 500 }
        println(blockers)
        println(parsed.sortedBy { it.percent }.take(4))
        val maxX = parsed.maxOf { it.pos.first }
        val empty = parsed.first { it.used == 0 }
        val distanceToTarget = empty.pos.second + empty.pos.first + maxX
        return distanceToTarget + 5 * (maxX - 1)
    }
}

fun main() {
    val testInput = """
        root@ebhq-gridcenter# df -h
        Filesystem            Size  Used  Avail  Use%
        /dev/grid/node-x0-y0   10T    8T     2T   80%
        /dev/grid/node-x0-y1   11T    6T     5T   54%
        /dev/grid/node-x0-y2   32T   28T     4T   87%
        /dev/grid/node-x1-y0    9T    7T     2T   77%
        /dev/grid/node-x1-y1    8T    0T     8T    0%
        /dev/grid/node-x1-y2   11T    7T     4T   63%
        /dev/grid/node-x2-y0   10T    6T     4T   60%
        /dev/grid/node-x2-y1    9T    8T     1T   88%
        /dev/grid/node-x2-y2    9T    6T     3T   66%
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day22.part1(testInput))
    println(Day22.part2(testInput))

    println("------Real------")
    val input = readInput("resources/2016/day22")
    println(Day22.part1(input))
    println(Day22.part2(input))
}