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
    data class BitsAtIndex(val index: Int, val gammaBit: Char, val epsilonBit: Char)
    data class IncompleteDiagnosticData(val gammaBits: String, val epsilonBits: String)

    data class DiagnosticReport(val gammaRate: Int, val epsilonRate: Int)

    val incompleteDiagnosticData = readInput()
        .flatMap { sequence -> sequence.mapIndexed { index, c -> index to c } }
        .groupBy { it.first }
        .map { (index, values) ->
            val bits = values.map { it.second }.groupingBy { it }.eachCount()

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

    println("Part 1: ${report.gammaRate * report.epsilonRate}")
}