package com.example.core.calculation.parser

import com.example.core.calculation.model.Fraction

/**
 * Lexer for mathematical expressions.
 */
class Lexer(private val input: String) {
    private var index = 0

    fun tokenize(): List<Token> {
        val tokens = mutableListOf<Token>()
        while (index < input.length) {
            val c = input[index]
            when {
                c.isWhitespace() -> index++
                c.isDigit() || (c == '.' && peek()?.isDigit() == true) -> {
                    tokens.add(readNumber())
                }
                c.isLetter() || c == '_' -> {
                    tokens.add(readIdentifier())
                }
                c == '+' -> { tokens.add(Token(TokenType.PLUS, "+", index++)) }
                c == '-' -> { tokens.add(Token(TokenType.MINUS, "-", index++)) }
                c == '*' -> { tokens.add(Token(TokenType.STAR, "*", index++)) }
                c == '/' -> { tokens.add(Token(TokenType.SLASH, "/", index++)) }
                c == '^' -> { tokens.add(Token(TokenType.CARET, "^", index++)) }
                c == '(' -> { tokens.add(Token(TokenType.LPAREN, "(", index++)) }
                c == ')' -> { tokens.add(Token(TokenType.RPAREN, ")", index++)) }
                c == ',' -> { tokens.add(Token(TokenType.COMMA, ",", index++)) }
                c == '=' -> { tokens.add(Token(TokenType.EQUALS, "=", index++)) }
                else -> {
                    // Skip unsupported or punctuation character
                    index++
                }
            }
        }
        tokens.add(Token(TokenType.EOF, "", index))
        return tokens
    }

    private fun peek(): Char? = if (index + 1 < input.length) input[index + 1] else null

    private fun readNumber(): Token {
        val start = index
        var hasDot = false
        while (index < input.length) {
            val c = input[index]
            if (c.isDigit()) {
                index++
            } else if (c == '.' && !hasDot) {
                hasDot = true
                index++
            } else {
                break
            }
        }
        val text = input.substring(start, index)
        return Token(TokenType.NUMBER, text, start)
    }

    private fun readIdentifier(): Token {
        val start = index
        while (index < input.length && (input[index].isLetterOrDigit() || input[index] == '_')) {
            index++
        }
        val text = input.substring(start, index)
        return Token(TokenType.IDENTIFIER, text, start)
    }
}

/**
 * Recursive Descent Expression Parser with proper operator precedence.
 */
class ExpressionParser(private val tokens: List<Token>) {
    private var current = 0

    constructor(expression: String) : this(Lexer(expression).tokenize())

    fun parse(): ExpressionNode {
        val expr = parseEquation()
        if (!isAtEnd()) {
            throw IllegalArgumentException("Unexpected token after expression: '${peek().text}' at position ${peek().position}")
        }
        return expr
    }

    // equation: expression ('=' expression)?
    private fun parseEquation(): ExpressionNode {
        val left = parseAdditive()
        if (match(TokenType.EQUALS)) {
            val right = parseAdditive()
            return ExpressionNode.EquationNode(left, right)
        }
        return left
    }

    // additive: multiplicative (('+' | '-') multiplicative)*
    private fun parseAdditive(): ExpressionNode {
        var node = parseMultiplicative()
        while (match(TokenType.PLUS, TokenType.MINUS)) {
            val op = if (previous().type == TokenType.PLUS) BinaryOperator.ADD else BinaryOperator.SUBTRACT
            val right = parseMultiplicative()
            node = ExpressionNode.BinaryOpNode(node, op, right)
        }
        return node
    }

    // multiplicative: exponential (('*' | '/') exponential)*
    private fun parseMultiplicative(): ExpressionNode {
        var node = parseExponential()
        while (match(TokenType.STAR, TokenType.SLASH)) {
            val op = if (previous().type == TokenType.STAR) BinaryOperator.MULTIPLY else BinaryOperator.DIVIDE
            val right = parseExponential()
            node = ExpressionNode.BinaryOpNode(node, op, right)
        }
        return node
    }

    // exponential: unary ('^' exponential)?  (right-associative)
    private fun parseExponential(): ExpressionNode {
        val node = parseUnary()
        if (match(TokenType.CARET)) {
            val right = parseExponential()
            return ExpressionNode.BinaryOpNode(node, BinaryOperator.POWER, right)
        }
        return node
    }

    // unary: ('+' | '-') unary | primary
    private fun parseUnary(): ExpressionNode {
        if (match(TokenType.PLUS)) {
            return ExpressionNode.UnaryOpNode(UnaryOperator.PLUS, parseUnary())
        }
        if (match(TokenType.MINUS)) {
            return ExpressionNode.UnaryOpNode(UnaryOperator.NEGATE, parseUnary())
        }
        return parsePrimary()
    }

    // primary: NUMBER | IDENTIFIER ('(' args ')')? | '(' expression ')'
    private fun parsePrimary(): ExpressionNode {
        if (match(TokenType.NUMBER)) {
            val text = previous().text
            val doubleVal = text.toDoubleOrNull() ?: 0.0
            val fraction = if (!text.contains('.')) {
                Fraction(text.toLong(), 1L)
            } else {
                null
            }
            return ExpressionNode.NumberNode(doubleVal, fraction, text)
        }

        if (match(TokenType.IDENTIFIER)) {
            val name = previous().text
            // Check if it is a function call like sin(30)
            if (match(TokenType.LPAREN)) {
                val args = mutableListOf<ExpressionNode>()
                if (!check(TokenType.RPAREN)) {
                    do {
                        args.add(parseAdditive())
                    } while (match(TokenType.COMMA))
                }
                consume(TokenType.RPAREN, "Expected ')' after function arguments.")
                return ExpressionNode.FunctionCallNode(name, args)
            }
            return ExpressionNode.VariableNode(name)
        }

        if (match(TokenType.LPAREN)) {
            val node = parseAdditive()
            consume(TokenType.RPAREN, "Expected ')' after parenthesized expression.")
            return node
        }

        throw IllegalArgumentException("Expected expression at position ${peek().position}, found '${peek().text}'")
    }

    private fun match(vararg types: TokenType): Boolean {
        for (type in types) {
            if (check(type)) {
                advance()
                return true
            }
        }
        return false
    }

    private fun consume(type: TokenType, message: String): Token {
        if (check(type)) return advance()
        throw IllegalArgumentException("$message Found '${peek().text}' at position ${peek().position}")
    }

    private fun check(type: TokenType): Boolean {
        if (isAtEnd()) return type == TokenType.EOF
        return peek().type == type
    }

    private fun advance(): Token {
        if (!isAtEnd()) current++
        return previous()
    }

    private fun isAtEnd(): Boolean = peek().type == TokenType.EOF
    private fun peek(): Token = tokens[current]
    private fun previous(): Token = tokens[current - 1]
}
