package net.frozenorb.potpvp.scoreboard;

import net.frozenorb.potpvp.kt.scoreboard.ScoreboardConfiguration;
import net.frozenorb.potpvp.kt.scoreboard.TitleGetter;
import net.minecraft.util.org.apache.commons.lang3.StringEscapeUtils;

public final class PotPvPScoreboardConfiguration {

    public static ScoreboardConfiguration create() {
        return new ScoreboardConfiguration(
                TitleGetter.forStaticString("&6&lMineHQ &7" + StringEscapeUtils.unescapeJava("\u2758") + " &fPotPvP"),
                new MultiplexingScoreGetter(
                        new MatchScoreGetter(),
                        new LobbyScoreGetter()
                ));
    }

}
