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

    data class Params(val line: List<Char>, val index: Int = 0)

    abstract class Res

    data class Pass(val index: Int) : Res()
    data class Fail(val firstCorruptIndex: Int, val char: Char) : Res()
    data class Incomplete(val foo: List<Char>) : Res()

    val foo = DeepRecursiveFunction<Params, Res> { params ->
        var (line, index) = params
        if (params.index > line.size - 1) return@DeepRecursiveFunction Incomplete(listOf())

        val expectedClosingChar = when (line[index]) {
            '(' -> ')'
            '[' -> ']'
            '{' -> '}'
            '<' -> '>'
            else -> return@DeepRecursiveFunction Pass(index)
        }

        index = callRecursive(params.copy(index = index + 1)).let {
            when (it) {
                is Pass -> it.index
                is Incomplete -> return@DeepRecursiveFunction Incomplete(it.foo + expectedClosingChar)
                is Fail -> return@DeepRecursiveFunction it
                else -> error("Unexpected result: $it")
            }
        }

        val closingChar = line[index]

        if (closingChar != expectedClosingChar)
            return@DeepRecursiveFunction Fail(index, line[index])

        callRecursive(params.copy(index = index + 1))
    }

    fun part1(): Int {
        val pointsByCharacter = mapOf(
            ')' to 3,
            ']' to 57,
            '}' to 1197,
            '>' to 25137
        )

        return input.mapNotNull { line -> foo.invoke(Params(line.toList())).let { if (it is Fail) it else null } }
            .sumOf { pointsByCharacter[it.char] ?: error("What: ${it.char}") }
    }

    fun part2(): Long {
        val pointsByCharacter = mapOf(
            ')' to 1,
            ']' to 2,
            '}' to 3,
            '>' to 4
        )

        return input.mapNotNull { line -> foo.invoke(Params(line.toList())).let { if (it is Incomplete) it else null } }
            .map { it.foo.map { char -> pointsByCharacter[char]!!.toLong() }.reduce { acc, i -> acc * 5 + i } }
            .sorted()
            .let { it[it.size / 2] }
    }

    println("Part 1: ${part1()}")
    println("Part 2: ${part2()}")
}