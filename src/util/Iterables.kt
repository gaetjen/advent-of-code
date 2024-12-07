package util

import y2024.Day07.combinations

/**
 * Splits a list into sublists where the predicate is true, similar to String.split.
 * @param matchInPost if true, the matching element is included in the sublist that follows it
 * @param matchInPre if true, the matching element is included in the sublist that precedes it
 */
fun <T> List<T>.split(matchInPost: Boolean = false, matchInPre: Boolean = false, predicate: (T) -> Boolean): List<List<T>> {
    val idx = this.indexOfFirst(predicate)
    return if (idx == -1) {
        listOf(this)
    } else {
        val preSplit = this.slice(0 until idx + if (matchInPre) 1 else 0)
        val tail = this.slice((idx + 1) until this.size).split(matchInPost, matchInPre, predicate).toMutableList()
        if (matchInPost) {
            tail[0] = listOf(this[idx]) + tail[0]
        }
        return listOf(preSplit) + tail
    }
}

fun <T> List<T>.split(predicate: (T) -> Boolean): List<List<T>> {
    val idx = this.indexOfFirst(predicate)
    return if (idx == -1) {
        listOf(this)
    } else {
        return listOf(this.take(idx)) + this.drop(idx + 1).split(predicate)
    }
}

/**
 * Combinatorics: generate all the ways of selecting [n] elements from [list] (ignoring order)
 */
fun <T> generateTakes(list: List<T>, n: Int) : Sequence<List<T>> = sequence  {
    if (n == 1) {
        list.forEach {
            yield(listOf(it))
        }
    } else {
        list.dropLast(n - 1).forEachIndexed { index, el ->
            val tails = generateTakes(list.subList(index + 1, list.size), n - 1)
            tails.forEach {
                yield(listOf(el) + it)
            }
        }
    }
}

/**
 * generate all the ways to select [n] elements from [list] (respecting order)
 */
fun <T> generateCombinations(list: List<T>, n: Int) : Sequence<List<T>> = sequence {
    if (n == 1) {
        list.forEach {
            yield(listOf(it))
        }
    } else {
        combinations(list, n - 1).forEach { tail ->
            list.forEach {
                yield(listOf(it) + tail)
            }
        }
    }
}

fun Iterable<Int>.product(): Int {
    var product = 1
    for (element in this) {
        product *= element
    }
    return product
}

fun Iterable<Long>.product(): Long {
    var product = 1L
    for (element in this) {
        product *= element
    }
    return product
}

fun <T> List<T>.indexOfAll(predicate: (T) -> Boolean): List<Int> {
    return this.mapIndexedNotNull { index, t -> if (predicate(t)) index else null }
}
