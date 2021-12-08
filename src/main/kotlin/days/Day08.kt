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

import utils.readInput

fun main() {
    data class Data(val input: List<Set<Char>>, val output: List<Set<Char>>)

    val data = readInput().map { line ->
        val (i, o) = line.split(" | ")
        Data(i.split(" ").map { it.toSet() }, o.split(" ").map { it.toSet() })
    }

    fun Data.solve(): Int {
        /*
         * Segments/Digits
         * - 2 => 1
         * - 3 => 7
         * - 4 => 4
         * - 5 => 2, 3, 5
         * - 6 => 6, 9, 0
         * - 7 => 8
         */

        val one = input.single { it.size == 2 }
        val four = input.single { it.size == 4 }
        val seven = input.single { it.size == 3 }
        val eight = input.single { it.size == 7 }

        val fiveSegmentsDigits = input.filter { it.size == 5 }
        val sixSegmentsDigits = input.filter { it.size == 6 }

        val three = fiveSegmentsDigits.single { fiveSegmentsDigit -> one.all { it in fiveSegmentsDigit } }
        val nine = sixSegmentsDigits.single { sixSegmentsDigit -> four.all { it in sixSegmentsDigit } }
        val five = fiveSegmentsDigits.single { fiveSegmentsDigit -> fiveSegmentsDigit != three && fiveSegmentsDigit.all { it in nine } }

        val six = sixSegmentsDigits.single { sixSegmentsDigit -> sixSegmentsDigit != nine && five.all { it in sixSegmentsDigit } }
        val zero = sixSegmentsDigits.single { it != six && it != nine }
        val two = fiveSegmentsDigits.single { it != three && it != five }

        return output.joinToString(separator = "") {
            when (it) {
                zero -> "0"
                one -> "1"
                two -> "2"
                three -> "3"
                four -> "4"
                five -> "5"
                six -> "6"
                seven -> "7"
                eight -> "8"
                nine -> "9"
                else -> error("")
            }
        }.toInt()
    }

    println("Part 1: ${data.sumOf { it.solve().toString().map(Char::digitToInt).count { it == 1 || it == 4 || it == 7 || it == 8 } }}")
    println("Part 2: ${data.sumOf { it.solve() }}")
}