package y2023

import util.readInput
import util.timingStatistics

object Day07 {
    private val nonNumbers = mapOf(
        'T' to 10,
        'J' to 11,
        'Q' to 12,
        'K' to 13,
        'A' to 14
    )

    private val nonNumbers2 = mapOf(
        'J' to 1,
        'T' to 10,
        'Q' to 11,
        'K' to 12,
        'A' to 13
    )

    data class Hand(
        val cards: List<Int>,
        val bid: Int,
        val position: Int,
        val setSizes: List<Int>
    )

    private fun parse(input: List<String>, faces: Map<Char, Int> = nonNumbers): List<Hand> {
        return input.mapIndexed { idx, line ->
            val (cards, bid) = line.split(" ")
            val parsedCards = cards.map {
                faces[it] ?: it.digitToInt()
            }
            val sets = setSizes(parsedCards)
            Hand(parsedCards, bid.toInt(), idx, sets)
        }
    }

    private val strengthComparator: Comparator<Hand> = Comparator { a, b ->
        a.setSizes.zip(b.setSizes).take(2).forEach { (aSize, bSize) ->
            if (aSize != bSize) {
                return@Comparator aSize.compareTo(bSize)
            }
        }
        a.cards.zip(b.cards).forEach { (aCard, bCard) ->
            if (aCard != bCard) {
                return@Comparator aCard.compareTo(bCard)
            }
        }

        return@Comparator 0
    }

    private fun setSizes(cards: List<Int>): List<Int> {
        val sets = (2..14).map { c ->
            cards.count { it == c }
        }.sortedDescending().toMutableList()
        sets[0] += cards.count { it == 1 }
        return sets
    }

    fun part1(input: List<String>): Int {
        val hands = parse(input).sortedWith(strengthComparator)
        return hands.mapIndexed { idx, hand ->
            hand.bid * (idx + 1)
        }.sum()
    }

    fun part2(input: List<String>): Int {
        val hands = parse(input, nonNumbers2).sortedWith(strengthComparator)
        return hands.mapIndexed { idx, hand ->
            hand.bid * (idx + 1)
        }.sum()
    }
}

fun main() {
    val testInput = """
        32T3K 765
        T55J5 684
        KK677 28
        KTJJT 220
        QQQJA 483
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day07.part1(testInput))
    println(Day07.part2(testInput))

    println("------Real------")
    val input = readInput(2023, 7)
    println("Part 1 result: ${Day07.part1(input)}")
    println("Part 2 result: ${Day07.part2(input)}")
    timingStatistics { Day07.part1(input) }
    timingStatistics { Day07.part2(input) }
}