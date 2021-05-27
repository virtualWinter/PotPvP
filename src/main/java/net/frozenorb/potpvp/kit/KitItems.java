package net.frozenorb.potpvp.kit;

import lombok.experimental.UtilityClass;
import net.frozenorb.potpvp.kt.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.ChatColor.AQUA;

@UtilityClass
public final class KitItems {

    public static final ItemStack OPEN_EDITOR_ITEM = new ItemStack(Material.BOOK);

    static {
        ItemUtils.setDisplayName(OPEN_EDITOR_ITEM, AQUA + "Edit Kits");
    }

}