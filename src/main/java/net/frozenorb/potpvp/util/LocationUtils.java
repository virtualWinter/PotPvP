package net.frozenorb.potpvp.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;

@UtilityClass
public final class LocationUtils {

    public static String locToStr(Location loc) {
        return "(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")";
    }

}