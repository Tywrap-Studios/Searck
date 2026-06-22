package org.tywrapstudios.searck.math.ruleset

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sqrt

enum class Operator(
    override val symbol: String,
    override val associativity: Associativity,
    override val precedence: Int,
    override val singleOperand: Boolean,
    override val operation: Operation
) : IOperator {
    ADDITION("+", Associativity.LEFT, 0, Double::plus),
    SUBTRACTION("-", Associativity.LEFT, 0, Double::minus),
    DIVISION("/", Associativity.LEFT, 5, Double::div),
    MULTIPLICATION("*", Associativity.LEFT, 5, Double::times),
    MODULUS("%", Associativity.LEFT, 5, Double::mod),
    POWER("^", Associativity.RIGHT, 10, Double::pow),
    SQRT("sqrt", Associativity.RIGHT, 10, true, { _, right ->
        sqrt(right)
    }),
    CEIL("ceil", Associativity.RIGHT, 10, true, { _, right ->
        ceil(right)
    }),
    FLOOR("floor", Associativity.RIGHT, 10, true, { _, right ->
        floor(right)
    }),
    ROUND("round", Associativity.RIGHT, 10, true, { _, right ->
        round(right)
    });

    constructor(
        symbol: String,
        associativity: Associativity,
        precedence: Int,
        operation: Operation
    ) : this(symbol, associativity, precedence, false, operation)
}