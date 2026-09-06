package com.nepalicode.dev.nepalilang.core

class NepaliEnvironment(val parent: NepaliEnvironment? = null) {
    private val bindings = mutableMapOf<String, NepaliValue>()

    fun define(name: String, value: NepaliValue) {
        bindings[name] = value
    }

    fun assign(name: String, value: NepaliValue): Boolean {
        if (bindings.containsKey(name)) {
            bindings[name] = value
            return true
        }
        if (parent != null) {
            return parent.assign(name, value)
        }
        // If not defined anywhere, define in current scope
        bindings[name] = value
        return true
    }

    fun get(name: String): NepaliValue? {
        if (bindings.containsKey(name)) {
            return bindings[name]
        }
        if (parent != null) {
            return parent.get(name)
        }
        return null
    }

    fun contains(name: String): Boolean {
        return bindings.containsKey(name) || (parent?.contains(name) == true)
    }

    fun allVariables(): Map<String, NepaliValue> {
        val all = mutableMapOf<String, NepaliValue>()
        if (parent != null) {
            all.putAll(parent.allVariables())
        }
        all.putAll(bindings)
        return all
    }
}
