package com.github.davidpav123.eggdrop

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.Plugin
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files

class ConfigManager(plugin: Plugin) {
    @JvmField
    val MAIN_SECTION = ConfigSection(null)

    @JvmField
    val FEATURE_EGG_RESPAWN = ConfigSection("egg-respawn")

    @JvmField
    val config: FileConfiguration

    init {
        // Check if the config file is present before the default config is saved to disk
        val configFile = File(plugin.dataFolder, "config.yml")
        val configFileExisted = configFile.exists()
        config = plugin.config
        plugin.saveDefaultConfig()
        val loadedConfigVersion = config.getInt("version")
        checkConfigVersion(configFile, configFileExisted, loadedConfigVersion, CONFIG_VERSION, "config")
    }

    companion object {
        // Increment this when updating the default config in any way
        // NOTE: The value of the version key in the config should ALWAYS BE 0 to allow the below code to work
        private const val CONFIG_VERSION = 4

        @JvmStatic
        fun checkConfigVersion(
            configFile: File,
            configFileExisted: Boolean,
            loadedConfigVersion: Int,
            targetConfigVersion: Int,
            noun: String?
        ) {
            if (loadedConfigVersion != targetConfigVersion) {
                if (configFileExisted) {
                    // The loaded config is the wrong version (the version number is different to current or doesn't exist)
                    Util.logWarning(
                        String.format(
                            "Your EnderDragonTweaks config is an old/unexpected version (config v%s)!" + " Delete/rename it and restart the server to generate a new one (config v%s)." + " (Not updating may result in default config values being used instead of your custom ones!)",
                            loadedConfigVersion,
                            targetConfigVersion
                        ).replace("config".toRegex(), noun!!)
                    )
                } else {
                    // A new config has just been generated, so the version needs to be set to current to
                    // avoid future warnings. This is done because the Spigot config API is hopeless
                    val path = configFile.toPath()
                    val charset = StandardCharsets.UTF_8
                    var content: String
                    try {
                        content = Files.readString(path, charset)
                        content = content.replace("version: $loadedConfigVersion", "version: $targetConfigVersion")
                        Files.writeString(path, content, charset)
                    } catch (e: IOException) {
                        // Something weird has happened
                    }
                }
            }
        }
    }
}
