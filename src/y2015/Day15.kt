package y2015

import util.readInput
import kotlin.math.max

data class Ingredient(
    val capacity: Int,
    val durability: Int,
    val flavor: Int,
    val texture: Int,
    val calories: Int
)

/**
 * generate all possible ways [total] can be a sum of [n] non-negative integers, taking order into account
 */
fun generateSplits(total: Int, n: Int) : Sequence<List<Int>> = sequence {
    if (n <= 1) {
        yield(listOf(total))
    } else if (n == 2) {
        (0..total).forEach {
            yield(listOf(it, total - it))
        }
    } else {
        (0..total).forEach { first ->
            generateSplits(total - first, n - 1).forEach {
                yield(listOf(first) + it)
            }
        }
    }
}

object Day15 {
    private fun parse(input: List<String>): List<Ingredient> {
        return input.map { str ->
            val els = str.replace(",", "").split(' ')
            Ingredient(
                els[2].toInt(),
                els[4].toInt(),
                els[6].toInt(),
                els[8].toInt(),
                els.last().toInt()
            )
        }
    }

    fun part1(input: List<String>): Int {
        val ingredients = parse(input)
        val numIngredients = ingredients.size
        val total = 100
        return generateSplits(total, numIngredients).maxOf {  amounts ->
            val cookie = cookie(amounts, ingredients)
            goodness(cookie)
        }
    }

    private fun cookie(amounts: List<Int>, ingredients: List<Ingredient>): Ingredient {
        return amounts.zip(ingredients).fold(Ingredient(0, 0, 0, 0, 0)) { acc, (amount, ingredient)->
            Ingredient(
                acc.capacity + amount * ingredient.capacity,
                acc.durability + amount * ingredient.durability,
                acc.flavor + amount * ingredient.flavor,
                acc.texture + amount * ingredient.texture,
                acc.calories + amount * ingredient.calories,
            )
        }
    }

    private fun goodness(cookie: Ingredient): Int {
        return max(0, cookie.capacity) * max(0, cookie.durability) * max(0, cookie.flavor) * max(0, cookie.texture)
    }

    fun part2(input: List<String>): Int {
        val ingredients = parse(input)
        val numIngredients = ingredients.size
        val total = 100
        return generateSplits(total, numIngredients).map {  amounts ->
            cookie(amounts, ingredients)
        }.filter {
            it.calories == 500
        }.maxOf {
            goodness(it)
        }
    }
}

fun main() {
    val testInput = """
        Butterscotch: capacity -1, durability -2, flavor 6, texture 3, calories 8
        Cinnamon: capacity 2, durability 3, flavor -2, texture -1, calories 3
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day15.part1(testInput))
    println(Day15.part2(testInput))

    println("------Real------")
    val input = readInput("resources/2015/day15")
    println(Day15.part1(input))
    println(Day15.part2(input))
}
