package y2015

import y2015.State.Companion.drainCost
import y2015.State.Companion.missileCost
import y2015.State.Companion.poisonCost
import y2015.State.Companion.rechargeCost
import y2015.State.Companion.shieldCost
import java.util.PriorityQueue
import kotlin.math.max

data class State(
    val playerHp: Int = 50,
    val playerMana: Int = 500,
    val enemyHp: Int = 51,
    val manaSpent: Int = 0,
    val shieldTimer: Int = 0,
    val poisonTimer: Int = 0,
    val rechargeTimer: Int = 0
) {
    companion object {
        const val enemyDamage = 9
        const val missileCost = 53
        const val missileDamage = 4
        const val drainCost = 73
        const val drainDamage = 2
        const val shieldCost = 113
        const val shieldDuration = 6
        const val shieldSize = 7
        const val poisonCost = 173
        const val poisonDuration = 6
        const val poisonDamage = 3
        const val rechargeCost = 229
        const val rechargeDuration = 5
        const val rechargeAmount = 101
    }

    fun nextState(action: Action): State {
        return applyAction(action)
            .applyEffects()
            .enemyMove()
            .applyEffects()
    }

    fun nextStateP2(action: Action): State {
        val next = applyAction(action)
            .applyEffects()
            .enemyMove()
            .applyEffects()
        return next.copy(
            playerHp = next.playerHp - 1
        )
    }

    fun applyAction(action: Action): State {
        val next = this.copy(
            playerMana = playerMana - action.cost,
            manaSpent = manaSpent + action.cost
        )
        return when (action) {
            Action.MISSILE -> next.copy(
                enemyHp = enemyHp - missileDamage
            )

            Action.DRAIN -> next.copy(
                playerHp = playerHp + drainDamage,
                enemyHp = enemyHp - drainDamage
            )

            Action.SHIELD -> next.copy(
                shieldTimer = shieldDuration
            )

            Action.POISON -> next.copy(
                poisonTimer = poisonDuration
            )

            Action.RECHARGE -> next.copy(
                rechargeTimer = rechargeDuration
            )
        }
    }

    fun applyEffects(): State {
        return this.copy(
            enemyHp = if (poisonTimer > 0) enemyHp - poisonDamage else enemyHp,
            playerMana = if (rechargeTimer > 0) playerMana + rechargeAmount else playerMana,
            shieldTimer = max(shieldTimer - 1, 0),
            poisonTimer = max(poisonTimer - 1, 0),
            rechargeTimer = max(rechargeTimer - 1, 0)
        )
    }

    fun enemyMove(): State {
        return if (enemyHp <= 0) {
            this
        } else {
            this.copy(
                playerHp = if (shieldTimer > 0) playerHp - enemyDamage + shieldSize else playerHp - enemyDamage
            )
        }
    }

    fun availableActions(): List<Action> {
        return if (playerHp <= 0) listOf()
        else listOfNotNull(
            Action.MISSILE,
            Action.DRAIN,
            if (shieldTimer == 0) Action.SHIELD else null,
            if (poisonTimer == 0) Action.POISON else null,
            if (rechargeTimer == 0) Action.RECHARGE else null,
        ).filter { it.cost <= playerMana }
    }
}

enum class Action(val cost: Int) {
    MISSILE(missileCost),
    DRAIN(drainCost),
    SHIELD(shieldCost),
    POISON(poisonCost),
    RECHARGE(rechargeCost)
}

object Day22 {

    fun part1(): Int {
        val start = State()
        val queue = PriorityQueue<State> { s1, s2 ->
            s1.manaSpent - s2.manaSpent
        }
        queue.add(start)
        while (true) {
            val next = queue.poll()
            if (next.enemyHp <= 0) {
                return next.manaSpent
            }
            queue.addAll(
                next.availableActions().map { next.nextState(it) }
            )
        }
    }

    fun part2(): Int {
        val start = State(playerHp = 49)
        val queue = PriorityQueue<State> { s1, s2 ->
            s1.manaSpent - s2.manaSpent
        }
        queue.add(start)
        while (true) {
            val next = queue.poll()
            if (next.enemyHp <= 0) {
                return next.manaSpent
            }
            queue.addAll(
                next.availableActions().map { next.nextStateP2(it) }
            )
        }
    }
}

fun main() {
    println("------Real------")
    println(Day22.part1())
    println(Day22.part2())
}
