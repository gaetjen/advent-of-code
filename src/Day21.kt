object Day21 {
    sealed class Monkey {
        companion object {
            val allMonkeys: MutableMap<String, Monkey> = mutableMapOf()
        }
        abstract fun result(): Long
        data class Number(val result: Long) : Monkey() {
            override fun result(): Long {
                return result
            }

        }
        data class Operator(
            val op1: String,
            val op2: String,
            val operation: (Long, Long) -> Long) : Monkey() {
            override fun result(): Long {
                val r1 = allMonkeys[op1]?.result()!!
                val r2 = allMonkeys[op2]?.result()!!
                return operation(r1, r2)
            }
        }

    }

    fun String.toOperation() = when (this) {
        "+" -> {a: Long, b: Long -> a + b}
        "-" -> {a: Long, b: Long -> a - b}
        "*" -> {a: Long, b: Long -> a * b}
        "/" -> {a: Long, b: Long -> a / b}
        else -> error("not an operation!")
    }

    private fun parse(input: List<String>) {
        val monkeys = input.associate { line ->
            val elements = line.split(": ")
            val name = elements.first()
            val tail = elements.last().split(" ")
            val monke = if (tail.size == 1) {
                Monkey.Number(elements.last().toLong())
            } else {
                Monkey.Operator(tail[0], tail.last(), tail[1].toOperation())
            }
            name to monke
        }
        Monkey.allMonkeys.putAll(monkeys)
    }

    fun part1(input: List<String>): Long? {
        parse(input)
        return Monkey.allMonkeys["root"]?.result()
    }

    fun part2(input: List<String>): Long {
        parse(input)
        /*
        for (t in 0..1_000_000L) {
            Monkey.allMonkeys["humn"] = Monkey.Number(t)
            if (Monkey.allMonkeys["root"]?.result() == 0L) {
                return t
            }
        }
        error("nothing found")
        //return 0L*/
        return 0L
    }
}

fun main() {
    val testInput = """
        root: pppw + sjmn
        dbpl: 5
        cczh: sllz + lgvd
        zczc: 2
        ptdq: humn - dvpt
        dvpt: 3
        lfqf: 4
        humn: 5
        ljgn: 2
        sjmn: drzm * dbpl
        sllz: 4
        pppw: cczh / lfqf
        lgvd: ljgn * ptdq
        drzm: hmdt - zczc
        hmdt: 32
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day21.part1(testInput))
    println(Day21.part2(testInput))

    println("------Real------")
    val input = readInput("resources/day21")
    println(Day21.part1(input))
    println(Day21.part2(input))
}
