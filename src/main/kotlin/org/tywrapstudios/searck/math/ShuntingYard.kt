package org.tywrapstudios.searck.math

import org.slf4j.LoggerFactory
import org.tywrapstudios.searck.math.exception.InvalidMathException
import org.tywrapstudios.searck.math.ruleset.Associativity
import org.tywrapstudios.searck.math.ruleset.IOperator
import org.tywrapstudios.searck.math.ruleset.Operator
import java.util.Stack

object ShuntingYard {
    val OPS = mutableMapOf<String, IOperator>()
    val FUNCTIONS = mutableMapOf<String, IOperator>()
    val FUNCTION_CHARS = mutableSetOf<Char>()
    private val LOGGER = LoggerFactory.getLogger(javaClass)

    init {
        for (operator in Operator.entries) {
            OPS[operator.symbol] = operator
            if (operator.symbol.length > 1) {
                FUNCTIONS[operator.symbol] = operator
                for (c in operator.symbol) {
                    FUNCTION_CHARS.add(c)
                }
            }
        }
    }

    /**
     * Parses an infix calculation into separate equation parts (tokens).
     * @see execute
     * @param equation the calculation to perform this parse on
     * @throws UnsupportedOperationException if the equation contains a supposed function that is not implemented (yet)
     * @throws InvalidMathException if your calculation contains characters that aren't allowed in this implementation
     */
    fun prepare(equation: String): List<String> {
        val equation = equation
            .trim()
            .replace(" ", "")
            .replace(",", ".")
        val cache = Stack<String>()
        val tokens = mutableListOf<String>()
        val function = StringBuilder()

        LOGGER.debug("[Prepare] checking: $equation")

        val chars = equation.toCharArray().toList()
        var i = 0
        while (i < chars.size) {
            val c = chars[i]
            val s = c.toString()
            var safe = false
            LOGGER.debug("[Prepare] checking char: $s")
            if (s.matches("\\d|[.]".toRegex())) {
                cache.push(s)
                safe = true
                LOGGER.debug("[Prepare] pushed to cache: $s")
            } else if (s.matches("[-+*/%^()]".toRegex())) {
                val cacheToken = processCache(cache, tokens)
                LOGGER.debug("[Prepare] operator found, added cached token: $cacheToken")
                tokens.add(s)
                safe = true
                LOGGER.debug("[Prepare] added token: $s")
            } else if(s.matches("[a-z]".toRegex())) {
                LOGGER.debug("[Prepare] possible function found")
                for (i2 in i..chars.lastIndex) {
                    val possibleChar = chars[i2]
                    if (FUNCTION_CHARS.contains(possibleChar)) {
                        function.append(possibleChar)
                        LOGGER.debug("[Prepare] function loop, add $possibleChar. New: {}", function)
                    } else {
                        LOGGER.debug("[Prepare] function loop, break on $possibleChar. Final: {}", function)
                        break
                    }
                }

                if (FUNCTIONS.contains(function.toString())) {
                    LOGGER.debug("[Prepare] function found: {}", function)
                    tokens.add(function.toString())
                    i += function.length - 1
                    safe = true
                } else {
                    LOGGER.debug("[Prepare] function not found: {}", function)
                    throw UnsupportedOperationException("[Prepare] this function is not available: $function")
                }
            }
            LOGGER.debug("[Prepare] finalizing for char: $c")
            function.clear()
            if (!safe) {
                cache.clear()
                throw InvalidMathException("${equation.replaceRange(i..i+1, ">>$c<<")} (at character $i)")
            }
            i++
        }
        LOGGER.debug("[Prepare] finalizing for equation: {}", equation)
        processCache(cache, tokens)
        LOGGER.debug("[Prepare] final: {}", tokens)
        return tokens
    }

    private fun processCache(cache: Stack<String>, tokens: MutableList<String>): String {
        val final = StringBuilder()
        LOGGER.debug("[Process Cache] started: {}", cache)
        while (cache.isNotEmpty()) {
            val first = cache.removeFirst()
            final.append(first)
            LOGGER.debug("[Process Cache] grabbed: {}", first)
            LOGGER.debug("[Process Cache] new cache: {}", cache)
            LOGGER.debug("[Process Cache] process loop: {}", final)
        }
        LOGGER.debug("[Process Cache] final: {}", final)
        if (final.isNotEmpty()) {
            tokens.add(final.toString())
        } else {
            LOGGER.debug("[Process Cache] empty, skipping")
        }
        return final.toString()
    }

    /**
     * Parses and returns a List from `infix`notation to `postfix`-notation
     *
     * You input a [List] of [String]s that contains every part of the infix notation.
     * ```
     *     // e.g. "6 + 4 / (6 - 4)"
     *     List<String> tokens = List.of("6", "+", "4", "/", "(", "6", "-", "4", ")");
     * ```
     * @see prepare
     * @param tokens a list containing every part of an `infix`-notation calculation
     */
    fun execute(tokens: List<String>): List<String> {
        val output = mutableListOf<String>()
        val stack = Stack<String>()

        // Read the tokens one at a time
        var i = 0
        while (i < tokens.size) {
            val token = tokens[i]
            if (OPS.containsKey(token)) {
                // Token is an operator
                val op = OPS[token]!!

                // We check if the token is a subtraction operator
                // If:
                // - It's the first token
                // - There's another operator or open parenthesis in front of it
                if (op == Operator.SUBTRACTION && (i == 0 || OPS.containsKey(tokens[i-1]) || tokens[i-1] == "(")) {
                    // It's a negative number, displayed in postfix as:
                    // 0 n -
                    // Equivalent to: 0 - n <=> -n
                    output.add("0")
                    output.add(tokens[i+1])
                    output.add("-")
                    // Skip the next token, as we already added it in the output
                    i += 2
                    continue
                }

                if (op.singleOperand) {
                    // Single operand operators can be pushed immediately since they need only one number
                    stack.push(token)
                    i++
                    continue
                }

                while (stack.isNotEmpty() && OPS.containsKey(stack.peek())) {
                    // While there is an operator (y) at the top of the operators stack and
                    // either (x) is left-associative and its precedence is less or equal to
                    // that of (y), or (x) is right-associative and its precedence
                    // is less than (y)
                    val topOp = OPS[stack.peek()]!!
                    if ((op.associativity == Associativity.LEFT && op <= topOp) ||
                        (op.associativity == Associativity.RIGHT && op < topOp)) {
                        // Pop (y) from the stack
                        // Add (y) output buffer
                        output.add(stack.pop())
                        continue
                    }
                    break
                }
                // Push the new operator
                stack.push(token)


            } else if (token == "(") {
                // If the token is an open parenthesis, push
                stack.push(token)
            } else if (token == ")") {
                // If the token is a closed parenthesis
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    // Until the top token (from the stack) is open parenthesis, pop from
                    // the stack to the output buffer
                    output.add(stack.pop())
                }
                // Also pop the open parenthesis but don't include it in the output buffer
                stack.pop()
            } else {
                // Otherwise, simply add the token to the output as it's (likely) a number
                output.add(token)
            }

            // Increase the index for the while loop
            i++
        }

        while (!stack.isEmpty()) {
            // While there are still operator tokens in the stack, pop them to output
            output.add(stack.pop())
        }

        return output
    }
}