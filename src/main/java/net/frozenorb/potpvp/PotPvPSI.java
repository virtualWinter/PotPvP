package net.frozenorb.potpvp;

import com.comphenix.protocol.ProtocolLibrary;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.chunksnapshot.ChunkSnapshot;
import net.frozenorb.potpvp.arena.ArenaHandler;
import net.frozenorb.potpvp.duel.DuelHandler;
import net.frozenorb.potpvp.elo.EloHandler;
import net.frozenorb.potpvp.follow.FollowHandler;
import net.frozenorb.potpvp.kit.KitHandler;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.kittype.KitTypeJsonAdapter;
import net.frozenorb.potpvp.kittype.KitTypeParameterType;
import net.frozenorb.potpvp.kt.command.CommandHandler;
import net.frozenorb.potpvp.kt.menu.ButtonListeners;
import net.frozenorb.potpvp.kt.nametag.NametagEngine;
import net.frozenorb.potpvp.kt.protocol.InventoryAdapter;
import net.frozenorb.potpvp.kt.protocol.LagCheck;
import net.frozenorb.potpvp.kt.protocol.PingAdapter;
import net.frozenorb.potpvp.kt.redis.Redis;
import net.frozenorb.potpvp.kt.redis.RedisCredentials;
import net.frozenorb.potpvp.kt.scoreboard.ScoreboardEngine;
import net.frozenorb.potpvp.kt.tab.TabAdapter;
import net.frozenorb.potpvp.kt.tab.TabEngine;
import net.frozenorb.potpvp.kt.util.serialization.*;
import net.frozenorb.potpvp.kt.uuid.RedisUUIDCache;
import net.frozenorb.potpvp.kt.uuid.UUIDCache;
import net.frozenorb.potpvp.kt.visibility.VisibilityEngine;
import net.frozenorb.potpvp.listener.*;
import net.frozenorb.potpvp.lobby.LobbyHandler;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.nametag.PotPvPNametagProvider;
import net.frozenorb.potpvp.party.PartyHandler;
import net.frozenorb.potpvp.postmatchinv.PostMatchInvHandler;
import net.frozenorb.potpvp.queue.QueueHandler;
import net.frozenorb.potpvp.rematch.RematchHandler;
import net.frozenorb.potpvp.scoreboard.PotPvPScoreboardConfiguration;
import net.frozenorb.potpvp.setting.SettingHandler;
import net.frozenorb.potpvp.statistics.StatisticsHandler;
import net.frozenorb.potpvp.tab.PotPvPLayoutProvider;
import net.frozenorb.potpvp.tournament.TournamentHandler;
import net.frozenorb.potpvp.util.event.HalfHourEvent;
import net.frozenorb.potpvp.util.event.HourEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.craftbukkit.libs.com.google.gson.TypeAdapter;
import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonReader;
import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonWriter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import org.spigotmc.SpigotConfig;

