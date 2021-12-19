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
import kotlin.math.*

fun main() {
    fun String.parseSFNumber(): SFNumber {
        var offset = 0

        return DeepRecursiveFunction<Unit, SFNumber> {
            when (this@parseSFNumber[offset]) {
                '[' -> {
                    offset++

                    val left = callRecursive(Unit)
                    require(this@parseSFNumber[offset++] == ',')

                    val right = callRecursive(Unit)
                    require(this@parseSFNumber[offset++] == ']')

                    SFPair(left, right)
                }
                else -> {
                    val number = this@parseSFNumber.drop(offset).takeWhile(Char::isDigit)
                    offset += number.length

                    SFRegular(number.toInt())
                }
            }
        }.invoke(Unit)
    }

    val input = readInput().map { it.parseSFNumber() }

    println("Part 1: ${input.reduce { acc, e -> acc + e }.magnitude }")
}

private data class BinaryTreePath(val decisions: List<Decision>): List<BinaryTreePath.Decision> by decisions {

    enum class Decision { LEFT, RIGHT }

    operator fun div(decision: Decision) = BinaryTreePath(decisions + decision)

}

private val LEFT get() = BinaryTreePath.Decision.LEFT
private val RIGHT get() = BinaryTreePath.Decision.RIGHT

private tailrec operator fun SFNumber.get(path: BinaryTreePath): SFNumber {
    return if (path.isEmpty()) {
        this
    } else {
        check(this is SFPair)

        when (val decision = path.first()) {
            LEFT -> left[BinaryTreePath(path.drop(1))]
            RIGHT -> right[BinaryTreePath(path.drop(1))]
            else -> error("Unexpected decision: $decision")
        }
    }
}

private fun SFNumber.findLeftOf(path: BinaryTreePath): BinaryTreePath? {
    var res: BinaryTreePath

    if (path.last() == RIGHT) {
        res = BinaryTreePath(path.mapIndexed { index, decision -> if (index == path.lastIndex) LEFT else decision })
        while (this[res] is SFPair) res /= RIGHT
    } else {
        res = path

        do {
            if (res.size - 1 <= 0) return null
            res = BinaryTreePath(res.take(res.size - 1))
        } while (res.last() == LEFT)

        res = BinaryTreePath(res.take(res.size - 1)) / LEFT
        while (this[res] is SFPair) res /= RIGHT
    }

    return res
}

private fun SFNumber.findRightOf(path: BinaryTreePath): BinaryTreePath? {
    var res: BinaryTreePath

    if (path.last() == LEFT) {
        res = BinaryTreePath(path.mapIndexed { index, decision -> if (index == path.lastIndex) RIGHT else decision })
        while (this[res] is SFPair) res /= LEFT
    } else {
        res = path

        do {
            if (res.size - 1 <= 0) return null
            res = BinaryTreePath(res.take(res.size - 1))
        } while (res.last() == RIGHT)

        res = BinaryTreePath(res.take(res.size - 1)) / RIGHT
        while (this[res] is SFPair) res /= LEFT
    }

    return res
}

private fun SFNumber.replace(path: BinaryTreePath, transform: (oldValue: SFNumber) -> SFNumber): SFNumber {
    return if (path.size == 0) {
        transform(this)
    } else {
        require(this is SFPair)

        if (path.size == 1) {
            when (val decision = path.single()) {
                LEFT -> copy(left = transform(left))
                RIGHT -> copy(right = transform(right))
                else -> error("Unexpected decision: $decision")
            }
        } else {
            when (val decision = path.first()) {
                LEFT -> copy(left = left.replace(BinaryTreePath(path.decisions.drop(1)), transform))
                RIGHT -> copy(right = right.replace(BinaryTreePath(path.decisions.drop(1)), transform))
                else -> error("Unexpected decision: $decision")
            }
        }
    }
}

private fun SFNumber.reduce(): SFNumber {
    abstract class ReductionStep

    data class ExplosionStep(val pair: SFPair, val path: BinaryTreePath) : ReductionStep()
    data class SplitStep(val path: BinaryTreePath) : ReductionStep()

    fun SFNumber.reduceImpl(path: BinaryTreePath, depth: Int): ReductionStep? {
        fun SFNumber.findExplosionStep(path: BinaryTreePath, depth: Int): ExplosionStep? {
            return if (this is SFPair) {
                if (depth >= 4 && left is SFRegular && right is SFRegular)
                    return ExplosionStep(this, path)

                left.findExplosionStep(path / LEFT, depth + 1) ?: right.findExplosionStep(path / RIGHT, depth + 1)
            } else {
                null
            }
        }

        fun SFNumber.findSplitStep(path: BinaryTreePath): SplitStep? {
            return if (this is SFPair) {
                left.findSplitStep(path / LEFT) ?: right.findSplitStep(path / RIGHT)
            } else {
                check(this is SFRegular)

                if (value >= 10)
                    SplitStep(path)
                else
                    null
            }
        }

        return findExplosionStep(path, depth) ?: findSplitStep(path)
    }

    var isDone = false
    var res = this

    while (!isDone) {
        val step = res.reduceImpl(BinaryTreePath(emptyList()), 0)

        if (step != null) {
            when (step) {
                is ExplosionStep -> {
                    val (lValue, rValue) = step.pair.let { (lValue, rValue) ->
                        (lValue as SFRegular).value to (rValue as SFRegular).value
                    }

                    val left = res.findLeftOf(step.path)
                    if (left != null) res = res.replace(left) { SFRegular((it as SFRegular).value + lValue) }

                    val right = res.findRightOf(step.path)
                    if (right != null) res = res.replace(right) { SFRegular((it as SFRegular).value + rValue) }

                    res = res.replace(step.path) { SFRegular(0) }
                }
                is SplitStep -> {
                    res = res.replace(step.path) {
                        val value = (it as SFRegular).value

                        SFPair(
                            left = SFRegular(floor(value.toFloat() / 2).toInt()),
                            right = SFRegular(ceil(value.toFloat() / 2).toInt())
                        )
                    }
                }
            }

            continue
        }

        isDone = true
    }

    return res
}

private sealed interface SFNumber {

    val magnitude: Long

    operator fun plus(other: SFNumber) =
        SFPair(this, other).reduce()

}

private data class SFPair(
    val left: SFNumber,
    val right: SFNumber
) : SFNumber {

    override val magnitude: Long
        get() = (3 * left.magnitude) + (2 * right.magnitude)

    override fun toString() =
        "[$left, $right]"
}

private data class SFRegular(
    val value: Int
) : SFNumber {

    override val magnitude: Long
        get() = value.toLong()

    override fun toString() =
        value.toString()

}