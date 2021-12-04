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
    data class CardEntry(val number: Int, var marked: Boolean)

    class BingoCard(val rows: List<List<CardEntry>>) {

        init {
            val rowsCount = rows.size
            require(rows.all { it.size == rowsCount })
        }

        val isComplete get() =
            rows.any { it.all { (_, marked) -> marked } } || rows.indices.any { x -> rows.all { column -> column[x].marked } }

        val entries get() = rows.flatten()

        fun mark(number: Int): Boolean {
            var changed = false

            rows.forEach {
                it.forEach { entry ->
                    if (number == entry.number && !entry.marked) {
                        changed = true
                        entry.marked = true
                    }
                }
            }

            return changed
        }

        override fun toString(): String = rows.joinToString(separator = "\n") { column ->
            column.joinToString(separator = "\t") { (number, marked) -> "${number.toString().padStart(2)}-${if (marked) "T" else "F"}"}
        }
    }

    fun parseInput(): Pair<List<Int>, List<BingoCard>> {
        val input = readInput()
        val drawnNumbers = input.first().split(",").map(String::toInt)

        val cards = input.drop(2)
            .windowed(size = 5, step = 6)
            .map { row -> BingoCard(row.map { it.trim().split("""\s+""".toRegex()).map { CardEntry(it.toInt(), false) } }) }

        return (drawnNumbers to cards)
    }

    fun part1(): Int {
        val (drawnNumbers, cards) = parseInput()

        val cardsByNumber = buildMap<Int, MutableList<BingoCard>> {
            cards.forEach { card -> card.rows.forEach { column -> column.forEach { (number, _) -> computeIfAbsent(number) { mutableListOf() }.add(card) } } }
        }

        val (drawnNumber, winningCard) = Unit.let {
            for (drawnNumber in drawnNumbers) {
                for (card in cardsByNumber[drawnNumber] ?: emptyList()) {
                    if (card.mark(drawnNumber)) {
                        if (card.isComplete) {
                            return@let drawnNumber to card
                        }
                    }
                }
            }

            error("No card won?")
        }

        val unmarkedSum = winningCard.entries.filter { (_, marked) -> !marked }.sumOf(CardEntry::number)
        return unmarkedSum * drawnNumber
    }

    fun part2(): Int {
        val (drawnNumbers, cards) = parseInput()

        val cardsByNumber = buildMap<Int, MutableList<BingoCard>> {
            cards.forEach { card -> card.rows.forEach { column -> column.forEach { (number, _) -> computeIfAbsent(number) { mutableListOf() }.add(card) } } }
        }

        val incompleteCards = ArrayList<BingoCard>(cards)

        val drawnNumber = Unit.let {
            for (drawnNumber in drawnNumbers) {
                for (card in (cardsByNumber[drawnNumber] ?: emptyList()).filter { it in incompleteCards }) {
                    if (card.mark(drawnNumber)) {
                        if (card.isComplete) {
                            if (incompleteCards.size == 1) {
                                return@let drawnNumber
                            } else {
                                incompleteCards.remove(card)
                            }
                        }
                    }
                }
            }

            error("No card 'won' last")
        }

        val losingCard = incompleteCards.single()
        val unmarkedSum = losingCard.entries.filter { (_, marked) -> !marked }.sumOf(CardEntry::number)
        return unmarkedSum * drawnNumber
    }

    println("Part 1: ${part1()}")
    println("Part 2: ${part2()}")
}