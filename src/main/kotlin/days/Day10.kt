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
@file:OptIn(ExperimentalStdlibApi::class)
package days

import utils.*

fun main() {
    val input = readInput()

    val pointsByCharacter = mapOf(
        ')' to 3,
        ']' to 57,
        '}' to 1197,
        '>' to 25137
    )

    data class Params(val line: List<Char>, val index: Int = 0)

    abstract class Res

    class Pass(val index: Int) : Res()
    class Fail(val firstCorruptIndex: Int) : Res()

    val foo = DeepRecursiveFunction<Params, Res?> { params ->
        var (line, index) = params
        if (params.index >= line.size - 1) return@DeepRecursiveFunction null

        val expectedClosingChar = when (line[params.index]) {
            '(' -> ')'
            '[' -> ']'
            '{' -> '}'
            '<' -> '>'
            else -> return@DeepRecursiveFunction Pass(index)
        }

        index = callRecursive(params.copy(index = index + 1)).let {
            when (it) {
                is Pass -> it.index
                else -> return@DeepRecursiveFunction it
            }
        }

        val closingChar = line[index]

        if (closingChar != expectedClosingChar) {
            return@DeepRecursiveFunction Fail(index)
        }

        callRecursive(params.copy(index = index + 1))
    }

    println("Part 1: ${input.mapNotNull { line -> foo.invoke(Params(line.toList()))?.let { pointsByCharacter[line[(it as Fail).firstCorruptIndex]] } }.sum()}")
}