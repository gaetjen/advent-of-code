object Day21 {
    sealed class Monkey(val name: String) {
        //abstract val name: String
        companion object {
            val allMonkeys: MutableMap<String, Monkey> = mutableMapOf()
            val allDependents: MutableMap<String, List<String>> = mutableMapOf()
        }

        abstract fun result(): Long
        abstract fun dependents(): List<String>
        class Number(name: String, val result: Long) : Monkey(name) {
            override fun result(): Long {
                return result
            }

            override fun dependents(): List<String> {
                return listOf(name)
            }

        }

        class Operator(
            name: String,
            val op1: String,
            val op2: String,
            val operation: String,
        ) : Monkey(name) {
            override fun result(): Long {
                val r1 = allMonkeys[op1]?.result()!!
                val r2 = allMonkeys[op2]?.result()!!
                return operation.toOperation()(r1, r2)
            }

            override fun dependents(): List<String> {
                if (name !in allDependents) {
                    allDependents[name] = allMonkeys[op1]!!.dependents() + allMonkeys[op2]!!.dependents()
                }
                return allDependents[name]!!
            }
        }

    }

    fun String.toOperation() = when (this) {
        "+" -> { a: Long, b: Long -> a + b }
        "-" -> { a: Long, b: Long -> a - b }
        "*" -> { a: Long, b: Long -> a * b }
        "/" -> { a: Long, b: Long -> a / b }
        else -> error("not an operation!")
    }

    private fun parse(input: List<String>) {
        val monkeys = input.associate { line ->
            val elements = line.split(": ")
            val name = elements.first()
            val tail = elements.last().split(" ")
            val monke = if (tail.size == 1) {
                Monkey.Number(name, elements.last().toLong())
            } else {
                Monkey.Operator(name, tail[0], tail.last(), tail[1])
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
        Monkey.allMonkeys.clear()
        Monkey.allDependents.clear()
        parse(input)
        val monkeyNames = Monkey.allMonkeys.keys
        val calledNames = Monkey.allMonkeys.values.filterIsInstance<Monkey.Operator>().flatMap { listOf(it.op1, it.op2) }
        println("num monkeys :" + monkeyNames.size)
        println("called monkeys :" + calledNames.size)
        println("unique called :" + calledNames.toSet().size)
        println(monkeyNames - calledNames.toSet())
        var c1 = (Monkey.allMonkeys["root"]!! as Monkey.Operator).op1
        var c2 = (Monkey.allMonkeys["root"]!! as Monkey.Operator).op2
        var (currentRoot, desiredResult) = if ("humn" in Monkey.allMonkeys[c1]!!.dependents()) {
            c1 to Monkey.allMonkeys[c2]!!.result()
        } else {
            c2 to Monkey.allMonkeys[c1]!!.result()
        }

        while (true) {
            if (c1 == "humn" || c2 == "humn") {
                return desiredResult
            }
            val rootMonkey = Monkey.allMonkeys[currentRoot] as Monkey.Operator
            c1 = rootMonkey.op1
            c2 = rootMonkey.op2
            val (nextRoot, subResult) = if ("humn" in Monkey.allMonkeys[c1]!!.dependents()) {
                c1 to Monkey.allMonkeys[c2]!!.result()
            } else {
                c2 to Monkey.allMonkeys[c1]!!.result()
            }
            desiredResult = nextDesired(rootMonkey, c1, nextRoot, desiredResult, subResult)
            currentRoot = nextRoot
        }
    }

    private fun nextDesired(
        rootMonkey: Monkey.Operator,
        first: String,
        nextRoot: String,
        currentResult: Long,
        subResult: Long,
    ): Long {
        return when (rootMonkey.operation) {
            "+" -> currentResult - subResult
            "*" -> currentResult / subResult
            "-" -> if (nextRoot == first) currentResult + subResult else -currentResult + subResult
            "/" -> if (nextRoot == first) currentResult * subResult else subResult / currentResult
            else -> error("not operator")
        }
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
