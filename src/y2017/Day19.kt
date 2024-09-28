package y2017

import util.Cardinal
import util.Pos
import util.readInput
import util.timingStatistics
import y2022.Day15.manhattanDist

sealed class Tube {
    data object Vertical : Tube()
    data object Horizontal : Tube()
    data object Corner : Tube()
    class CornerWithDirections(
        val dirs: List<Cardinal>
    ) : Tube() {
        fun catchesTravellerInDirection(dir: Cardinal) : Boolean {
            return dir !in dirs
        }
        fun continueFromTo(from: Cardinal): Cardinal {
            return dirs.first { it.relativePos != (from.relativePos.first * -1 to from.relativePos.second * -1) }
        }
    }

    class Letter(val c: Char) : Tube()
}

object Day19 {
    private fun parse(input: List<String>): Pair<Pos, Map<Pos, Tube>> {
        var start = 0 to 0
        val map = input.flatMapIndexed { row, line ->
            line.mapIndexedNotNull { column, c ->
                if (row == 0 && c == '|') {
                    start = 0 to column
                }
                if (c != ' ') {
                    (row to column) to when (c) {
                        '|' -> Tube.Vertical
                        '-' -> Tube.Horizontal
                        '+' -> Tube.Corner
                        else -> Tube.Letter(c)
                    }
                } else {
                    null
                }
            }
        }.toMap()
        return start to map
    }

    fun part1(input: List<String>): Pair<String, Int> {
        val (start, tubes) = parse(input)
        val interestingStuff = tubes.filter { (_, t) ->
            t is Tube.Corner || t is Tube.Letter
        }.mapValues { (pos, tile) ->
            if (tile is Tube.Letter) {
                tile
            } else {
                Tube.CornerWithDirections(
                    Cardinal.entries.filter { dir ->
                        tubes[dir.of(pos)]?.let {
                            it::class in listOf(
                                Tube.Vertical::class,
                                Tube.Horizontal::class,
                                Tube.Letter::class
                            )
                        } == true
                    }
                )
            }
        }
        val visitedLetters = mutableListOf<Char>()
        var current = start
        var dir = Cardinal.SOUTH
        var distance = 1
        while (true) {
            val (p, d, newLetters) = nextDirection(current, dir, interestingStuff)
            distance += p.manhattanDist(current)
            current = p
            visitedLetters.addAll(newLetters)
            if (d == null) {
                break
            } else {
                dir = d
            }
        }
        return visitedLetters.joinToString("") to distance
    }

    fun nextDirection(start: Pos, direction: Cardinal, map: Map<Pos, Tube>): Triple<Pos, Cardinal?, List<Char>> {
        val segment = map.filter { (pos, t) ->
            when (direction) {
                Cardinal.WEST -> pos.first == start.first && pos.second < start.second
                Cardinal.SOUTH -> pos.second == start.second && pos.first > start.first
                Cardinal.EAST -> pos.first == start.first && pos.second > start.second
                Cardinal.NORTH -> pos.second == start.second && pos.first < start.first
            } && (t is Tube.Letter || t is Tube.CornerWithDirections)
        }.entries.sortedBy { (pos, _) ->
            when(direction) {
                Cardinal.WEST -> -pos.second
                Cardinal.SOUTH -> pos.first
                Cardinal.EAST -> pos.second
                Cardinal.NORTH -> -pos.first
            }
        }.takeWhile { (_, t) ->
            t is Tube.Letter || (t is Tube.CornerWithDirections && t.catchesTravellerInDirection(direction))
        }
        val firstCorner = segment.firstOrNull { it.value is Tube.CornerWithDirections }
        val end = firstCorner?.key ?: segment.last().key
        val letters = segment.takeWhile { it.value is Tube.Letter }.map { (it.value as Tube.Letter).c }
        val nextDirection = (firstCorner?.value as Tube.CornerWithDirections?)?.continueFromTo(direction)
        return Triple(end, nextDirection, letters)
    }
}

fun main() {
    val testInput = """
     |          
     |  +--+    
     A  |  C    
 F---|----E|--+ 
     |  |  |  D 
     +B-+  +--+ 
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day19.part1(testInput))

    println("------Real------")
    val input = readInput(2017, 19)
    println("Part 1 result: ${Day19.part1(input)}")
    timingStatistics { Day19.part1(input) }
}