object Day11 {
    data class Monkey(
        var items: MutableList<Long>,
        val operation: (Long) -> Long,
        val divisible: Int,
        val targetTrue: Int,
        val targetFalse: Int,
        var numInspections: Long = 0
    ) {
        fun throwAll(monkeys: List<Monkey>) {
            numInspections += items.size
            items.forEach {
                val newWorry = operation(it) / 3
                if (newWorry % divisible == 0L) {
                    monkeys[targetTrue].items.add(newWorry)
                } else {
                    monkeys[targetFalse].items.add(newWorry)
                }
            }
            items = mutableListOf()
        }
        fun throwAllBigWorry(monkeys: List<Monkey>) {
            val div = monkeys.map { it.divisible }.reduce { acc, d -> acc * d }
            numInspections += items.size
            items.forEach {
                val newWorry = operation(it) % div
                if (newWorry % divisible== 0L) {
                    monkeys[targetTrue].items.add(newWorry)
                } else {
                    monkeys[targetFalse].items.add(newWorry)
                }
            }
            items = mutableListOf()
        }
    }

    private fun toMonkey(input: List<String>): Monkey {
        val items = input[1].substringAfterLast(": ").split(", ").map { it.toLong() }.toMutableList()
        val op = toOperation(input[2])
        val (divisible, targetTrue, targetFalse) = input.drop(3).map { it.substringAfterLast(" ").toInt() }
        return Monkey(items, op, divisible, targetTrue, targetFalse)
    }

    private fun toOperation(input: String): (Long) -> Long {
        val (operator, operandStr) = input.substringAfterLast("old ").split(" ")
        if (operandStr == "old") {
            return { it -> (it * it) }
        }
        val operand = operandStr.toInt()
        return when (operator) {
            "+" -> { it -> (it + operand) }
            "*" -> { it -> (it * operand) }
            else -> error("unknown operator")
        }
    }

    private fun parse(input: List<String>): List<Monkey> {
        return input.split { it.isEmpty() }
            .map { toMonkey(it) }
    }

    fun part1(input: List<String>): Long {
        val monkeys = parse(input)
        repeat(20) {
            monkeys.forEach {it.throwAll(monkeys)}
        }
        val inspections = monkeys.sortedBy { it.numInspections }.takeLast(2)
        return inspections[0].numInspections * inspections[1].numInspections
    }

    fun part2(input: List<String>): Long {
        val monkeys = parse(input)
        repeat(10000) {
            monkeys.forEach {it.throwAllBigWorry(monkeys)}
        }
        val inspections = monkeys.sortedByDescending { it.numInspections }
        println(inspections.map { it.numInspections })
        return inspections[0].numInspections * inspections[1].numInspections
    }
}

fun main() {
    val testInput = """
        Monkey 0:
  Starting items: 79, 98
  Operation: new = old * 19
  Test: divisible by 23
    If true: throw to monkey 2
    If false: throw to monkey 3

Monkey 1:
  Starting items: 54, 65, 75, 74
  Operation: new = old + 6
  Test: divisible by 19
    If true: throw to monkey 2
    If false: throw to monkey 0

Monkey 2:
  Starting items: 79, 60, 97
  Operation: new = old * old
  Test: divisible by 13
    If true: throw to monkey 1
    If false: throw to monkey 3

Monkey 3:
  Starting items: 74
  Operation: new = old + 3
  Test: divisible by 17
    If true: throw to monkey 0
    If false: throw to monkey 1
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day11.part1(testInput))
    println(Day11.part2(testInput))

    println("------Real------")
    val input = readInput("resources/day11")
    println(Day11.part1(input))
    println(Day11.part2(input))
}
