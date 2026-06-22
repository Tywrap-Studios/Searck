package org.tywrapstudios.searck.math.exception

class InvalidMathException(invalid: String) : Exception("This math operation is invalid: $invalid")