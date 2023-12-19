package y2023

import util.readInput
import util.split
import util.timingStatistics

object Day19 {
    data class MachinePart(
        val x: Int,
        val m: Int,
        val a: Int,
        val s: Int
    ) {
        fun rating(): Int = x + m + a + s
    }

    data class RuleResult(
        val accepted: Boolean?,
        val reference: String?
    ) {
        companion object {
            fun fromString(str: String): RuleResult {
                return when (str) {
                    "A" -> RuleResult(true, null)
                    "R" -> RuleResult(false, null)
                    else -> RuleResult(null, str)
                }
            }
        }
    }

    private fun parse(input: List<String>): Pair<MutableMap<String, List<(MachinePart) -> RuleResult>>, List<MachinePart>> {
        val (workflowsInput, machinePartsInput) = input.split { it.isBlank() }
        val workflows: MutableMap<String, List<(MachinePart) -> RuleResult>> = mutableMapOf()
        workflowsInput.forEach {
            val (name, rules) = it.split('{', '}')
            val ruleList = rules.split(',').map { rule ->
                if (':' !in rule) {
                    { _: MachinePart -> RuleResult.fromString(rule) }
                } else {
                    val (condition, result) = rule.split(':')
                    val field = condition[0]
                    val op = condition[1]
                    val value = condition.drop(2).toInt()
                    val refFun = when (field) {
                        'x' -> { part: MachinePart -> part.x }
                        'm' -> { part: MachinePart -> part.m }
                        'a' -> { part: MachinePart -> part.a }
                        's' -> { part: MachinePart -> part.s }
                        else -> throw IllegalArgumentException("Unknown field $field")
                    }
                    val compFun = when (op) {
                        '<' -> { a: Int, b: Int -> a < b }
                        '>' -> { a: Int, b: Int -> a > b }
                        else -> throw IllegalArgumentException("Unknown operator $op")
                    }
                    { part: MachinePart ->
                        if (compFun(refFun(part), value)) {
                            RuleResult.fromString(result)
                        } else {
                            RuleResult(null, null)
                        }
                    }
                }
            }
            workflows[name] = ruleList
        }

        val machineParts = machinePartsInput.map { line ->
            val (x, m, a, s) = line.dropLast(1).split('=', ',').chunked(2).map { it[1] }
            MachinePart(x.toInt(), m.toInt(), a.toInt(), s.toInt())
        }

        return workflows to machineParts
    }

    fun part1(input: List<String>): Int {
        val (workflows, parts) = parse(input)
        return parts.filter { part ->
            var currentWorkflow = workflows["in"]!!
            var currentResult = currentWorkflow.first()(part)
            while (currentResult.accepted == null) {
                currentResult = currentWorkflow.map { it(part) }.first { it.accepted != null || it.reference != null }
                if (currentResult.accepted == null) {
                    currentWorkflow = workflows[currentResult.reference!!]!!
                }
            }
            currentResult.accepted!!
        }.sumOf { it.rating() }
    }

    data class SearchSpace(
        val x: List<LongRange>,
        val m: List<LongRange>,
        val a: List<LongRange>,
        val s: List<LongRange>,
        val rules: List<Rule>,
        var ruleIdx: Int
    ) {
        fun size(): Long {
            return sumOfRanges(x) * sumOfRanges(m) * sumOfRanges(a) * sumOfRanges(s)
        }

        fun splitBy(field: Char, splitAt: Int): Pair<SearchSpace, SearchSpace> {
            val toSplit = when (field) {
                'x' -> x
                'm' -> m
                'a' -> a
                's' -> s
                else -> throw IllegalArgumentException("Unknown field $field")
            }
            val splitIdx = toSplit.indexOfFirst { splitAt in it }
            if (splitIdx == -1) {
                val (before, after) = toSplit.partition { it.last < splitAt }
                return splitTo(field, before, after)
            } else {
                val (before, after) = toSplit.take(splitIdx) to toSplit.drop(splitIdx + 1)
                val splitRange = toSplit[splitIdx]
                val (beforeSplit, afterSplit) = splitRange.first..splitAt to (splitAt + 1)..splitRange.last
                return splitTo(field, before + listOf(beforeSplit), listOf(afterSplit) + after)
            }

        }

        private fun splitTo(
            field: Char,
            before: List<LongRange>,
            after: List<LongRange>
        ) = when (field) {
            'x' -> copy(x = before) to copy(x = after)
            'm' -> copy(m = before) to copy(m = after)
            'a' -> copy(a = before) to copy(a = after)
            's' -> copy(s = before) to copy(s = after)
            else -> throw IllegalArgumentException("Unknown field $field")
        }
    }

