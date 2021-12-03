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
    fun part1(): Int {
        data class DiagnosticReport(val gammaRate: Int, val epsilonRate: Int)
        data class BitsAtIndex(val index: Int, val gammaBit: Char, val epsilonBit: Char)
        data class IncompleteDiagnosticData(val gammaBits: String, val epsilonBits: String)

        val incompleteDiagnosticData = readInput()
            .flatMap { sequence -> sequence.mapIndexed { index, c -> index to c } }
            .groupBy(keySelector = { it.first }, valueTransform = { it.second })
            .map { (index, values) ->
                val bits = values.groupingBy { it }.eachCount()

                BitsAtIndex(
                    index,
                    bits.maxByOrNull { it.value }!!.key,
                    bits.minByOrNull { it.value }!!.key
                )
            }
            .sortedBy { it.index }
            .fold(initial = IncompleteDiagnosticData(gammaBits = "", epsilonBits = "")) { acc, it ->
                IncompleteDiagnosticData(
                    gammaBits = acc.gammaBits + it.gammaBit,
                    epsilonBits = acc.epsilonBits + it.epsilonBit
                )
            }

        val report = DiagnosticReport(
            gammaRate = incompleteDiagnosticData.gammaBits.toInt(radix = 2),
            epsilonRate = incompleteDiagnosticData.epsilonBits.toInt(radix = 2)
        )

        return report.gammaRate * report.epsilonRate
    }

    fun part2(): Int {
        val input = readInput()

        tailrec fun Pair<List<String>, List<String>>.filterDownToOne(index: Int = 0, max: Int): Pair<List<String>, List<String>> {
            var (first, second) = this

            if (first.size > 1) {
                val occs = first.groupingBy { it[index] }.eachCount()
                val mcb = if (occs['0']!! <= occs['1']!!) '1' else '0'

                first = first.filter { it[index] == mcb }
            }

            if (second.size > 1) {
                val occs = second.groupingBy { it[index] }.eachCount()
                val lcb = if (occs['0']!! > occs['1']!!) '1' else '0'

                second = second.filter { it[index] == lcb }
            }

            return if (index < max) (first to second).filterDownToOne(index + 1, max) else (first to second)
        }

        return (input to input).filterDownToOne(max = input.first().length - 1)
            .let { it.first.first().toInt(radix = 2) * it.second.first().toInt(radix = 2) }
    }

    println("Part 1: ${part1()}")
    println("Part 2: ${part2()}")
}