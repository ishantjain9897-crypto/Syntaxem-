package com.example.core.calculation.parser

import com.example.core.calculation.model.Fraction

enum class TokenType {
    NUMBER,
    IDENTIFIER,
    PLUS,
    MINUS,
    STAR,
    SLASH,
    CARET,
    LPAREN,
    RPAREN,
    COMMA,
    EQUALS,
    EOF
}

data class Token(
    val type: TokenType,
    val text: String,
    val position: Int
)

enum class BinaryOperator {
    ADD,
    SUBTRACT,
    MULTIPLY,
    DIVIDE,
    POWER
}

enum class UnaryOperator {
    PLUS,
    NEGATE
}

/**
 * Abstract Syntax Tree (AST) node hierarchy for parsed mathematical expressions.
 */
sealed class ExpressionNode {
    data class NumberNode(
        val value: Double,
        val fraction: Fraction? = null,
        val rawText: String = value.toString()
    ) : ExpressionNode()

    data class VariableNode(
        val name: String
    ) : ExpressionNode()

    data class BinaryOpNode(
        val left: ExpressionNode,
        val operator: BinaryOperator,
        val right: ExpressionNode
    ) : ExpressionNode()

    data class UnaryOpNode(
        val operator: UnaryOperator,
        val operand: ExpressionNode
    ) : ExpressionNode()

    data class FunctionCallNode(
        val functionName: String,
        val arguments: List<ExpressionNode>
    ) : ExpressionNode()

    data class EquationNode(
        val left: ExpressionNode,
        val right: ExpressionNode
    ) : ExpressionNode()
}
