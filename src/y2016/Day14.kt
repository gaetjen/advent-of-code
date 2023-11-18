package y2016

import util.md5

object Day14 {
    fun part1(salt: String): Int {
        val candidates = mutableSetOf<Pair<Int, Char>>()
        var currentIdx = 0
        val keyIdxs = mutableListOf<Int>()
        while (keyIdxs.size < 100) {
            candidates.removeIf {
                it.first < currentIdx - 1000
            }
            val hash = (salt + currentIdx.toString()).md5()
            val triples = hasConsecutive(hash, 3)
            if (triples.isNotEmpty()) {
                val quints = hasConsecutive(hash, 5)
                quints.forEach { fiveChar ->
                    val found = candidates.filter { (_, c) -> fiveChar == c }
                    found.forEach {
                        keyIdxs.add(it.first)
                        candidates.remove(it)
                    }
                }
                candidates.add(currentIdx to triples.first())
            }
            currentIdx++
        }
        keyIdxs.sort()
        println("all: $keyIdxs")
        return keyIdxs[63]
    }

    private fun hasConsecutive(str: String, n: Int): List<Char> {
        return str.windowed(n, 1).filter {
            it.toSet().size == 1
        }.map { it.first() }
    }

    fun part2(salt: String): Int {
        val candidates = mutableSetOf<Pair<Int, Char>>()
        var currentIdx = 0
        val keyIdxs = mutableListOf<Int>()
        while (keyIdxs.size < 100) {
            candidates.removeIf {
                it.first < currentIdx - 1000
            }
            val hash = (salt + currentIdx.toString()).md5Stretched()
            val triples = hasConsecutive(hash, 3)
            if (triples.isNotEmpty()) {
                val quints = hasConsecutive(hash, 5)
                quints.forEach { fiveChar ->
                    val found = candidates.filter { (_, c) -> fiveChar == c }
                    found.forEach {
                        keyIdxs.add(it.first)
                        candidates.remove(it)
                    }
                }
                candidates.add(currentIdx to triples.first())
            }
            currentIdx++
        }
        keyIdxs.sort()
        println("all: $keyIdxs")
        return keyIdxs[63]
    }
}

private fun String.md5Stretched(): String {
    var result = this
    repeat(2017) {
        result = result.md5()
    }
    return result
}

fun main() {
    val testInput = "abc"
    println("------Tests------")
    println(Day14.part1(testInput))
    println(Day14.part2(testInput))

    println("------Real------")
    val input = "zpqevtbw"
    println(Day14.part1(input))
    println(Day14.part2(input))
}