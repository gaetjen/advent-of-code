package util

import java.io.File
import java.math.BigInteger
import java.security.MessageDigest


/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt")
    .readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')


/**
 * Splits a list into sublists where the predicate is true, similar to String.split.
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

private operator fun <T> ((T) -> Boolean).not(): (T) -> Boolean = { input -> !this(input) }
