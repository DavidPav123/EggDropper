package com.github.davidpav123.eggdrop

import com.github.davidpav123.eggdrop.EggDrop.Companion.configManager
import com.github.davidpav123.eggdrop.Util.logInfo
import com.github.davidpav123.eggdrop.Util.logWarning
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.EnderDragon
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import java.util.*

class EnderDragonDeathListener : AbstractEnderDragonTweaksListener() {
    init {
        val configManager = configManager!!
        doSpawnEgg = configManager.FEATURE_EGG_RESPAWN.isEnabled
        if (doSpawnEgg) {
            configuredEggLocationAsVector = Vector(
                configManager.FEATURE_EGG_RESPAWN.getInt("position.x"),
                configManager.FEATURE_EGG_RESPAWN.getInt("position.y"),
                configManager.FEATURE_EGG_RESPAWN.getInt("position.z")
            )
            overrideEggY = configManager.FEATURE_EGG_RESPAWN.getBoolean("position.override-y")
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onEnderDragonDeath(e: EntityDeathEvent) {
        if (e.entity !is EnderDragon) return
        val dragonEntity = e.entity
        val theEnd = dragonEntity.world
        if (theEnd.environment != World.Environment.THE_END) return
        object : BukkitRunnable() {
            override fun run() {
                if (doSpawnEgg) spawnEgg(theEnd)
            }
        }.runTaskLater(EggDrop.getPlugin(), 80)
    }

    private fun spawnEgg(theEnd: World) {
        // The game automatically spawns an Egg when the Dragon is first killed
        // This plugin shouldn't spawn another one
        if (!Objects.requireNonNull(theEnd.enderDragonBattle)!!.hasBeenPreviouslyKilled()) {
            return
        }
        val eggLocation = findSpawnEggLocation(theEnd)
        if (eggLocation == null) {
            logWarning("Unable to find a suitable position for the Dragon Egg")
            return
        }
        eggLocation.block.type = Material.DRAGON_EGG
        logInfo(
            "Spawned Dragon Egg at " + String.format(
                "%s %s %s in world %s",
                eggLocation.blockX,
                eggLocation.blockY,
                eggLocation.blockZ,
                eggLocation.getWorld().name
            )
        )
    }

    private fun findSpawnEggLocation(theEnd: World): Location? {
        val searchLocation = Objects.requireNonNull(
            Objects.requireNonNull(theEnd.enderDragonBattle)!!.endPortalLocation
        )?.let {
            Location(
                theEnd,
                configuredEggLocationAsVector!!.blockX.toDouble(),
                it.y,
                configuredEggLocationAsVector!!.blockZ.toDouble()
            )
        }
        if (overrideEggY) {
            if (searchLocation != null) {
                searchLocation.y = configuredEggLocationAsVector!!.blockY.toDouble()
            }
            return searchLocation
        }
        val startY = searchLocation?.blockY
        val worldHeight = theEnd.maxHeight
        // Search through the Y value of (0, 0) to find a place for the egg
        if (startY != null) {
            for (i in startY until worldHeight) {
                searchLocation.y = i.toDouble()
                if (searchLocation.block.type.isAir) {
                    return searchLocation
                }
            }
        }
        // Unable to find anywhere for the Egg to go
        return null
    }

    override fun shouldRegisterListener(): Boolean {
        return doSpawnEgg
    }

    companion object {
        private var doSpawnEgg: Boolean = false
        private var configuredEggLocationAsVector: Vector? = null
        private var overrideEggY = false
    }
}