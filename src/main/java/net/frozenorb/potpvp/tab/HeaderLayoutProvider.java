package net.frozenorb.potpvp.tab;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kt.tab.TabLayout;
import net.frozenorb.potpvp.kt.util.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

final class HeaderLayoutProvider implements BiConsumer<Player, TabLayout> {

    @Override
    public void accept(Player player, TabLayout tabLayout) {
        header:
        {
            tabLayout.set(1, 0, "&6&lMineHQ PotPvP");
        }

        status:
        {
            tabLayout.set(1, 1, ChatColor.GRAY + "Your Connection", Math.max(((PlayerUtils.getPing(player) + 5) / 10) * 10, 1));
        }

        online:
        {
            tabLayout.set(0, 1, ChatColor.GRAY + "Online: ", Bukkit.getOnlinePlayers().size());
        }

        match:
        {
            tabLayout.set(0, 1, ChatColor.GRAY + "In Fights: ", PotPvPSI.getInstance().getMatchHandler().countPlayersPlayingInProgressMatches());
        }
    }

}
