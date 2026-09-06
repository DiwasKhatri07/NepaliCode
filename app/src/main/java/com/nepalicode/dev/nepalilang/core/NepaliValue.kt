package com.nepalicode.dev.nepalilang.core

sealed interface NepaliValue {
    fun toDisplayString(): String
    fun isTruthy(): Boolean = true
    fun typeName(): String
}

object NepaliNull : NepaliValue {
    override fun toDisplayString(): String = "None"
    override fun isTruthy(): Boolean = false
    override fun typeName(): String = "null"
}

data class NepaliBool(val value: Boolean) : NepaliValue {
    override fun toDisplayString(): String = if (value) "True" else "False"
    override fun isTruthy(): Boolean = value
    override fun typeName(): String = "boolean"
}

data class NepaliNumber(val value: Double, val isInt: Boolean = false) : NepaliValue {
    override fun toDisplayString(): String {
        return if (isInt || value % 1.0 == 0.0) {
            value.toLong().toString()
        } else {
            value.toString()
        }
    }
    override fun isTruthy(): Boolean = value != 0.0
    override fun typeName(): String = if (isInt) "number" else "decimal"

    fun toLong(): Long = value.toLong()
}

data class NepaliString(val value: String) : NepaliValue {
    override fun toDisplayString(): String = value
    override fun isTruthy(): Boolean = value.isNotEmpty()
    override fun typeName(): String = "text"
}

data class NepaliList(val elements: MutableList<NepaliValue> = mutableListOf()) : NepaliValue {
    override fun toDisplayString(): String =
        "[" + elements.joinToString(", ") {
            if (it is NepaliString) "\"${it.value}\"" else it.toDisplayString()
        } + "]"
    override fun isTruthy(): Boolean = elements.isNotEmpty()
    override fun typeName(): String = "list"
}

data class NepaliMap(val entries: MutableMap<String, NepaliValue> = mutableMapOf()) : NepaliValue {
    override fun toDisplayString(): String {
        val pairs = entries.map { (k, v) ->
            val valStr = if (v is NepaliString) "\"${v.value}\"" else v.toDisplayString()
            "\"$k\": $valStr"
        }
        return "{" + pairs.joinToString(", ") + "}"
    }
    override fun isTruthy(): Boolean = entries.isNotEmpty()
    override fun typeName(): String = "map"
}

data class NepaliFunction(
    val name: String,
    val params: List<String>,
    val body: List<Stmt>,
    val closure: NepaliEnvironment
) : NepaliValue {
    override fun toDisplayString(): String = "<kaam $name(${params.joinToString(", ")})>"
    override fun typeName(): String = "function"
}

data class NepaliBuiltin(
    val name: String,
    val function: suspend (args: List<NepaliValue>, env: NepaliEnvironment) -> NepaliValue
) : NepaliValue {
    override fun toDisplayString(): String = "<built-in function $name>"
    override fun typeName(): String = "function"
}

data class NepaliModule(
    val name: String,
    val members: MutableMap<String, NepaliValue> = mutableMapOf()
) : NepaliValue {
    override fun toDisplayString(): String = "<module '$name'>"
    override fun typeName(): String = "module"
}

data class NepaliObject(
    val className: String,
    val fields: MutableMap<String, NepaliValue> = mutableMapOf(),
    val methods: MutableMap<String, NepaliBuiltin> = mutableMapOf()
) : NepaliValue {
    override fun toDisplayString(): String = "<$className object>"
    override fun typeName(): String = className
}

data class NepaliClass(
    val name: String,
    val methods: Map<String, NepaliFunction> = emptyMap()
) : NepaliValue {
    override fun toDisplayString(): String = "<kakshya $name>"
    override fun typeName(): String = "class"
}
