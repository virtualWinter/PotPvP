package net.frozenorb.potpvp.kt.scoreboard

import com.google.common.base.Preconditions
import org.bukkit.ChatColor
import org.bukkit.entity.Player

open class TitleGetter {

    val defaultTitle: String

    constructor(defaultTitle: String) {
        this.defaultTitle = ChatColor.translateAlternateColorCodes('&', defaultTitle)
    }

    open fun getTitle(player: Player): String {
        return this.defaultTitle
    }

    companion object {
        @JvmStatic
        fun forStaticString(staticString: String): TitleGetter {
            Preconditions.checkNotNull(staticString as Any)
            return TitleGetter(staticString)
        }
    }

}