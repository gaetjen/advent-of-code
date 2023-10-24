package y2022

import util.readInput
import util.split

object Day13 {
    private fun parse(input: List<String>): List<List<ListItem>> {
        return input.split { it.isEmpty() }
            .map {
                it.map { ln ->
                    ListItem.of(ln)
                }
            }
    }

    sealed class ListItem {
        companion object {
            fun of(string: String): ListItem {
                if (string.count { it == '[' } != string.count { it == ']' }) {
                    error("opening and closing brackets don't match")
                }
                if ('[' !in string) {
                    if (',' in string) error("unexpected comma!")
                    return ListNumber(string.toInt())
                }
                val elements = splitOnTopLevelComma(string.drop(1).dropLast(1))
                return SubList(elements.map { of(it) })
            }
        }

        fun compareTo(other: ListItem): Int {
            return when {
                this is ListNumber && other is ListNumber -> this.number - other.number
                this is ListNumber && other is SubList ->  SubList(listOf(this)).compareTo(other)
                this is SubList && other is ListNumber -> this.compareTo(SubList(listOf(other)))
                this is SubList && other is SubList -> {
                    this.list.zip(other.list).map { (ti, oi) ->
                        ti.compareTo(oi)
                    }.firstOrNull { it != 0} ?: (this.list.size - other.list.size)
                }
                else -> error("exhaustive conditions")
            }
        }


        data class ListNumber(val number: Int) : ListItem()
        data class SubList(val list: List<ListItem>) : ListItem()
    }

    private fun splitOnTopLevelComma(str: String): List<String> {
        if (str.isEmpty()) return listOf()
        return if (str[0] == '[') {
            val idx = getIndexOfMatchingClosing(str)
            listOf(str.substring(0..idx)) + splitOnTopLevelComma(str.drop(idx + 2))
        } else {
            val h = str.takeWhile { it != ',' }
            val t = str.dropWhile { it != ',' }.drop(1)
            listOf(h) + splitOnTopLevelComma(t)
        }
    }

    private fun getIndexOfMatchingClosing(str: String): Int {
        var idx = 0
        var stackLevel = 1
        while (stackLevel != 0) {
            idx++
            when (str[idx]) {
                '[' -> stackLevel++
                ']' -> stackLevel--
                //else -> just continue iterating
            }
        }
        return idx
    }

    fun part1(input: List<String>): Long {
        val items = parse(input)
        return items.mapIndexed { index, listItems ->
            if (listItems[0].compareTo(listItems[1]) <= 0) {
                index + 1
            } else {
                -1
            }
        }.filter { it >= 0 }.sum().toLong()
    }

    fun part2(input: List<String>): Long {
        val cmp = Comparator<ListItem> { o1, o2 ->
            if (o1 == null || o2 == null) error("did not expect to compare nulls")
            o1.compareTo(o2)
        }
        val items = parse(input).flatten()
        val (div1, div2) = parse(listOf("[[2]]", "[[6]]")).flatten()
        val sorted = (items + listOf(div1, div2)).sortedWith(cmp)
        return (sorted.indexOf(div1) + 1L) * (sorted.indexOf(div2) + 1)
    }
}

fun main() {
    val testInput = """
        [1,1,3,1,1]
        [1,1,5,1,1]

        [[1],[2,3,4]]
        [[1],4]

        [9]
        [[8,7,6]]

        [[4,4],4,4]
        [[4,4],4,4,4]

        [7,7,7,7]
        [7,7,7]

        []
        [3]

        [[[]]]
        [[]]

        [1,[2,[3,[4,[5,6,7]]]],8,9]
        [1,[2,[3,[4,[5,6,0]]]],8,9]
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day13.part1(testInput))
    println(Day13.part2(testInput))

    println("------Real------")
    val input = readInput("resources/2022/day13")
    println(Day13.part1(input))
    println(Day13.part2(input))
}
