package org.tywrapstudios.searck.math.ruleset

import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.asinh
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

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
    }),
    TAN("tan", Associativity.RIGHT, 15, true, {_, right ->
        tan(right)
    }),
    ATAN("atan", Associativity.RIGHT, 15, true, {_, right ->
        atan(right)
    }),
    ATAN2("atan2", Associativity.LEFT, 15, { left, right ->
        atan2(left, right)
    }),
    COS("cos", Associativity.RIGHT, 15, true, {_, right ->
        cos(right)
    }),
    ACOS("acos", Associativity.RIGHT, 15, true, {_, right ->
        acos(right)
    }),
    SIN("sin", Associativity.RIGHT, 15, true, {_, right ->
        sin(right)
    }),
    ASIN("asin", Associativity.RIGHT, 15, true, {_, right ->
        asin(right)
    }),
    ;

    constructor(
        symbol: String,
        associativity: Associativity,
        precedence: Int,
        operation: Operation
    ) : this(symbol, associativity, precedence, false, operation)
}