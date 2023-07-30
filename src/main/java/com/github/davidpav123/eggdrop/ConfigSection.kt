package com.github.davidpav123.eggdrop

import com.github.davidpav123.eggdrop.EggDrop.Companion.configManager

class ConfigSection(configSectionName: String?) {
    private val configSectionHeader: String

    init {
        configSectionHeader = if (!configSectionName.isNullOrEmpty()) "$configSectionName." else ""
    }

    val isEnabled: Boolean
        get() = getBoolean("enabled")

    private fun getValue(name: String): Any {
        val path = configSectionHeader + name
        //Util.logWarning(path + " = " + value);
        return configManager!!.config[path]!!
    }

    fun getBoolean(name: String): Boolean {
        val value = getValue(name)
        return if (value is Int) {
            value > 0
        } else try {
            value as Boolean
        } catch (e: ClassCastException) {
            Util.logWarning("Config value " + name + ": " + e.message)
            false
        }
    }

    fun getInt(name: String): Int {
        return getValue(name) as Int
    }

}
