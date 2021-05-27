package net.frozenorb.potpvp.kt.nametag

import net.frozenorb.potpvp.PotPvPSI
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.metadata.MetadataValue

internal class NametagListener : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        event.player.setMetadata("starkNametag-LoggedIn", FixedMetadataValue(PotPvPSI.getInstance(), true) as MetadataValue)
        PotPvPSI.getInstance().nametagEngine.initiatePlayer(event.player)
        PotPvPSI.getInstance().nametagEngine.reloadPlayer(event.player)
        PotPvPSI.getInstance().nametagEngine.reloadOthersFor(event.player)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        event.player.removeMetadata("starkNametag-LoggedIn", PotPvPSI.getInstance())
        PotPvPSI.getInstance().nametagEngine.teamMap.remove(event.player.name)
    }
}