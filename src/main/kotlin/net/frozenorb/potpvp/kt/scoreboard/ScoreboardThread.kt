package net.frozenorb.potpvp.kt.scoreboard

import net.frozenorb.potpvp.PotPvPSI

class ScoreboardThread : Thread("stark - Scoreboard Thread") {

    init {
        this.isDaemon = true
    }

    override fun run() {
        while (true) {
            for (online in PotPvPSI.getInstance().server.onlinePlayers) {
                try {
                    PotPvPSI.getInstance().scoreboardEngine.updateScoreboard(online)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            try {
                sleep(PotPvPSI.getInstance().scoreboardEngine.updateInterval * 50L)
            } catch (e2: InterruptedException) {
                e2.printStackTrace()
            }

        }
    }

}