package net.frozenorb.potpvp.kt.tab

import net.frozenorb.potpvp.PotPvPSI
import net.minecraft.util.com.google.gson.JsonElement
import net.minecraft.util.com.google.gson.JsonParser
import net.minecraft.util.com.mojang.authlib.GameProfile
import net.minecraft.util.com.mojang.authlib.HttpAuthenticationService
import net.minecraft.util.com.mojang.authlib.properties.PropertyMap
import net.minecraft.util.com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.net.Proxy
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference

class TabEngine {

    private var propertyMapSerializer: AtomicReference<Any> = AtomicReference()
    private var defaultPropertyMap: AtomicReference<Any> = AtomicReference()
    var layoutProvider: LayoutProvider? = null
    val tabs: ConcurrentHashMap<String, Tab> = ConcurrentHashMap()

    fun load() {
        getDefaultPropertyMap()
        TabThread().start()
        PotPvPSI.getInstance().server.pluginManager.registerEvents(TabListeners(), PotPvPSI.getInstance())
    }

    internal fun addPlayer(player: Player) {
        tabs[player.name] = Tab(player)
    }

    internal fun updatePlayer(player: Player) {
        if (tabs.containsKey(player.name)) {
            tabs[player.name]!!.update()
        }
    }

    internal fun removePlayer(player: Player) {
        tabs.remove(player.name)
    }

    private fun fetchSkin(): PropertyMap? {
        val propertyMap = PotPvPSI.getInstance().redis.runBackboneRedisCommand { redis ->
            redis.get("stark:skinPropertyMap")
        }

        if (propertyMap != null && propertyMap.isNotEmpty()) {
            Bukkit.getLogger().info("Using cached PropertyMap for skin...")
            val jsonObject = JsonParser().parse(propertyMap).asJsonArray
            return getPropertyMapSerializer().deserialize(jsonObject as JsonElement, null, null)
        }

        val profile = GameProfile(UUID.fromString("6b22037d-c043-4271-94f2-adb00368bf16"), "bananasquad")
        val authenticationService = YggdrasilAuthenticationService(Proxy.NO_PROXY, "") as HttpAuthenticationService
        val sessionService = authenticationService.createMinecraftSessionService()
        val profile2 = sessionService.fillProfileProperties(profile, true)
        val localPropertyMap = profile2.properties

        PotPvPSI.getInstance().redis.runBackboneRedisCommand { redis ->
            Bukkit.getLogger().info("Caching PropertyMap for skin...")
            redis.setex("stark:skinPropertyMap", 3600, getPropertyMapSerializer().serialize(localPropertyMap, null, null).toString())
        }

        return localPropertyMap
    }

    fun getPropertyMapSerializer(): PropertyMap.Serializer {
        var value = propertyMapSerializer.get()
        if (value == null) {
            synchronized(propertyMapSerializer) {
                value = propertyMapSerializer.get()
                if (value == null) {
                    val actualValue = PropertyMap.Serializer()
                    value = actualValue
                    propertyMapSerializer.set(value)
                }
            }
        }
        return (if (value === propertyMapSerializer) null else value) as PropertyMap.Serializer
    }

    fun getDefaultPropertyMap(): PropertyMap {
        var value = defaultPropertyMap.get()
        if (value == null) {
            synchronized(defaultPropertyMap) {
                value = defaultPropertyMap.get()
                if (value == null) {
                    val actualValue = fetchSkin()
                    value = actualValue ?: defaultPropertyMap
                    defaultPropertyMap.set(value)
                }
            }
        }
        return (if (value === defaultPropertyMap) null else value) as PropertyMap
    }

}