package net.frozenorb.potpvp.tab;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kt.tab.LayoutProvider;
import net.frozenorb.potpvp.kt.tab.TabLayout;
import net.frozenorb.potpvp.kt.util.PlayerUtils;
import net.frozenorb.potpvp.match.Match;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.BiConsumer;

public final class PotPvPLayoutProvider implements LayoutProvider {

    static final int MAX_TAB_Y = 20;

    private final BiConsumer<Player, TabLayout> headerLayoutProvider = new HeaderLayoutProvider();
    private final BiConsumer<Player, TabLayout> lobbyLayoutProvider = new LobbyLayoutProvider();
    private final BiConsumer<Player, TabLayout> matchSpectatorLayoutProvider = new MatchSpectatorLayoutProvider();
    private final BiConsumer<Player, TabLayout> matchParticipantLayoutProvider = new MatchParticipantLayoutProvider();

    @Override
    public TabLayout provide(Player player) {
        if (PotPvPSI.getInstance() == null) return TabLayout.create(player);
        TabLayout tabLayout = TabLayout.create(player);


        Match match = PotPvPSI.getInstance().getMatchHandler().getMatchPlayingOrSpectating(player);
        headerLayoutProvider.accept(player, tabLayout);

        if (match != null) {
            if (match.isSpectator(player.getUniqueId())) {
                matchSpectatorLayoutProvider.accept(player, tabLayout);
            } else {
                matchParticipantLayoutProvider.accept(player, tabLayout);
            }
        } else {
            lobbyLayoutProvider.accept(player, tabLayout);
        }

        return tabLayout;
    }

    static int getPingOrDefault(UUID check) {
        Player player = Bukkit.getPlayer(check);
        return player != null ? PlayerUtils.getPing(player) : 0;
    }

}