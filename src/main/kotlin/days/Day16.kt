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
        .single()
        .toList()
        .flatMap { it.digitToInt(radix = 16).toString(radix = 2).padStart(length = 4, '0').toList() }

    var advance = 0

    fun <T> Iterable<T>.takeAndAdvance(n: Int): List<T> =
        take(n).also { advance += n }

    fun <T> List<T>.firstAndAdvance(): T =
        first().also { advance++ }

    val parseIRElement = DeepRecursiveFunction<Unit, IRElement> {
        val version = input.drop(advance).takeAndAdvance(3).joinToString(separator = "").toInt(radix = 2)

        when (val type = input.drop(advance).takeAndAdvance(3).joinToString(separator = "").toInt(radix = 2)) {
            4 -> { // Literal Packet
                val literal = input
                    .drop(advance)
                    .chunked(5)
                    .takeWhileWithFirstOffender { it[0] == '1' }
                    .also { advance += 5 * it.size }
                    .flatMap { it.drop(1) }
                    .joinToString(separator = "")
                    .toLong(radix = 2)

                IRLiteral(version, type, literal)
            }
            else -> { // Operator Packet
                when (val lengthTypeID = input.drop(advance).firstAndAdvance().digitToInt(radix = 2)) {
                    0 -> {
                        val totalPacketLength = input.drop(advance).takeAndAdvance(15).joinToString(separator = "").toInt(radix = 2) - 6 /* = header */ - 1 /* = lengthTypeID */

                        val a = advance
                        val subPackets = mutableListOf<IRElement>()

                        while (advance - a < totalPacketLength) {
                            subPackets += callRecursive(Unit)
                        }

                        IRExpression(version, type, subPackets)
                    }
                    1 -> {
                        val subPacketCount = input.drop(advance).takeAndAdvance(11).joinToString(separator = "").toInt(radix = 2)
                        val subPackets = (0 until subPacketCount).map { callRecursive(Unit) }

                        IRExpression(version, type, subPackets)
                    }
                    else -> error("Unexpected lengthTypeID: $lengthTypeID")
                }
            }
        }
    }

    val irElement = parseIRElement.invoke(Unit)

    fun IRElement.part1(): Int = when (this) {
        is IRExpression -> version + elements.sumOf { it.part1() }
        is IRLiteral -> version
    }

    println(irElement)
    println("Part 1: ${irElement.part1()}")
}

private sealed interface IRElement {
    val version: Int
    val type: Int
}

private data class IRExpression(
    override val version: Int,
    override val type: Int,
    val elements: List<IRElement>
) : IRElement

private data class IRLiteral(
    override val version: Int,
    override val type: Int,
    val value: Long
): IRElement