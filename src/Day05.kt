object Day05 {
    data class Move(
        val amount: Int,
        val from: Int,
        val to: Int
    )

    private fun transpose(matrix: List<List<Char>>): List<List<Char>> {
        return List(matrix.last().last().toString().toInt()) {stackIdx ->
            List(matrix.size - 1) {rowIdx ->
                matrix[rowIdx][stackIdx]
            }.reversed()
        }
    }

    private fun stackRowsToColumns(stacks: List<String>): MutableList<MutableList<Char>> {
        val elements = stacks.map { row ->
            row.chunked(4).map { it[1] }
                .toList()
        }
        return transpose(elements).map { row -> row.takeWhile { it != ' ' }.toMutableList() }.toMutableList()
    }

    fun part1(stacks: List<String>, moves: List<String>): String {
        val stackLists = stackRowsToColumns(stacks)
        val parsedMoves = parseMoves(moves)
        parsedMoves.forEach {
            applyMove(it, stackLists)
        }
        println(stackLists)
        return stackLists.map { it.last() }.joinToString("")
    }

    private fun applyMove(move: Move, stacks: MutableList<MutableList<Char>>) {
        for (i in 1..move.amount) {
            stacks[move.to].add(stacks[move.from].last())
            stacks[move.from].removeLast()
        }
    }

    private fun parseMoves(moves: List<String>): List<Move> {
        return moves.map {
            val splits = it.split(' ')
            Move(splits[1].toInt(), splits[3].toInt()-1, splits[5].toInt()-1)
        }
    }

    fun part2(stacks: List<String>, moves: List<String>): String {
        val stackLists = stackRowsToColumns(stacks)
        val parsedMoves = parseMoves(moves)
        parsedMoves.forEach {
            applyMove2(it, stackLists)
        }
        println(stackLists)
        return stackLists.map { it.last() }.joinToString("")
    }

    private fun applyMove2(move: Move, stacks: MutableList<MutableList<Char>>) {
        stacks[move.to].addAll(stacks[move.from].takeLast(move.amount))
        stacks[move.from] = stacks[move.from].dropLast(move.amount).toMutableList()
    }
}

/*
     [H]         [D]     [P]
 [W] [B]         [C] [Z] [D]
 [T] [J]     [T] [J] [D] [J]
 [H] [Z]     [H] [H] [W] [S]     [M]
 [P] [F] [R] [P] [Z] [F] [W]     [F]
 [J] [V] [T] [N] [F] [G] [Z] [S] [S]
 [C] [R] [P] [S] [V] [M] [V] [D] [Z]
 [F] [G] [H] [Z] [N] [P] [M] [N] [D]
  1   2   3   4   5   6   7   8   9
 */
fun main() {
    val input = readInput("resources/day05")
    val stacks = input.takeWhile { it.isNotEmpty() }
    val moves = input.dropWhile { it.isNotEmpty() }.drop(1)

    println(Day05.part1(stacks, moves))
    println(Day05.part2(stacks, moves))
}