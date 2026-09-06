package com.nepalicode.dev.nepalilang.core

import android.content.Context
import kotlinx.coroutines.CancellationException

class NepaliReturnException(val value: NepaliValue) : Exception()
class NepaliBreakException : Exception()
class NepaliContinueException : Exception()

class NepaliRuntimeException(
    val fileName: String = "main.np",
    val line: Int = 1,
    val column: Int = 1,
    override val message: String,
    val suggestion: String = "Check syntax and variable values."
) : Exception(message) {
    fun formatFormattedError(): String {
        return buildString {
            appendLine("NepaliLang Error 🇳🇵")
            appendLine("-----------------")
            appendLine("File: $fileName")
            appendLine("Line: $line")
            appendLine("Column: $column")
            appendLine()
            appendLine(message)
            appendLine()
            appendLine("Suggestion:")
            appendLine(suggestion)
        }
    }
}

class NepaliInterpreter(
    private val context: Context,
    private val callbacks: NepaliIdeCallbacks,
    private val fileName: String = "main.np"
) {
    val globalEnv = NepaliEnvironment()
    private val stdLib = NepaliStdLib(context, callbacks)

    init {
        registerBuiltins(globalEnv)
        stdLib.registerStandardModules(globalEnv)
    }

    suspend fun execute(program: Program, env: NepaliEnvironment = globalEnv) {
        for (stmt in program.statements) {
            executeStatement(stmt, env)
        }
    }

    private fun registerBuiltins(env: NepaliEnvironment) {
        // print / dekha
        val printBuiltin = NepaliBuiltin("print") { args, _ ->
            val text = args.joinToString(" ") { it.toDisplayString() }
            callbacks.onPrint(text)
            NepaliNull
        }
        env.define("print", printBuiltin)
        env.define("dekha", printBuiltin)

        // input / leu / sodha
        val inputBuiltin = NepaliBuiltin("input") { args, _ ->
            val prompt = args.getOrNull(0)?.toDisplayString() ?: ""
            val ans = callbacks.onInput(prompt)
            NepaliString(ans)
        }
        env.define("input", inputBuiltin)
        env.define("leu", inputBuiltin)
        env.define("sodha", inputBuiltin)

        // len / lambai
        val lenBuiltin = NepaliBuiltin("len") { args, _ ->
            val arg = args.getOrNull(0) ?: NepaliNull
            val length = when (arg) {
                is NepaliString -> arg.value.length
                is NepaliList -> arg.elements.size
                is NepaliMap -> arg.entries.size
                else -> throw NepaliRuntimeException(fileName, 1, 1, "len() requires string, list, or map", "Check argument passed to len()")
            }
            NepaliNumber(length.toDouble(), isInt = true)
        }
        env.define("len", lenBuiltin)
        env.define("lambai", lenBuiltin)

        // yo() - Nepali convenience constructor
        val yoBuiltin = NepaliBuiltin("yo") { args, _ ->
            args.getOrNull(0) ?: NepaliNull
        }
        env.define("yo", yoBuiltin)

        // range / daura
        val rangeBuiltin = NepaliBuiltin("range") { args, _ ->
            val (start, stop, step) = when (args.size) {
                1 -> Triple(0L, (args[0] as? NepaliNumber)?.toLong() ?: 0L, 1L)
                2 -> Triple((args[0] as? NepaliNumber)?.toLong() ?: 0L, (args[1] as? NepaliNumber)?.toLong() ?: 0L, 1L)
                3 -> Triple((args[0] as? NepaliNumber)?.toLong() ?: 0L, (args[1] as? NepaliNumber)?.toLong() ?: 0L, (args[2] as? NepaliNumber)?.toLong() ?: 1L)
                else -> Triple(0L, 0L, 1L)
            }
            val list = mutableListOf<NepaliValue>()
            var i = start
            if (step > 0) {
                while (i < stop) {
                    list.add(NepaliNumber(i.toDouble(), isInt = true))
                    i += step
                }
            } else if (step < 0) {
                while (i > stop) {
                    list.add(NepaliNumber(i.toDouble(), isInt = true))
                    i += step
                }
            }
            NepaliList(list)
        }
        env.define("range", rangeBuiltin)
        env.define("daura", rangeBuiltin)

        // type / prakaar
        val typeBuiltin = NepaliBuiltin("type") { args, _ ->
            NepaliString(args.getOrNull(0)?.typeName() ?: "null")
        }
        env.define("type", typeBuiltin)
        env.define("prakaar", typeBuiltin)
        env.define("prakar", typeBuiltin)

        // type conversion builtins
        val strBuiltin = NepaliBuiltin("str") { args, _ -> NepaliString(args.getOrNull(0)?.toDisplayString() ?: "") }
        env.define("str", strBuiltin)
        env.define("shabda", strBuiltin)

        val numberBuiltin = NepaliBuiltin("number") { args, _ ->
            val v = args.getOrNull(0)?.toDisplayString()?.toLongOrNull() ?: 0L
            NepaliNumber(v.toDouble(), isInt = true)
        }
        env.define("number", numberBuiltin)
        env.define("ank", numberBuiltin)

        val decimalBuiltin = NepaliBuiltin("decimal") { args, _ ->
            val v = args.getOrNull(0)?.toDisplayString()?.toDoubleOrNull() ?: 0.0
            NepaliNumber(v, isInt = false)
        }
        env.define("decimal", decimalBuiltin)
        env.define("dashamlav", decimalBuiltin)

        val boolBuiltin = NepaliBuiltin("bool") { args, _ -> NepaliBool(args.getOrNull(0)?.isTruthy() ?: false) }
        env.define("bool", boolBuiltin)
        env.define("satya_jhut", boolBuiltin)

        val listBuiltin = NepaliBuiltin("list") { args, _ ->
            when (val a = args.getOrNull(0)) {
                is NepaliList -> NepaliList(a.elements.toMutableList())
                is NepaliString -> NepaliList(a.value.map { NepaliString(it.toString()) }.toMutableList())
                else -> NepaliList()
            }
        }
        env.define("list", listBuiltin)
        env.define("suchi", listBuiltin)

        val mapBuiltin = NepaliBuiltin("map") { args, _ ->
            when (val a = args.getOrNull(0)) {
                is NepaliMap -> NepaliMap(a.entries.toMutableMap())
                else -> NepaliMap()
            }
        }
        env.define("map", mapBuiltin)
        env.define("kosh", mapBuiltin)

        // list helpers: ulta (reversed), kram (sorted), thapa (append), nikala (pop)
        env.define("ulta", NepaliBuiltin("ulta") { args, _ ->
            val list = (args.getOrNull(0) as? NepaliList)?.elements?.reversed() ?: emptyList()
            NepaliList(list.toMutableList())
        })
        env.define("kram", NepaliBuiltin("kram") { args, _ ->
            val list = (args.getOrNull(0) as? NepaliList)?.elements ?: emptyList()
            val sorted = list.sortedBy { it.toDisplayString() }
            NepaliList(sorted.toMutableList())
        })

        // math builtins: sum, min, max, abs, round, sorted
        val sumBuiltin = NepaliBuiltin("sum") { args, _ ->
            val list = (args.getOrNull(0) as? NepaliList)?.elements ?: emptyList()
            var sum = 0.0
            var allInt = true
            for (item in list) {
                if (item is NepaliNumber) {
                    sum += item.value
                    if (!item.isInt) allInt = false
                }
            }
            NepaliNumber(sum, isInt = allInt)
        }
        env.define("sum", sumBuiltin)
        env.define("jod", sumBuiltin)

        env.define("min", NepaliBuiltin("min") { args, _ ->
            val list = (args.getOrNull(0) as? NepaliList)?.elements ?: args
            val minVal = list.filterIsInstance<NepaliNumber>().minByOrNull { it.value }
            minVal ?: NepaliNull
        })
        env.define("max", NepaliBuiltin("max") { args, _ ->
            val list = (args.getOrNull(0) as? NepaliList)?.elements ?: args
            val maxVal = list.filterIsInstance<NepaliNumber>().maxByOrNull { it.value }
            maxVal ?: NepaliNull
        })
        env.define("abs", NepaliBuiltin("abs") { args, _ ->
            val n = (args.getOrNull(0) as? NepaliNumber)?.value ?: 0.0
            NepaliNumber(kotlin.math.abs(n))
        })
        env.define("round", NepaliBuiltin("round") { args, _ ->
            val n = (args.getOrNull(0) as? NepaliNumber)?.value ?: 0.0
            NepaliNumber(kotlin.math.round(n), isInt = true)
        })
        env.define("sorted", NepaliBuiltin("sorted") { args, _ ->
            val list = (args.getOrNull(0) as? NepaliList)?.elements ?: mutableListOf()
            val sorted = list.sortedBy { it.toDisplayString() }.toMutableList()
            NepaliList(sorted)
        })
    }

    private suspend fun executeStatement(stmt: Stmt, env: NepaliEnvironment) {
        when (stmt) {
            is ExprStmt -> {
                evaluateExpr(stmt.expr, env)
            }
            is AssignStmt -> {
                val value = evaluateExpr(stmt.value, env)
                when (val target = stmt.target) {
                    is IdentifierExpr -> {
                        env.assign(target.name, value)
                    }
                    is IndexExpr -> {
                        val obj = evaluateExpr(target.target, env)
                        val idx = evaluateExpr(target.index, env)
                        when (obj) {
                            is NepaliList -> {
                                val i = (idx as? NepaliNumber)?.toLong()?.toInt()
                                    ?: throw NepaliRuntimeException(fileName, target.token.line, target.token.column, "List index must be integer", "Provide an integer index")
                                if (i in 0 until obj.elements.size) {
                                    obj.elements[i] = value
                                } else {
                                    throw NepaliRuntimeException(fileName, target.token.line, target.token.column, "Index $i out of range for list of size ${obj.elements.size}", "Check bounds of list")
                                }
                            }
                            is NepaliMap -> {
                                obj.entries[idx.toDisplayString()] = value
                            }
                            else -> throw NepaliRuntimeException(fileName, target.token.line, target.token.column, "Cannot assign index to type ${obj.typeName()}", "Ensure target is a list or map")
                        }
                    }
                    is MemberAccessExpr -> {
                        val obj = evaluateExpr(target.target, env)
                        when (obj) {
                            is NepaliObject -> obj.fields[target.member] = value
                            is NepaliModule -> obj.members[target.member] = value
                            else -> throw NepaliRuntimeException(fileName, target.token.line, target.token.column, "Cannot assign member '${target.member}' on ${obj.typeName()}", "Verify object field")
                        }
                    }
                    else -> throw NepaliRuntimeException(fileName, stmt.token.line, stmt.token.column, "Invalid assignment target", "Assign to variable, index, or property")
                }
            }
            is IfStmt -> {
                val cond = evaluateExpr(stmt.condition, env).isTruthy()
                if (cond) {
                    val blockEnv = NepaliEnvironment(env)
                    for (s in stmt.thenBranch) executeStatement(s, blockEnv)
                } else {
                    var matched = false
                    for (elif in stmt.elifBranches) {
                        if (evaluateExpr(elif.condition, env).isTruthy()) {
                            val elifEnv = NepaliEnvironment(env)
                            for (s in elif.body) executeStatement(s, elifEnv)
                            matched = true
                            break
                        }
                    }
                    if (!matched && stmt.elseBranch != null) {
                        val elseEnv = NepaliEnvironment(env)
                        for (s in stmt.elseBranch) executeStatement(s, elseEnv)
                    }
                }
            }
            is BreakStmt -> throw NepaliBreakException()
            is ContinueStmt -> throw NepaliContinueException()
            is WhileStmt -> {
                while (evaluateExpr(stmt.condition, env).isTruthy()) {
                    val loopEnv = NepaliEnvironment(env)
                    try {
                        for (s in stmt.body) executeStatement(s, loopEnv)
                    } catch (b: NepaliBreakException) {
                        break
                    } catch (c: NepaliContinueException) {
                        continue
                    }
                }
            }
            is ForStmt -> {
                val iterableVal = evaluateExpr(stmt.iterable, env)
                val items = when (iterableVal) {
                    is NepaliNumber -> (0 until iterableVal.toLong()).map { NepaliNumber(it.toDouble(), isInt = true) }
                    is NepaliList -> iterableVal.elements
                    is NepaliString -> iterableVal.value.map { NepaliString(it.toString()) }
                    is NepaliMap -> iterableVal.entries.keys.map { NepaliString(it) }
                    else -> throw NepaliRuntimeException(fileName, 1, 1, "Cannot iterate over ${iterableVal.typeName()}", "Ensure iterable is a list, number, string, or map")
                }
                for (item in items) {
                    val loopEnv = NepaliEnvironment(env)
                    loopEnv.define(stmt.variable, item)
                    try {
                        for (s in stmt.body) executeStatement(s, loopEnv)
                    } catch (b: NepaliBreakException) {
                        break
                    } catch (c: NepaliContinueException) {
                        continue
                    }
                }
            }
            is ClassDefStmt -> {
                val methodsMap = stmt.methods.associate { it.name to NepaliFunction(it.name, it.params, it.body, env) }
                val klass = NepaliClass(stmt.name, methodsMap)
                env.define(stmt.name, klass)
            }
            is FunctionDefStmt -> {
                val func = NepaliFunction(stmt.name, stmt.params, stmt.body, env)
                env.define(stmt.name, func)
            }
            is ReturnStmt -> {
                val retVal = stmt.value?.let { evaluateExpr(it, env) } ?: NepaliNull
                throw NepaliReturnException(retVal)
            }
            is ImportStmt -> {
                // Modules are already registered in global environment
                val mod = globalEnv.get(stmt.module) as? NepaliModule
                    ?: throw NepaliRuntimeException(fileName, 1, 1, "Module '${stmt.module}' not found", "Ensure module is available in NepaliLang stdlib (anurodh, web, browser, automation, ganit, etc.)")
                if (stmt.symbols.isNotEmpty()) {
                    for (sym in stmt.symbols) {
                        val member = mod.members[sym]
                            ?: throw NepaliRuntimeException(fileName, 1, 1, "Symbol '$sym' not found in module '${stmt.module}'", "Check module exports")
                        env.define(sym, member)
                    }
                } else if (stmt.alias != null) {
                    env.define(stmt.alias, mod)
                } else {
                    env.define(stmt.module, mod)
                }
            }
            is TryExceptStmt -> {
                try {
                    val tryEnv = NepaliEnvironment(env)
                    for (s in stmt.tryBlock) executeStatement(s, tryEnv)
                } catch (e: Exception) {
                    if (e is CancellationException || e is NepaliReturnException) throw e
                    val exceptEnv = NepaliEnvironment(env)
                    if (stmt.exceptVar != null) {
                        exceptEnv.define(stmt.exceptVar, NepaliString(e.message ?: "Error"))
                    }
                    for (s in stmt.exceptBlock) executeStatement(s, exceptEnv)
                } finally {
                    stmt.finallyBlock?.let {
                        val finEnv = NepaliEnvironment(env)
                        for (s in it) executeStatement(s, finEnv)
                    }
                }
            }
            is AssertStmt -> {
                val cond = evaluateExpr(stmt.condition, env).isTruthy()
                if (!cond) {
                    val msg = stmt.message?.let { evaluateExpr(it, env).toDisplayString() } ?: "Assertion failed"
                    throw NepaliRuntimeException(fileName, stmt.token.line, stmt.token.column, msg, "Assertion check was false")
                }
            }
        }
    }

    suspend fun evaluateExpr(expr: Expr, env: NepaliEnvironment): NepaliValue {
        return when (expr) {
            is LiteralExpr -> {
                when (val v = expr.value) {
                    null -> NepaliNull
                    is Boolean -> NepaliBool(v)
                    is Long -> NepaliNumber(v.toDouble(), isInt = true)
                    is Int -> NepaliNumber(v.toDouble(), isInt = true)
                    is Double -> NepaliNumber(v, isInt = (v % 1.0 == 0.0))
                    is String -> {
                        // Interpolate f-strings if contains {variable}
                        if (v.contains("{") && v.contains("}")) {
                            interpolateString(v, env)
                        } else {
                            NepaliString(v)
                        }
                    }
                    else -> NepaliString(v.toString())
                }
            }
            is IdentifierExpr -> {
                env.get(expr.name) ?: throw NepaliRuntimeException(
                    fileName,
                    expr.token.line,
                    expr.token.column,
                    "Undefined variable or name '${expr.name}'",
                    "Check spelling or define '${expr.name}' before use."
                )
            }
            is BinaryExpr -> {
                val left = evaluateExpr(expr.left, env)

                // Short-circuit logical operators
                if (expr.op == TokenType.OR || expr.op == TokenType.WA) {
                    return if (left.isTruthy()) left else evaluateExpr(expr.right, env)
                }
                if (expr.op == TokenType.AND || expr.op == TokenType.RA) {
                    return if (!left.isTruthy()) left else evaluateExpr(expr.right, env)
                }

                val right = evaluateExpr(expr.right, env)
                evaluateBinaryOp(left, expr.op, right, expr.token)
            }
            is UnaryExpr -> {
                val right = evaluateExpr(expr.right, env)
                when (expr.op) {
                    TokenType.NOT, TokenType.HOINA -> NepaliBool(!right.isTruthy())
                    TokenType.MINUS -> {
                        if (right is NepaliNumber) {
                            NepaliNumber(-right.value, isInt = right.isInt)
                        } else {
                            throw NepaliRuntimeException(fileName, expr.token.line, expr.token.column, "Unary '-' requires a number", "Check operand type")
                        }
                    }
                    TokenType.PLUS -> right
                    else -> right
                }
            }
            is CallExpr -> {
                val callee = evaluateExpr(expr.callee, env)
                val evaluatedArgs = expr.arguments.map { evaluateExpr(it, env) }

                when (callee) {
                    is NepaliClass -> {
                        val instance = NepaliObject(callee.name)
                        val initMethod = callee.methods["__init__"]
                        if (initMethod != null) {
                            val initEnv = NepaliEnvironment(initMethod.closure)
                            val selfParam = initMethod.params.getOrNull(0) ?: "self"
                            initEnv.define(selfParam, instance)
                            for (i in 1 until initMethod.params.size) {
                                val paramName = initMethod.params[i]
                                val argVal = evaluatedArgs.getOrElse(i - 1) { NepaliNull }
                                initEnv.define(paramName, argVal)
                            }
                            try {
                                for (s in initMethod.body) {
                                    executeStatement(s, initEnv)
                                }
                            } catch (ret: NepaliReturnException) {
                                // Handled
                            }
                        }
                        instance
                    }
                    is NepaliBuiltin -> callee.function(evaluatedArgs, env)
                    is NepaliFunction -> {
                        val funcEnv = NepaliEnvironment(callee.closure)
                        for (i in callee.params.indices) {
                            val paramName = callee.params[i]
                            val argVal = evaluatedArgs.getOrElse(i) { NepaliNull }
                            funcEnv.define(paramName, argVal)
                        }
                        try {
                            for (s in callee.body) {
                                executeStatement(s, funcEnv)
                            }
                            NepaliNull
                        } catch (ret: NepaliReturnException) {
                            ret.value
                        }
                    }
                    else -> throw NepaliRuntimeException(
                        fileName,
                        expr.token.line,
                        expr.token.column,
                        "Type '${callee.typeName()}' is not callable",
                        "Ensure callee is a function or method"
                    )
                }
            }
            is MemberAccessExpr -> {
                val target = evaluateExpr(expr.target, env)
                when (target) {
                    is NepaliModule -> target.members[expr.member]
                        ?: throw NepaliRuntimeException(fileName, expr.token.line, expr.token.column, "Module '${target.name}' has no member '${expr.member}'", "Check available functions")
                    is NepaliObject -> {
                        target.fields[expr.member]
                            ?: target.methods[expr.member]
                            ?: run {
                                val klass = (env.get(target.className) as? NepaliClass)
                                    ?: (globalEnv.get(target.className) as? NepaliClass)
                                val methodFn = klass?.methods?.get(expr.member)
                                if (methodFn != null) {
                                    NepaliBuiltin(expr.member) { args, _ ->
                                        val methodEnv = NepaliEnvironment(methodFn.closure)
                                        val selfParam = methodFn.params.getOrNull(0) ?: "self"
                                        methodEnv.define(selfParam, target)
                                        for (i in 1 until methodFn.params.size) {
                                            val pName = methodFn.params[i]
                                            val pVal = args.getOrElse(i - 1) { NepaliNull }
                                            methodEnv.define(pName, pVal)
                                        }
                                        try {
                                            for (s in methodFn.body) {
                                                executeStatement(s, methodEnv)
                                            }
                                            NepaliNull
                                        } catch (ret: NepaliReturnException) {
                                            ret.value
                                        }
                                    }
                                } else null
                            }
                            ?: throw NepaliRuntimeException(fileName, expr.token.line, expr.token.column, "Object '${target.className}' has no member '${expr.member}'", "Check field or method name")
                    }
                    is NepaliString -> getStringMethod(target, expr.member)
                    is NepaliList -> getListMethod(target, expr.member)
                    is NepaliMap -> getMapMethod(target, expr.member)
                    else -> throw NepaliRuntimeException(fileName, expr.token.line, expr.token.column, "Cannot access member '${expr.member}' on ${target.typeName()}", "Check object type")
                }
            }
            is IndexExpr -> {
                val target = evaluateExpr(expr.target, env)
                val idx = evaluateExpr(expr.index, env)
                when (target) {
                    is NepaliList -> {
                        val i = (idx as? NepaliNumber)?.toLong()?.toInt()
                            ?: throw NepaliRuntimeException(fileName, expr.token.line, expr.token.column, "List index must be integer", "Pass an integer index")
                        if (i in 0 until target.elements.size) {
                            target.elements[i]
                        } else {
                            throw NepaliRuntimeException(fileName, expr.token.line, expr.token.column, "Index $i out of bounds (size ${target.elements.size})", "Check list size")
                        }
                    }
                    is NepaliMap -> {
                        target.entries[idx.toDisplayString()] ?: NepaliNull
                    }
                    is NepaliString -> {
                        val i = (idx as? NepaliNumber)?.toLong()?.toInt()
                            ?: throw NepaliRuntimeException(fileName, expr.token.line, expr.token.column, "String index must be integer", "Pass integer index")
                        if (i in 0 until target.value.length) {
                            NepaliString(target.value[i].toString())
                        } else {
                            throw NepaliRuntimeException(fileName, expr.token.line, expr.token.column, "String index $i out of bounds", "Check string length")
                        }
                    }
                    else -> throw NepaliRuntimeException(fileName, expr.token.line, expr.token.column, "Cannot index into type ${target.typeName()}", "Ensure target is a list, map, or string")
                }
            }
            is ListLiteralExpr -> {
                val elements = expr.elements.map { evaluateExpr(it, env) }.toMutableList()
                NepaliList(elements)
            }
            is MapLiteralExpr -> {
                val map = mutableMapOf<String, NepaliValue>()
                for ((kExpr, vExpr) in expr.entries) {
                    val k = evaluateExpr(kExpr, env).toDisplayString()
                    val v = evaluateExpr(vExpr, env)
                    map[k] = v
                }
                NepaliMap(map)
            }
        }
    }

    private fun evaluateBinaryOp(left: NepaliValue, op: TokenType, right: NepaliValue, token: Token): NepaliValue {
        // String concatenation
        if (op == TokenType.PLUS && (left is NepaliString || right is NepaliString)) {
            return NepaliString(left.toDisplayString() + right.toDisplayString())
        }

        // List concatenation
        if (op == TokenType.PLUS && left is NepaliList && right is NepaliList) {
            val combined = (left.elements + right.elements).toMutableList()
            return NepaliList(combined)
        }

        // Numeric operations
        if (left is NepaliNumber && right is NepaliNumber) {
            val a = left.value
            val b = right.value
            val resultIsInt = left.isInt && right.isInt

            return when (op) {
                TokenType.PLUS -> NepaliNumber(a + b, isInt = resultIsInt)
                TokenType.MINUS -> NepaliNumber(a - b, isInt = resultIsInt)
                TokenType.STAR -> NepaliNumber(a * b, isInt = resultIsInt)
                TokenType.SLASH -> {
                    if (b == 0.0) throw NepaliRuntimeException(fileName, token.line, token.column, "Division by zero.", "Check the value used as the divisor.")
                    NepaliNumber(a / b)
                }
                TokenType.PERCENT -> {
                    if (b == 0.0) throw NepaliRuntimeException(fileName, token.line, token.column, "Modulo by zero.", "Check divisor.")
                    NepaliNumber(a % b, isInt = resultIsInt)
                }
                TokenType.POWER -> NepaliNumber(Math.pow(a, b))
                TokenType.LESS -> NepaliBool(a < b)
                TokenType.LESS_EQUAL -> NepaliBool(a <= b)
                TokenType.GREATER -> NepaliBool(a > b)
                TokenType.GREATER_EQUAL -> NepaliBool(a >= b)
                TokenType.EQUAL, TokenType.IS -> NepaliBool(a == b)
                TokenType.NOT_EQUAL -> NepaliBool(a != b)
                else -> NepaliNull
            }
        }

        // General equality
        return when (op) {
            TokenType.EQUAL, TokenType.IS -> NepaliBool(left == right || left.toDisplayString() == right.toDisplayString())
            TokenType.NOT_EQUAL -> NepaliBool(left != right && left.toDisplayString() != right.toDisplayString())
            TokenType.IN, TokenType.MA -> {
                when (right) {
                    is NepaliList -> NepaliBool(right.elements.any { it.toDisplayString() == left.toDisplayString() })
                    is NepaliString -> NepaliBool(right.value.contains(left.toDisplayString()))
                    is NepaliMap -> NepaliBool(right.entries.containsKey(left.toDisplayString()))
                    else -> NepaliBool(false)
                }
            }
            else -> throw NepaliRuntimeException(fileName, token.line, token.column, "Unsupported operation '${token.lexeme}' between ${left.typeName()} and ${right.typeName()}", "Check operand types")
        }
    }

    private fun interpolateString(template: String, env: NepaliEnvironment): NepaliString {
        val regex = Regex("\\{([^}]+)\\}")
        val result = regex.replace(template) { matchResult ->
            val exprText = matchResult.groups[1]?.value?.trim() ?: ""
            val v = env.get(exprText)
            v?.toDisplayString() ?: "{${exprText}}"
        }
        return NepaliString(result)
    }

    private fun getStringMethod(target: NepaliString, method: String): NepaliBuiltin {
        return when (method) {
            "upper" -> NepaliBuiltin("upper") { _, _ -> NepaliString(target.value.uppercase()) }
            "lower" -> NepaliBuiltin("lower") { _, _ -> NepaliString(target.value.lowercase()) }
            "trim" -> NepaliBuiltin("trim") { _, _ -> NepaliString(target.value.trim()) }
            "replace" -> NepaliBuiltin("replace") { args, _ ->
                val old = args.getOrNull(0)?.toDisplayString() ?: ""
                val rep = args.getOrNull(1)?.toDisplayString() ?: ""
                NepaliString(target.value.replace(old, rep))
            }
            "contains" -> NepaliBuiltin("contains") { args, _ ->
                val s = args.getOrNull(0)?.toDisplayString() ?: ""
                NepaliBool(target.value.contains(s))
            }
            "starts_with" -> NepaliBuiltin("starts_with") { args, _ ->
                val s = args.getOrNull(0)?.toDisplayString() ?: ""
                NepaliBool(target.value.startsWith(s))
            }
            "ends_with" -> NepaliBuiltin("ends_with") { args, _ ->
                val s = args.getOrNull(0)?.toDisplayString() ?: ""
                NepaliBool(target.value.endsWith(s))
            }
            "split" -> NepaliBuiltin("split") { args, _ ->
                val delim = args.getOrNull(0)?.toDisplayString() ?: " "
                val parts = target.value.split(delim).map { NepaliString(it) as NepaliValue }.toMutableList()
                NepaliList(parts)
            }
            "len", "length", "lambai" -> NepaliBuiltin("len") { _, _ ->
                NepaliNumber(target.value.length.toDouble(), isInt = true)
            }
            else -> throw NepaliRuntimeException(fileName, 1, 1, "String has no method '$method'", "Supported methods: upper, lower, trim, replace, contains, starts_with, ends_with, split, len")
        }
    }

    private fun getListMethod(target: NepaliList, method: String): NepaliBuiltin {
        return when (method) {
            "add", "append", "push", "thapa" -> NepaliBuiltin("push") { args, _ ->
                val item = args.getOrNull(0) ?: NepaliNull
                target.elements.add(item)
                NepaliNull
            }
            "pop", "nikala" -> NepaliBuiltin("pop") { _, _ ->
                if (target.elements.isNotEmpty()) {
                    target.elements.removeAt(target.elements.size - 1)
                } else {
                    NepaliNull
                }
            }
            "remove", "hatau" -> NepaliBuiltin("remove") { args, _ ->
                val item = args.getOrNull(0) ?: NepaliNull
                target.elements.removeIf { it.toDisplayString() == item.toDisplayString() }
                NepaliNull
            }
            "size", "length", "len", "lambai", "count" -> NepaliBuiltin("size") { _, _ ->
                NepaliNumber(target.elements.size.toDouble(), isInt = true)
            }
            "clear", "khali" -> NepaliBuiltin("clear") { _, _ ->
                target.elements.clear()
                NepaliNull
            }
            "join" -> NepaliBuiltin("join") { args, _ ->
                val delim = args.getOrNull(0)?.toDisplayString() ?: ", "
                NepaliString(target.elements.joinToString(delim) { it.toDisplayString() })
            }
            else -> throw NepaliRuntimeException(fileName, 1, 1, "List has no method '$method'", "Supported methods: push, pop, add, remove, size, clear, join")
        }
    }

    private fun getMapMethod(target: NepaliMap, method: String): NepaliBuiltin {
        return when (method) {
            "get" -> NepaliBuiltin("get") { args, _ ->
                val key = args.getOrNull(0)?.toDisplayString() ?: ""
                target.entries[key] ?: args.getOrNull(1) ?: NepaliNull
            }
            "keys" -> NepaliBuiltin("keys") { _, _ ->
                NepaliList(target.entries.keys.map { NepaliString(it) }.toMutableList())
            }
            "values" -> NepaliBuiltin("values") { _, _ ->
                NepaliList(target.entries.values.toMutableList())
            }
            "size" -> NepaliBuiltin("size") { _, _ ->
                NepaliNumber(target.entries.size.toDouble(), isInt = true)
            }
            else -> throw NepaliRuntimeException(fileName, 1, 1, "Map has no method '$method'", "Supported methods: get, keys, values, size")
        }
    }
}
