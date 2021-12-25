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

/*
 * Can't solve this in code without writing a full interval constraint solver/optimizer...
 *
 * This is not my idea, but I'll document it anyway because otherwise the
 * implementation is everything but trivial.
 *
 * The input can be divided in 14 blocks (each starting with an `inp` instruction). Each
 * input block roughly looks as follows:
 *
 * inp w
 * mul x 0
 * add x z
 * mod x 26
 * div z {DIV}
 * add x {CHECK}
 * eql x w
 * eql x 0
 * mul y 0
 * add y 25
 * mul y x
 * add y 1
 * mul z y
 * mul y 0
 * add y w
 * add y {OFFSET}
 * mul y x
 * add z y
 *
 * where {DIV}, {CHECK} and {OFFSET} vary per block.
 *
 * A couple of notes though:
 * 1. {DIV} is 1 in half of the blocks and 26 in the other half.
 * 2. {CHECK} is positive exactly when {DIV} is 1.
 * 3. x and y are cleared before they are used.
 *
 * Using this knowledge, the following function can be derived:
 *
 * fun (w, z):
 *   x = (z % 26) + {CHECK}
 *	 if (CHECK < 0)
 *      z /= 26
 *   else
 *      z = 26 * z + w + {OFFSET}
 *
 *     {CHECK}, {OFFSET}
 *  1.      10,        1
 *  2.      11,        9
 *  3.      14,       12
 *  4.      13,        6
 *  5.      -6,        9
 *  6.     -14,       15
 *  7.      14,        7
 *  8.      13,       12
 *  9.      -8,       15
 * 10.     -15,        3
 * 11.      10,        6
 * 12.     -11,        2
 * 13.     -13,       10
 * 14.      -4,       12
 *
 *  1. PUSH input[0] + 1
     *  2. PUSH input[1] + 9
         *  3. PUSH input[2] + 12
             *  4. PUSH input[3] + 6
             *  5. POP. input[4] == v - 6
         *  6. POP. input[5] == v - 14
         *  7. PUSH input[6] + 7
             *  8. PUSH input[7] + 12
             *  9. POP. input[8] == v - 8
         * 10. POP. input[9] == v - 15
         * 11. PUSH input[10] + 6
         * 12. POP. input[11] == v - 11
     * 13. POP. input[12] == v - 13
 * 14. POP. input[13] == v - 4
 *
 *
 * input[4] = input[3]
 * input[5] = input[2] - 2
 * input[8] = input[7] + 4
 * input[9] = input[6] - 8
 * input[11] = input[10] - 5
 * input[12] = input[1] - 4
 * input[13] = input[0] - 3
 */

fun main() {
    fun part1() = "99999795919456"
    fun part2() = "45311191516111"

    println("Part 1: ${part1()}")
    println("Part 2: ${part2()}")
}