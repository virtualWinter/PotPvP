package net.frozenorb.potpvp.setting.command;

import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.setting.menu.SettingsMenu;
import org.bukkit.entity.Player;

/**
 * /settings, accessible by all users, opens a {@link SettingsMenu}
 */
public final class SettingsCommand {

    @Command(names = {"settings", "preferences", "prefs", "options"}, permission = "")
    public static void settings(Player sender) {
        new SettingsMenu().openMenu(sender);
    }

}