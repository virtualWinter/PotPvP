package net.frozenorb.potpvp.kt.tab

import net.frozenorb.potpvp.PotPvPSI
import org.bukkit.Bukkit

class TabThread : Thread("PotPvP - Tab Thread") {

    private val protocolLib = Bukkit.getServer().pluginManager.getPlugin("ProtocolLib")

    init {
        this.isDaemon = true
    }

    override fun run() {
        while (PotPvPSI.getInstance().isEnabled && protocolLib != null && protocolLib.isEnabled) {
            for (online in PotPvPSI.getInstance().server.onlinePlayers) {
                try {
                    PotPvPSI.getInstance().tabEngine.updatePlayer(online)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            try {
                sleep(250L)
            } catch (e2: InterruptedException) {
                e2.printStackTrace()
            }

        }
    }

}