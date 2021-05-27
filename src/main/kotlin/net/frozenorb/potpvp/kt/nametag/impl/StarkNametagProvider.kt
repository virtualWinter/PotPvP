package net.frozenorb.potpvp.kt.nametag.impl

import net.frozenorb.potpvp.kt.nametag.NametagInfo
import net.frozenorb.potpvp.kt.nametag.NametagProvider
import org.bukkit.entity.Player

class StarkNametagProvider : NametagProvider("Stark Provider", 1) {

    override fun fetchNametag(toRefresh: Player, refreshFor: Player): NametagInfo {
        return createNametag("", "")
    }

}