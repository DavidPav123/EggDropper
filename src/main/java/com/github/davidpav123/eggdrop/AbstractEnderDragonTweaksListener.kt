package com.github.davidpav123.eggdrop

import org.bukkit.event.Listener

abstract class AbstractEnderDragonTweaksListener : Listener {
    abstract fun shouldRegisterListener(): Boolean
    abstract val statisticsDefaults: Map<String?, Any?>?
}
