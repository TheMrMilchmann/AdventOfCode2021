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
    val commands = readInput()

    fun part1(): Int {
        var verticalPos = 0
        var horizontalPos = 0

        for ((instruction, y) in commands.map { it.split(" ") }) {
            val amount = y.toInt()

            when (instruction) {
                "forward" -> horizontalPos += amount
                "down" -> verticalPos += amount
                "up" -> verticalPos -= amount
            }
        }

        return verticalPos * horizontalPos
    }

    fun part2(): Int {
        var verticalPos = 0
        var horizontalPos = 0
        var aim = 0

        for ((instruction, y) in commands.map { it.split(" ") }) {
            val amount = y.toInt()

            when (instruction) {
                "forward" -> {
                    horizontalPos += amount
                    verticalPos += aim * amount
                }
                "down" -> aim += amount
                "up" -> aim -= amount
            }
        }

        return verticalPos * horizontalPos
    }

    println("Part 1: ${part1()}")
    println("Part 2: ${part2()}")
}