package net.frozenorb.potpvp.kt.command.data.parameter.impl

import net.frozenorb.potpvp.kt.command.data.parameter.ParameterType
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class BooleanParameterType : ParameterType<Boolean?> {

    private val map: MutableMap<String, Boolean> = mutableMapOf()

    init {
        map["on"] = true
        map["yes"] = true
        map["false"] = false
        map["off"] = false
        map["no"] = false
    }

    override fun transform(sender: CommandSender, source: String): Boolean? {
        if (!this.map.containsKey(source.toLowerCase())) {
            sender.sendMessage("${ChatColor.RED} $source is not a valid boolean.")
            return null
        }

        return this.map[source.toLowerCase()]
    }

    override fun tabComplete(sender: Player, flags: Set<String>, source: String): List<String> {
        return ArrayList(this.map.keys)
    }

}