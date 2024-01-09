package io.github.divinerealms.utils;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.divinerealms.configs.Config;
import io.github.divinerealms.managers.UtilManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Slime;

@Getter
public class LineChecker implements Runnable {
    private final Logger logger;
    private final Config config;
    private final RegionManager regionManager;

    public LineChecker(final UtilManager utilManager) {
        this.logger = utilManager.getLogger();
        this.config = utilManager.getConfig();
        this.regionManager = WorldGuardPlugin.inst().getRegionManager(Bukkit.getWorld("world"));
    }

    @Override
    public void run() {
        for (final String regionName : getConfig().getStringList("regions")) {
            final ProtectedRegion region = getRegionManager().getRegion(regionName);

            if (region == null) continue;

            for (final Entity cube : Bukkit.getWorld("world").getEntities()) {
                if (!(cube instanceof Slime)) continue;

                final Location cubeLoc = cube.getLocation();
                final Vector cubeVector = new Vector(cubeLoc.getX(), cubeLoc.getY(), cubeLoc.getZ());
                if (region.contains(cubeVector)) {
                    //cube.remove();
                    getLogger().send("fcfa", "IDE GAS: lopta presla gol liniju " + regionName);
                }
            }
        }
    }
}
