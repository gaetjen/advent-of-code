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
        val position: Int
    )

    private fun parse(input: List<String>, faces: Map<Char, Int> = nonNumbers): List<Hand> {
        return input.mapIndexed { idx, line ->
            val (cards, bid) = line.split(" ")
            val parsedCards = cards.map {
                faces[it] ?: it.digitToInt()
            }
            Hand(parsedCards, bid.toInt(), idx)
        }
    }

    private val strengthComparator: Comparator<Hand> = Comparator { a, b ->
        val aSets = counts(a.cards)
        val bSets = counts(b.cards)

        val distinctA = a.cards.distinct().size
        val distinctB = b.cards.distinct().size
        val distinctDiff = distinctB - distinctA
        if (distinctDiff != 0) return@Comparator distinctDiff

        val maxSetSizeA = aSets.values.max()
        val maxSetSizeB = bSets.values.max()
        val setSizeDiff = maxSetSizeA - maxSetSizeB
        if (setSizeDiff != 0) return@Comparator setSizeDiff

        a.cards.zip(b.cards).forEach { (aCard, bCard) ->
            if (aCard != bCard) {
                return@Comparator aCard.compareTo(bCard)
            }
        }

        return@Comparator 0
    }

    private fun counts(cards: List<Int>): Map<Int, Int> {
        val counts = mutableMapOf<Int, Int>()
        for (card in cards) {
            counts[card] = counts.getOrDefault(card, 0) + 1
        }
        return counts
    }

    fun part1(input: List<String>): Int {
        val hands = parse(input).sortedWith(strengthComparator)
        return hands.mapIndexed { idx, hand ->
            hand.bid * (idx + 1)
        }.sum()
    }

    private val strengthComparator2: Comparator<Hand> = Comparator { a, b ->
        val aSets = counts(a.cards)
        val bSets = counts(b.cards)

        val noJokersA = aSets.filterKeys { it != 1 }
        val noJokersB = bSets.filterKeys { it != 1 }

        val maxSetSizeA = (noJokersA.values.maxOrNull() ?: 0) + (aSets[1] ?: 0)
        val maxSetSizeB = (noJokersB.values.maxOrNull() ?: 0) + (bSets[1] ?: 0)
        val setSizeDiff = maxSetSizeA - maxSetSizeB
        if (setSizeDiff != 0) return@Comparator setSizeDiff

        if (maxSetSizeA in listOf(2, 3)) {
            val secondSetA = noJokersA.values.sortedDescending()[1]
            val secondSetB = noJokersB.values.sortedDescending()[1]
            val secondSetDiff = secondSetA - secondSetB
            if (secondSetDiff != 0) return@Comparator secondSetDiff
        }
        a.cards.zip(b.cards).forEach { (aCard, bCard) ->
            if (aCard != bCard) {
                return@Comparator aCard.compareTo(bCard)
            }
        }

        return@Comparator 0
    }

    fun part2(input: List<String>): Int {
        val hands = parse(input, nonNumbers2).sortedWith(strengthComparator2)
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