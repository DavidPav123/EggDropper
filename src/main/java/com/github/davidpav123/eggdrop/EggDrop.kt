package com.github.davidpav123.eggdrop

import org.bukkit.command.CommandExecutor
import org.bukkit.command.TabCompleter
import org.bukkit.plugin.java.JavaPlugin

class EggDrop : JavaPlugin() {
    override fun onEnable() {
        configManager = ConfigManager(this)
        if (!configManager!!.MAIN_SECTION.isEnabled) return

        //setupCommand("respawndragon", new RespawnDragonCommand(), null);
        val listeners = arrayOf(
            EnderDragonDeathListener(),
        )
        val pluginManager = server.pluginManager
        for (listener in listeners) {
            if (listener.shouldRegisterListener()) {
                pluginManager.registerEvents(listener, this)
            }
        }
    }

    @Suppress("unused")
    private fun setupCommand(name: String, executor: CommandExecutor, tabCompleter: TabCompleter) {
        val command = getCommand(name)!!
        command.setExecutor(executor)
        command.tabCompleter = tabCompleter
    }

    companion object {
        const val LOG_PREFIX = "[EnderDragonTweaks] "

        @JvmStatic
        var configManager: ConfigManager? = null
            private set
    }
}
