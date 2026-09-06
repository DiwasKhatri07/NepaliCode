package com.nepalicode.dev.nepalilang.core

sealed interface AstNode

sealed interface Expr : AstNode {
    val token: Token
}

data class LiteralExpr(val value: Any?, override val token: Token) : Expr
data class IdentifierExpr(val name: String, override val token: Token) : Expr
data class BinaryExpr(val left: Expr, val op: TokenType, val right: Expr, override val token: Token) : Expr
data class UnaryExpr(val op: TokenType, val right: Expr, override val token: Token) : Expr
data class CallExpr(val callee: Expr, val arguments: List<Expr>, override val token: Token) : Expr
data class MemberAccessExpr(val target: Expr, val member: String, override val token: Token) : Expr
data class IndexExpr(val target: Expr, val index: Expr, override val token: Token) : Expr
data class ListLiteralExpr(val elements: List<Expr>, override val token: Token) : Expr
data class MapLiteralExpr(val entries: List<Pair<Expr, Expr>>, override val token: Token) : Expr

sealed interface Stmt : AstNode

data class ExprStmt(val expr: Expr) : Stmt
data class AssignStmt(
    val target: Expr, // IdentifierExpr, IndexExpr, or MemberAccessExpr
    val value: Expr,
    val token: Token
) : Stmt

data class ElifBranch(val condition: Expr, val body: List<Stmt>)

data class IfStmt(
    val condition: Expr,
    val thenBranch: List<Stmt>,
    val elifBranches: List<ElifBranch> = emptyList(),
    val elseBranch: List<Stmt>? = null
) : Stmt

data class WhileStmt(
    val condition: Expr,
    val body: List<Stmt>
) : Stmt

data class ForStmt(
    val variable: String,
    val iterable: Expr,
    val body: List<Stmt>
) : Stmt

data class FunctionDefStmt(
    val name: String,
    val params: List<String>,
    val body: List<Stmt>
) : Stmt

data class ReturnStmt(
    val value: Expr?,
    val token: Token
) : Stmt

data class ImportStmt(
    val module: String,
    val alias: String? = null,
    val fromModule: String? = null,
    val symbols: List<String> = emptyList()
) : Stmt

data class TryExceptStmt(
    val tryBlock: List<Stmt>,
    val exceptBlock: List<Stmt>,
    val exceptVar: String? = null,
    val finallyBlock: List<Stmt>? = null
) : Stmt

data class AssertStmt(
    val condition: Expr,
    val message: Expr? = null,
    val token: Token
) : Stmt

data class BreakStmt(val token: Token) : Stmt
data class ContinueStmt(val token: Token) : Stmt

data class ClassDefStmt(
    val name: String,
    val methods: List<FunctionDefStmt>,
    val token: Token
) : Stmt

data class Program(val statements: List<Stmt>) : AstNode
