package com.github.davidpav123.eggdrop;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.Objects;

public class EnderDragonDeathListener extends AbstractEnderDragonTweaksListener {


    private static boolean doSpawnEgg;
    private static Vector configuredEggLocationAsVector;
    private static boolean overrideEggY;


    public EnderDragonDeathListener() {
        final ConfigManager configManager = EggDrop.getConfigManager();
        assert configManager != null;

        doSpawnEgg = configManager.FEATURE_EGG_RESPAWN.isEnabled();
        if (doSpawnEgg) {
            configuredEggLocationAsVector = new Vector(configManager.FEATURE_EGG_RESPAWN.getInt("position.x"), configManager.FEATURE_EGG_RESPAWN.getInt("position.y"), configManager.FEATURE_EGG_RESPAWN.getInt("position.z"));
            overrideEggY = configManager.FEATURE_EGG_RESPAWN.getBoolean("position.override-y");
        }

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEnderDragonDeath(EntityDeathEvent e) {
        if (!(e.getEntity() instanceof EnderDragon)) return;

        final LivingEntity dragonEntity = e.getEntity();
        final World theEnd = dragonEntity.getWorld();

        if (theEnd.getEnvironment() != Environment.THE_END) return;

        new BukkitRunnable() {
            @Override
            public void run() {
                if (doSpawnEgg) spawnEgg(theEnd);
            }
        }.runTaskLater(EggDrop.getPlugin(EggDrop.class), 80);
    }

    private void spawnEgg(World theEnd) {
        // The game automatically spawns an Egg when the Dragon is first killed
        // This plugin shouldn't spawn another one
        if (!Objects.requireNonNull(theEnd.getEnderDragonBattle()).hasBeenPreviouslyKilled()) {
            return;
        }

        Location eggLocation = findSpawnEggLocation(theEnd);
        if (eggLocation == null) {
            Util.logWarning("Unable to find a suitable position for the Dragon Egg");
            return;
        }
        eggLocation.getBlock().setType(Material.DRAGON_EGG);
        Util.logInfo("Spawned Dragon Egg at " + String.format("%s %s %s in world %s", eggLocation.getBlockX(), eggLocation.getBlockY(), eggLocation.getBlockZ(), eggLocation.getWorld().getName()));
    }

    private Location findSpawnEggLocation(World theEnd) {
        final Location searchLocation = new Location(theEnd, configuredEggLocationAsVector.getBlockX(), Objects.requireNonNull(Objects.requireNonNull(theEnd.getEnderDragonBattle()).getEndPortalLocation()).getY(), configuredEggLocationAsVector.getBlockZ());

        if (overrideEggY) {
            searchLocation.setY(configuredEggLocationAsVector.getBlockY());
            return searchLocation;
        }

        final int startY = searchLocation.getBlockY();
        final int worldHeight = theEnd.getMaxHeight();
        // Search through the Y value of (0, 0) to find a place for the egg
        for (int i = startY; i < worldHeight; i++) {
            searchLocation.setY(i);
            if (searchLocation.getBlock().getType().isAir()) {
                return searchLocation;
            }
        }
        // Unable to find anywhere for the Egg to go
        return null;
    }

    @Override
    public boolean shouldRegisterListener() {
        return doSpawnEgg;
    }

    @Override
    public Map<String, Object> getStatisticsDefaults() {

        return null;
    }
}