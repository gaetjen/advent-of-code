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


fun <T> transpose(matrix: List<List<T>>): List<List<T>> {
    return List(matrix.first().size) { rowIdx ->
        List(matrix.size) { colIdx ->
            matrix[colIdx][rowIdx]
        }
    }
}

fun <T> getRow(grid: List<List<T>>, rowIdx: Int) = grid[rowIdx]
fun <T> getCol(grid: List<List<T>>, colIdx: Int) = grid.map { it[colIdx] }

fun <T> List<T>.split(matchInPost: Boolean = true, matchInPre: Boolean = false, predicate: (T) -> Boolean): List<List<T>> {
    val idx = this.indexOfFirst(predicate)
    return if (idx == -1) {
        listOf(this)
    } else {
        val preSplit = this.slice(0 until idx + if (matchInPre) 1 else 0)
        val postSplit = this.slice((idx + if (matchInPost) 1 else 0) until this.size)
        return listOf(preSplit) + postSplit.split(matchInPost, matchInPre, predicate)
    }
}

private operator fun <T> ((T) -> Boolean).not(): (T) -> Boolean = { input -> !this(input) }
