package com.github.davidpav123.eggdrop

import org.bukkit.Bukkit

object Util {

    @JvmStatic
    fun logInfo(message: String) {
        Bukkit.getLogger().info(EggDrop.LOG_PREFIX + message)
    }

    @JvmStatic
    fun logWarning(message: String) {
        Bukkit.getLogger().warning(EggDrop.LOG_PREFIX + message)
    }
}
