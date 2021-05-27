package net.frozenorb.potpvp.kt.tab

import net.frozenorb.potpvp.PotPvPSI
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitRunnable

class TabListeners : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        object : BukkitRunnable() {
            override fun run() {
                PotPvPSI.getInstance().tabEngine.addPlayer(event.player)
            }
        }.runTaskLater(PotPvPSI.getInstance(), 10L)
    }

    @EventHandler
    fun onPlayerLeave(event: PlayerQuitEvent) {
        PotPvPSI.getInstance().tabEngine.removePlayer(event.player)
        TabLayout.remove(event.player)
    }

}