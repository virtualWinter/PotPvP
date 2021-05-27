package net.frozenorb.potpvp.party;

import lombok.experimental.UtilityClass;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kt.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static net.frozenorb.potpvp.PotPvPLang.LEFT_ARROW;
import static net.frozenorb.potpvp.PotPvPLang.RIGHT_ARROW;
import static org.bukkit.ChatColor.*;

@UtilityClass
public final class PartyItems {

    public static final Material ICON_TYPE = Material.NETHER_STAR;

    public static final ItemStack LEAVE_PARTY_ITEM = new ItemStack(Material.FIRE);
    public static final ItemStack START_TEAM_SPLIT_ITEM = new ItemStack(Material.DIAMOND_SWORD);
    public static final ItemStack START_FFA_ITEM = new ItemStack(Material.GOLD_SWORD);
    public static final ItemStack OTHER_PARTIES_ITEM = new ItemStack(Material.SKULL_ITEM);

    static {
        ItemUtils.setDisplayName(LEAVE_PARTY_ITEM, RED + "Leave Party");
        ItemUtils.setDisplayName(START_TEAM_SPLIT_ITEM, YELLOW + "Start Team Split");
        ItemUtils.setDisplayName(START_FFA_ITEM, YELLOW + "Start Party FFA");
        ItemUtils.setDisplayName(OTHER_PARTIES_ITEM, GREEN + "Other Parties");
    }

    public static ItemStack icon(Party party) {
        ItemStack item = new ItemStack(ICON_TYPE);

        String leaderName = PotPvPSI.getInstance().uuidCache.name(party.getLeader());
        String displayName = LEFT_ARROW + AQUA + BOLD + leaderName + AQUA + "'s Party" + RIGHT_ARROW;

        ItemUtils.setDisplayName(item, displayName);
        return item;
    }

}
