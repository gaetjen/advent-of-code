package y2024

import util.readInput
import util.timingStatistics

sealed class DiskBlock {
    data class File(val id: Int) : DiskBlock()
    data object Empty : DiskBlock()
}

sealed class DiskChunk(
    open val length: Int,
    open val startIdx: Int
) {
    abstract fun block(): DiskBlock

    data class File(
        val id: Int,
        override val length: Int,
        override val startIdx: Int
    ) : DiskChunk(length, startIdx) {
        override fun block(): DiskBlock.File {
            return DiskBlock.File(id)
        }
    }

    data class Empty(
        override val length: Int,
        override val startIdx: Int
    ) : DiskChunk(length, startIdx) {
        override fun block(): DiskBlock.Empty {
            return DiskBlock.Empty
        }
    }
}

object Day09 {

    private fun parse(input: List<String>): List<DiskBlock> {
        val parsed = input.first().map { it.digitToInt() } + if (input.first().length % 2 == 1) listOf(0) else listOf()
        val disk = parsed.chunked(2).withIndex().flatMap { (idx, pair) ->
            val (file, free) = pair
            List(file) { DiskBlock.File(idx) } + List(free) { DiskBlock.Empty }
        }
        return disk
    }

    fun part1(input: List<String>): Long {
        val disk = parse(input)
        val backwardsFiles = disk.reversed().filterIsInstance<DiskBlock.File>().asSequence().iterator()
        val compressedLength = disk.count { it is DiskBlock.File }
        val compressedDisk = disk.take(compressedLength).map { block ->
            when (block) {
                is DiskBlock.File -> block
                is DiskBlock.Empty -> backwardsFiles.next()
            }
        }
        return checkSum(compressedDisk)
    }

    private fun checkSum(fileSystem: List<DiskBlock>): Long {
        return fileSystem.withIndex().sumOf { (idx, block) ->
            when (block) {
                is DiskBlock.Empty -> 0
                is DiskBlock.File -> idx.toLong() * block.id
            }
        }
    }


    fun part2(input: List<String>): Long {
        val disk = parse(input)
        val deepSplit: DeepRecursiveFunction<List<IndexedValue<DiskBlock>>, List<List<IndexedValue<DiskBlock>>>> =
            DeepRecursiveFunction { list ->
                val idx = list.indexOfFirst { (idx, block) ->
                    if (idx + 1 < disk.size) {
                        disk[idx + 1] != block
                    } else false
                }
                if (idx == -1) {
                    listOf(list)
                } else {
                    val preSplit = list.slice(0 until idx + 1)
                    listOf(preSplit) + this.callRecursive(list.slice((idx + 1) until list.size))
                }
            }
        val (files, empties) = deepSplit(disk.withIndex().toList())
            .map { chunk ->
                when (val first = chunk.first().value) {
                    is DiskBlock.File -> DiskChunk.File(
                        id = first.id,
                        length = chunk.size,
                        startIdx = chunk.first().index
                    )

                    is DiskBlock.Empty -> DiskChunk.Empty(
                        length = chunk.size,
                        startIdx = chunk.first().index
                    )
                }
            }.partition { it is DiskChunk.File }
        val newEmpties = empties.toMutableList()
        val newFiles = files.reversed().map { file ->
            val freeSpace = newEmpties.indexOfFirst { it.startIdx < file.startIdx && it.length >= file.length }
            if (freeSpace == -1) {
                file
            } else {
                val newFile = DiskChunk.File(
                    id = (file as DiskChunk.File).id,
                    startIdx = newEmpties[freeSpace].startIdx,
                    length = file.length
                )
                if (newEmpties[freeSpace].length == file.length) {
                    newEmpties.removeAt(freeSpace)
                } else {
                    newEmpties[freeSpace] = DiskChunk.Empty(
                        startIdx = newEmpties[freeSpace].startIdx + newFile.length,
                        length = newEmpties[freeSpace].length - newFile.length
                    )
                }
                newEmpties.add(DiskChunk.Empty(file.length, file.startIdx))
                newEmpties.sortBy { it.startIdx }
                newFile
            }
        }
        val newDisk = (newFiles + newEmpties).sortedBy { it.startIdx }.flatMap { chunk ->
            List(chunk.length) { chunk.block() }
        }
        return checkSum(newDisk)
    }
}

fun main() {
    val testInput = """
        2333133121414131402
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day09.part1(testInput))
    println(Day09.part2(testInput))

    println("------Real------")
    val input = readInput(2024, 9)
    println("Part 1 result: ${Day09.part1(input)}")
    println("Part 2 result: ${Day09.part2(input)}")
    timingStatistics { Day09.part1(input) }
    timingStatistics { Day09.part2(input) }
}