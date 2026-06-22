package org.tywrapstudios.searck.math

import org.slf4j.LoggerFactory
import java.util.*

object StringCalculator {
    private val LOGGER = LoggerFactory.getLogger(javaClass)

    fun calculate(calculation: String): Double {
        if (calculation.isEmpty()) return Double.NaN

        try {
            // Prepare and get tokens
            val tokens = ShuntingYard.prepare(calculation)
            val postfix = ShuntingYard.execute(tokens)

            val doubles = Stack<Double>()
            for (token in postfix) {
                LOGGER.debug("Checking Token: $token")
                if (!ShuntingYard.OPS.contains(token)) {
                    doubles.push(token.toDouble())
                    LOGGER.debug("Pushed: $token")
                } else {
                    // If it is, we get it from the OPS list
                    val op = ShuntingYard.OPS[token]!!
                    // We get the right associative digit, by popping it
                    val right: Double = doubles.pop()
                    // We try to get the left associative digit too, by popping it.
                    // If it doesn't exist if the operator operates on a single operand
                    // we can always assign it to right here if the check is false,
                    // as Single Operand Operators have Right Associativity
                    val left = if (!op.singleOperand) doubles.pop() else right

                    // We calculate and push the outcome
                    val result: Double = op.operation(left, right)
                    LOGGER.debug("Result: $result")
                    doubles.push(result)
                }
            }
            return doubles.pop()
        } catch (e: Exception) {
            LOGGER.debug("Error calculating $calculation", e)
            return Double.NaN
        }
    }
}