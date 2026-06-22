package org.tywrapstudios.searck.math.ruleset

enum class Associativity {
    LEFT,
    RIGHT
}

typealias Operation = (left: Double, right: Double) -> Double

interface IOperator : Comparable<IOperator> {
    val associativity: Associativity
    val precedence: Int
    val symbol: String
    val singleOperand: Boolean
    val operation: Operation

    override fun compareTo(other: IOperator): Int {
        return this.precedence - other.precedence
    }
}