import java.io.IOException;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class PotPvPSI extends JavaPlugin {

    private static PotPvPSI instance;

    @Getter
    @Setter
    private static Gson gson = new GsonBuilder()
            .registerTypeHierarchyAdapter(PotionEffect.class, new PotionEffectAdapter())
            .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter())
            .registerTypeHierarchyAdapter(Location.class, new LocationAdapter())
            .registerTypeHierarchyAdapter(Vector.class, new VectorAdapter())
            .registerTypeAdapter(BlockVector.class, new BlockVectorAdapter())
            .registerTypeHierarchyAdapter(KitType.class, new KitTypeJsonAdapter()) // custom KitType serializer
            .registerTypeAdapter(ChunkSnapshot.class, new ChunkSnapshotAdapter())
            .serializeNulls()
            .create();

    public static Gson plainGson = new GsonBuilder()
            .registerTypeHierarchyAdapter(PotionEffect.class, new PotionEffectAdapter())
            .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter())
            .registerTypeHierarchyAdapter(Location.class, new LocationAdapter())
            .registerTypeHierarchyAdapter(Vector.class, new VectorAdapter())
            .registerTypeAdapter(BlockVector.class, new BlockVectorAdapter())
            .serializeNulls()
            .create();

    @Getter
    private MongoClient mongoClient;
    @Getter
    private MongoDatabase mongoDatabase;

    @Getter
    private SettingHandler settingHandler;
    @Getter
    private DuelHandler duelHandler;
    @Getter
    private KitHandler kitHandler;
    @Getter
    private LobbyHandler lobbyHandler;
    @Getter
    private ArenaHandler arenaHandler;
    @Getter
    private MatchHandler matchHandler;
    @Getter
    private PartyHandler partyHandler;
    @Getter
    private QueueHandler queueHandler;
    @Getter
    private RematchHandler rematchHandler;
    @Getter
    private PostMatchInvHandler postMatchInvHandler;
    @Getter
    private FollowHandler followHandler;
    @Getter
    private EloHandler eloHandler;
    @Getter
    private TournamentHandler tournamentHandler;

    /*SI Starts*/

    @Getter
    public Redis redis;
    @Getter
    public CommandHandler commandHandler;
    @Getter
    public ScoreboardEngine scoreboardEngine;
    @Getter
    public TabEngine tabEngine;
    @Getter
    public NametagEngine nametagEngine;
    @Getter
    public VisibilityEngine visibilityEngine;
    @Getter
    public UUIDCache uuidCache;

    @Override
    public void onEnable() {
        SpigotConfig.onlyCustomTab = true;
        instance = this;
        saveDefaultConfig();

        setupMongo();
        setupRedis();

        for (World world : Bukkit.getWorlds()) {
            world.setGameRuleValue("doDaylightCycle", "false");
            world.setGameRuleValue("doMobSpawning", "false");
            world.setTime(6_000L);
        }

        uuidCache = new RedisUUIDCache();
        uuidCache.load();
        getServer().getPluginManager().registerEvents(uuidCache, this);

        commandHandler = new CommandHandler();
        commandHandler.load();
        commandHandler.registerAll(this);
        commandHandler.registerParameterType(KitType.class, new KitTypeParameterType());

        scoreboardEngine = new ScoreboardEngine();
        scoreboardEngine.load();
        scoreboardEngine.setConfiguration(PotPvPScoreboardConfiguration.create());

        tabEngine = new TabEngine();
        tabEngine.load();
        tabEngine.setLayoutProvider(new PotPvPLayoutProvider());

        nametagEngine = new NametagEngine();
        nametagEngine.load();
        nametagEngine.registerProvider(new PotPvPNametagProvider());

        visibilityEngine = new VisibilityEngine();
        visibilityEngine.load();

        PingAdapter pingAdapter = new PingAdapter();

        ProtocolLibrary.getProtocolManager().addPacketListener(pingAdapter);
        ProtocolLibrary.getProtocolManager().addPacketListener(new InventoryAdapter());
        ProtocolLibrary.getProtocolManager().addPacketListener(new TabAdapter());

        getServer().getPluginManager().registerEvents(pingAdapter, this);

        new LagCheck().runTaskTimerAsynchronously(this, 100L, 100L);

        settingHandler = new SettingHandler();
        duelHandler = new DuelHandler();
        kitHandler = new KitHandler();
        lobbyHandler = new LobbyHandler();
        arenaHandler = new ArenaHandler();
        matchHandler = new MatchHandler();
        partyHandler = new PartyHandler();
        queueHandler = new QueueHandler();
        rematchHandler = new RematchHandler();
        postMatchInvHandler = new PostMatchInvHandler();
        followHandler = new FollowHandler();
        eloHandler = new EloHandler();
        tournamentHandler = new TournamentHandler();

        getServer().getPluginManager().registerEvents(new BasicPreventionListener(), this);
        getServer().getPluginManager().registerEvents(new BowHealthListener(), this);
        getServer().getPluginManager().registerEvents(new ChatFormatListener(), this);
        getServer().getPluginManager().registerEvents(new ChatToggleListener(), this);
        getServer().getPluginManager().registerEvents(new NightModeListener(), this);
        getServer().getPluginManager().registerEvents(new PearlCooldownListener(), this);
        getServer().getPluginManager().registerEvents(new RankedMatchQualificationListener(), this);
        getServer().getPluginManager().registerEvents(new TabCompleteListener(), this);
        getServer().getPluginManager().registerEvents(new StatisticsHandler(), this);

        getServer().getPluginManager().registerEvents(new ButtonListeners(), this);
        setupHourEvents();

    }

    @Override
    public void onDisable() {
        for (Match match : this.matchHandler.getHostedMatches()) {
            if (match.getKitType().isBuildingAllowed()) match.getArena().restore();
        }

        try {
            arenaHandler.saveSchematics();
        } catch (IOException e) {
            e.printStackTrace();
        }

        instance = null;
    }

    private void setupHourEvents() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor((new ThreadFactoryBuilder()).setNameFormat("PotPvP - Hour Event Thread").setDaemon(true).build());
        int minOfHour = Calendar.getInstance().get(12);
        int minToHour = 60 - minOfHour;
        int minToHalfHour = (minToHour >= 30) ? minToHour : (30 - minOfHour);

        executor.scheduleAtFixedRate(() -> Bukkit.getScheduler().runTask(this, () -> {
            Bukkit.getServer().getPluginManager().callEvent(new HourEvent(Date.from(Instant.now()).getHours()));
        }), minToHour, 60L, TimeUnit.MINUTES);

        executor.scheduleAtFixedRate(() -> Bukkit.getScheduler().runTask(this, () -> {
            Date dateNow = Date.from(Instant.now());
            Bukkit.getServer().getPluginManager().callEvent(new HalfHourEvent(dateNow.getHours(), dateNow.getMinutes()));
        }), minToHalfHour, 30L, TimeUnit.MINUTES);
    }

    private void setupRedis() {
        redis = new Redis();

        RedisCredentials.Builder localBuilder = new RedisCredentials.Builder()
                .host(getConfig().getString("LocalRedis.Host"))
                .port(getConfig().getInt("LocalRedis.Port"));

        if (getConfig().contains("LocalRedis.Password")) {
            localBuilder.password(getConfig().getString("LocalRedis.Password"));
        }

        if (getConfig().contains("LocalRedis.DbId")) {
            localBuilder.dbId(getConfig().getInt("LocalRedis.DbId"));
        }

        RedisCredentials.Builder backboneBuilder = new RedisCredentials.Builder()
                .host(getConfig().getString("BackboneRedis.Host"))
                .port(getConfig().getInt("BackboneRedis.Port"));

        if (getConfig().contains("BackboneRedis.Password")) {
            backboneBuilder.password(getConfig().getString("BackboneRedis.Password"));
        }

        if (getConfig().contains("BackboneRedis.DbId")) {
            backboneBuilder.dbId(getConfig().getInt("BackboneRedis.DbId"));
        }

        redis.load(localBuilder.build(), backboneBuilder.build());
    }

    private void setupMongo() {
        mongoClient = new MongoClient(
                getConfig().getString("Mongo.Host"),
                getConfig().getInt("Mongo.Port")
        );

        String databaseId = getConfig().getString("Mongo.Database");
        mongoDatabase = mongoClient.getDatabase(databaseId);
    }

    // This is here because chunk snapshots are (still) being deserialized, and serialized sometimes.
    private static class ChunkSnapshotAdapter extends TypeAdapter<ChunkSnapshot> {

        @Override
        public ChunkSnapshot read(JsonReader arg0) throws IOException {
            return null;
        }

        @Override
        public void write(JsonWriter arg0, ChunkSnapshot arg1) throws IOException {

        }

    }

    public static PotPvPSI getInstance() {
        return instance;
    }

}