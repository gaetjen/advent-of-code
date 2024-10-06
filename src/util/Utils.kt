package util

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileNotFoundException
import java.math.BigInteger
import java.security.MessageDigest

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt")
    .readLines()


fun readInput(year: Int, day: Int): List<String> {
    val dayString = String.format("%02d", day)
    val file = File("src", "resources/$year/day$dayString.txt")
    return try {
        file.readLines()
    } catch (e: FileNotFoundException) {
        println("No input file found for day $year/$day, fetching from adventofcode.com")
        val token = System.getenv("AOC_SESSION")
        if (token == null || token == "") {
            throw IllegalStateException("No AOC_SESSION environment variable found, please set it to your session token")
        }
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://adventofcode.com/${year}/day/${day}/input")
            .get()
            .addHeader("Cookie", "session=${token}")
            .build()
        try {
            val response = client.newCall(request).execute()
            val body = response.body!!.string()
            val dir = File("src", "resources/$year")
            if (!dir.exists()) dir.mkdirs()
            file.writeText(body)
            body.lines()
            readInput(year, day)
        } catch (e: Exception) {
            println("Error while fetching input file for day $day: ${e.javaClass.simpleName} ${e.message}")
            throw e
        }
    }
}

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

private operator fun <T> ((T) -> Boolean).not(): (T) -> Boolean = { input -> !this(input) }
