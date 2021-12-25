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
    var data = readInput().map(String::toList).toGrid()

    var hasMoved: Boolean
    var move = 0

    do {
        hasMoved = false
        move++

        var res = data.map { pos, c ->
            if (c == '>' && data[data.shiftRightWrapped(pos)] == '.') {
                hasMoved = true
                '.'
            } else if (c == '.' && data[data.shiftLeftWrapped(pos)] == '>') {
                hasMoved = true
                '>'
            } else
                c
        }

        res = res.map { pos, c ->
            if (c == 'v' && res[res.shiftDownWrapped(pos)] == '.') {
                hasMoved = true
                '.'
            } else if (c == '.' && res[res.shiftUpWrapped(pos)] == 'v') {
                hasMoved = true
                'v'
            } else
                c
        }

        data = res
    } while (hasMoved)

    println("Part 1: $move")
}