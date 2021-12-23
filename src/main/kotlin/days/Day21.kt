/*
 * Copyright (c) 2021 Leon Linhart
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package days

import utils.*

fun main() {
    val pattern = """Player (\d+) starting position: (\d+)""".toRegex()

    val players = readInput().map { line ->
        val (_, pos) =  pattern.matchEntire(line)?.destructured ?: error("Could not parse $line")
        pos.toInt()
    }

    fun part1(): Int {
        val positions = players.mapTo(mutableListOf()) { it }
        val scores = players.mapTo(mutableListOf()) { 0 }

        var roll = 0

        outer@while (true) {
            for (player in players.indices) {
                var advance = 0
                for (i in 0..2) advance += ((++roll - 1) % 100) + 1

                positions[player] = ((positions[player] + advance - 1) % 10) + 1
                scores[player] = scores[player] + positions[player]

                if (scores[player] >= 1000) break@outer
            }
        }

        return scores.minOf { it } * roll
    }

    fun part2(): Long {
        data class State(
            val curPos: Int, val curScore: Int,
            val nxtPos: Int, val nxtScore: Int,
            val roll: Int
        )

        data class Result(val p1Wins: Long, val p2Wins: Long)

        val rolls = listOf(1, 2, 3).let { sides ->
            sides.flatMap { first -> sides.flatMap { second -> sides.map { third -> first + second + third } } }
        }

        val cache = mutableMapOf<State, Result>()

        fun State.process(): Result = when {
            curScore >= 21 || nxtScore >= 21 -> {
                if (roll % 2 == 0 && curScore <= nxtScore)
                    Result(p1Wins = 1, p2Wins = 0)
                else
                    Result(p1Wins = 0, p2Wins = 1)
            }
            else -> cache.getOrPut(this) {
                rolls.map { advance ->
                    val newPos = ((curPos + advance - 1) % 10) + 1

                    State(
                        curPos = nxtPos,
                        curScore = nxtScore,
                        nxtPos = newPos,
                        nxtScore = curScore + newPos,
                        roll = roll + 1
                    )
                }.map(State::process)
                    .reduce { acc, e ->
                        Result(acc.p1Wins + e.p1Wins, acc.p2Wins + e.p2Wins)
                    }
            }
        }

        return State(
            curPos = players[0],
            curScore = 0,
            nxtPos = players[1],
            nxtScore = 0,
            roll = 0
        ).process().let { maxOf(it.p1Wins, it.p2Wins) }
    }

    println("Part 1: ${part1()}")
    println("Part 2: ${part2()}")
}