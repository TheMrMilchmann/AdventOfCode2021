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
    val input = readInput()

    val template = input.first()
    val rules = input.drop(2)
        .map { it.split(" -> ") }
        .associateBy(
            keySelector = { it[0] },
            valueTransform = { it[1] }
        )

    fun steps(): Sequence<Map<String, Long>> =
        generateSequence(template.windowed(size = 2).groupingBy { it }.eachCount().mapValues { (_, count) -> count.toLong() }) { prev ->
            prev.flatMap { (pair, count) ->
                val inserted = rules[pair] ?: error("No matching rule for pattern '$pair'")

                listOf("${pair[0]}$inserted" to count, "$inserted${pair[1]}" to count)
            }.groupingBy { it.first }
                .fold(0L) { acc, (_, count) -> acc + count }
        }

    fun Map<String, Long>.eachCount(): Map<Char, Long> =
        flatMap { (pair, count) -> listOf(pair[0] to count, pair[1] to count) }
            .groupingBy { it.first }
            .fold(0L) { acc, (_, count) -> acc + count }
            .mapValues { (polymer, count) ->
                var actualCount = count / 2

                if (polymer == template.first())
                    actualCount++

                if (polymer == template.last())
                    actualCount++

                actualCount
            }

    fun resultAfter(steps: Int): Long {
        val counts = steps().drop(steps).first().eachCount()

        val min = counts.minOf { (_, count) -> count }
        val max = counts.maxOf { (_, count) -> count }

        return max - min
    }

    println("Part 1: ${resultAfter(steps = 10)}")
    println("Part 2: ${resultAfter(steps = 40)}")
}