package net.frozenorb.potpvp.kt.menu.protocol

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.EnumWrappers
import net.frozenorb.potpvp.PotPvPSI
import net.frozenorb.potpvp.kt.menu.protocol.event.PlayerCloseInventoryEvent
import net.frozenorb.potpvp.kt.menu.protocol.event.PlayerOpenInventoryEvent
import org.bukkit.Bukkit
import java.util.*

class InventoryAdapter : PacketAdapter(PotPvPSI.getInstance(), PacketType.Play.Client.CLIENT_COMMAND, PacketType.Play.Client.CLOSE_WINDOW) {

    override fun onPacketReceiving(event: PacketEvent?) {
        val player = event!!.player
        val packet = event.packet

        if (packet.type === PacketType.Play.Client.CLIENT_COMMAND && packet.clientCommands.size() != 0 && packet.clientCommands.read(0) == EnumWrappers.ClientCommand.OPEN_INVENTORY_ACHIEVEMENT) {
            if (!currentlyOpen.contains(player.uniqueId)) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(PotPvPSI.getInstance()) { Bukkit.getPluginManager().callEvent(PlayerOpenInventoryEvent(player)) }
            }

            currentlyOpen.add(player.uniqueId)
        } else if (packet.type === PacketType.Play.Client.CLOSE_WINDOW) {
            if (currentlyOpen.contains(player.uniqueId)) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(PotPvPSI.getInstance()) { Bukkit.getPluginManager().callEvent(PlayerCloseInventoryEvent(player)) }
            }

            currentlyOpen.remove(player.uniqueId)
        }
    }

    companion object {
        var currentlyOpen: HashSet<UUID> = HashSet()
    }

}