    private fun sumOfRanges(ranges: List<LongRange>) = ranges.sumOf { it.last - it.first + 1 }


    data class Rule(
        val ref: Char?,
        val splitAt: Int?,
        val results: Pair<RuleResult, RuleResult?>,
    )

    fun parse2(input: List<String>): MutableMap<String, List<Rule>> {
        val workflowsInput = input.takeWhile { it.isNotBlank() }
        val workflows: MutableMap<String, List<Rule>> = mutableMapOf()
        workflowsInput.forEach { line ->
            val (name, rules) = line.split('{', '}')
            workflows[name] = rules.split(',').map { rule ->
                if (':' !in rule) {
                    Rule(
                        ref = null,
                        splitAt = null,
                        results = RuleResult.fromString(rule) to null
                    )
                } else {
                    val (condition, result) = rule.split(':')
                    val field = condition[0]
                    val op = condition[1]
                    val value = condition.drop(2).toInt()
                    val (split, results) = when (op) {
                        '<' -> value - 1 to (RuleResult.fromString(result) to RuleResult(null, null))
                        '>' -> value to (RuleResult(null, null) to RuleResult.fromString(result))
                        else -> throw IllegalArgumentException("Unknown operator $op")
                    }
                    Rule(
                        ref = field,
                        splitAt = split,
                        results = results
                    )
                }
            }
        }
        return workflows
    }

    fun part2(input: List<String>): Long {
        val workflows = parse2(input)
        val start = SearchSpace(
            x = listOf(1L..4000),
            m = listOf(1L..4000),
            a = listOf(1L..4000),
            s = listOf(1L..4000),
            rules = workflows["in"]!!,
            ruleIdx = 0
        )
        val acceptedSpaces = mutableListOf<SearchSpace>()
        var frontier = listOf(start)
        while (frontier.isNotEmpty()) {
            frontier = frontier.flatMap { space ->
                val rule = space.rules[space.ruleIdx++]
                val ref = rule.ref
                if (ref == null) {
                    val ruleResult = rule.results.first
                    listOfNotNull(updatedSearchSpaces(ruleResult, acceptedSpaces, space, workflows))
                } else {
                    val splitAt = rule.splitAt!!
                    val (result1, result2) = rule.results
                    val (space1, space2) = space.splitBy(ref, splitAt)
                    listOfNotNull(
                        updatedSearchSpaces(result1, acceptedSpaces, space1, workflows),
                        updatedSearchSpaces(result2!!, acceptedSpaces, space2, workflows)
                    )
                }
            }
        }

        return acceptedSpaces.sumOf { it.size() }
    }

    private fun updatedSearchSpaces(
        ruleResult: RuleResult,
        acceptedSpaces: MutableList<SearchSpace>,
        space: SearchSpace,
        workflows: MutableMap<String, List<Rule>>
    ): SearchSpace? {
        if (ruleResult.accepted == true) {
            acceptedSpaces.add(space)
        }
        return if (ruleResult.accepted != null) {
            null
        } else if (ruleResult.reference == null) {
            space
        } else {
            space.copy(rules = workflows[ruleResult.reference]!!, ruleIdx = 0)
        }
    }
}

fun main() {
    val testInput = """
        px{a<2006:qkq,m>2090:A,rfg}
        pv{a>1716:R,A}
        lnx{m>1548:A,A}
        rfg{s<537:gd,x>2440:R,A}
        qs{s>3448:A,lnx}
        qkq{x<1416:A,crn}
        crn{x>2662:A,R}
        in{s<1351:px,qqz}
        qqz{s>2770:qs,m<1801:hdj,R}
        gd{a>3333:R,R}
        hdj{m>838:A,pv}

        {x=787,m=2655,a=1222,s=2876}
        {x=1679,m=44,a=2067,s=496}
        {x=2036,m=264,a=79,s=2244}
        {x=2461,m=1339,a=466,s=291}
        {x=2127,m=1623,a=2188,s=1013}
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day19.part1(testInput))
    println(Day19.part2(testInput))

    println("------Real------")
    val input = readInput(2023, 19)
    println("Part 1 result: ${Day19.part1(input)}")
    println("Part 2 result: ${Day19.part2(input)}")
    timingStatistics { Day19.part1(input) }
    timingStatistics { Day19.part2(input) }
}