package server;

import client.SkillFactory;
import client.inventory.MapleInventoryIdentifier;
import constants.ServerConstants;
import handling.MapleServerHandler;
import handling.channel.ChannelServer;
import handling.channel.MapleGuildRanking;
import handling.login.LoginServer;
import handling.login.LoginInformationProvider;
import handling.world.World;
import java.sql.SQLException;
import database.DatabaseConnection;
import handling.cashshop.CashShopServer;
import handling.world.family.MapleFamily;
import handling.world.guild.MapleGuild;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import server.Timer.*;
import server.events.MapleOxQuizFactory;
import server.life.MapleLifeFactory;
import server.life.MapleMonsterInformationProvider;
import server.life.MobSkillFactory;
import server.life.PlayerNPC;
import server.maps.MapleMap;
import server.quest.MapleQuest;
import java.util.concurrent.atomic.AtomicInteger;

public class Start {

    public static long startTime = System.currentTimeMillis();
    public static final Start instance = new Start();
    public static AtomicInteger CompletedLoadingThreads = new AtomicInteger(0);
    public static List<Thread> threads = new ArrayList<>();

    public void run() throws InterruptedException {
        System.setProperty("net.sf.odinms.wzpath", "wz");
        System.setProperty("polyglot.js.nashorn-compat", "true");
        if (Boolean.parseBoolean(ServerProperties.getProperty("net.sf.odinms.world.admin"))
                || ServerConstants.Use_Localhost) {
            ServerConstants.Use_Fixed_IV = false;
            System.out.println("[!!! Admin Only Mode Active !!!]");
        }
        var con = DatabaseConnection.getConnection();
        try {
            try (PreparedStatement ps = con.prepareStatement("UPDATE accounts SET loggedin = 0")) {
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("[EXCEPTION] Please check if the SQL server is active.");
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (Exception ignore) {
            }
        }
        System.out.println("Starting " + ServerProperties.getProperty("net.sf.odinms.login.serverName") + " v"
                + ServerConstants.MAPLE_VERSION + "." + ServerConstants.MAPLE_PATCH);

        System.out.print(System.lineSeparator());

        long start = System.currentTimeMillis();

        // Loading Skills -> ok
        SkillFactory.load();

        System.out.print("Loading World... ");
        World.init();
        System.out.println("loaded in " + (System.currentTimeMillis() - start) + "ms.");

        start = System.currentTimeMillis();
        System.out.print("Loading Timers... ");
        WorldTimer.getInstance().start();
        EtcTimer.getInstance().start();
        MapTimer.getInstance().start();
        CloneTimer.getInstance().start();
        EventTimer.getInstance().start();
        BuffTimer.getInstance().start();
        PingTimer.getInstance().start();
        System.out.println("loaded in " + (System.currentTimeMillis() - start) + "ms.");

        // Loading Random Rewards -> ... Should load before Maple Quests. OK
        RandomRewards.load();

        // Maple Quest -> gonna show its own message. ok
        MapleQuest.initQuests();

        // Load Player NPC
        PlayerNPC.loadAll();// touch - so we see database problems early... ok

        // Loading Random Rewards -> gonna show its own message. ok
        MTSStorage.load();

        // Updating Inventory Identifier -> made here bc just a line... ._.
        Thread t = new Thread(() -> {
            final long startx = System.currentTimeMillis();
            MapleInventoryIdentifier.getInstance();
            System.out.println("Guilds Ranking loaded in " + (System.currentTimeMillis() - startx) + "ms.");
        });
        threads.add(t);

        // Loading Guild Ranking -> gonna show its own message. ok
        MapleGuildRanking.getInstance().load();

        // Loading Guilds -> gonna show its own message. ok
        MapleGuild.loadAll(); // (this);

        // Maple Family -> gonna show its own message. ok
        MapleFamily.loadAll();

        // Loading Maple Carnival Factory ok
        MapleCarnivalFactory.getInstance();

        // Maple Quest count -> gonna show its own message. ok
        MapleLifeFactory.loadQuestCounts();

        Thread t2 = new Thread(() -> {

            // Load ETC -> gonna show its own message.
            MapleItemInformationProvider.getInstance().runEtc();

            // Loading Mobs -> ...
            MapleMonsterInformationProvider.getInstance().load();

            // Loading Items -> ...
            MapleItemInformationProvider.getInstance().runItems();

        });
        threads.add(t2);

        // Loading Login information -> ... ok
        LoginInformationProvider.getInstance();

        // Loading MapleOxQuizFactory -> ... ok
        MapleOxQuizFactory.getInstance();

        // Loading Mob Skill Factory -> ... ok
        MobSkillFactory.getInstance();

        // SpeedRunner.loadSpeedRuns();
        // Loading Cash Item Factory -> ... ok
        CashItemFactory.getInstance().initialize();

        // Loading MapleServerHandler -> ... ok
        MapleServerHandler.initiate();

        // Loading Login Server... ok
        LoginServer.run_startup_configurations();

        // Start Channel Server... ok
        ChannelServer.startChannel_Main();

        // Load Cash Shop Server -> ...
        CashShopServer.run_startup_configurations();

        Thread t3 = new Thread(() -> {
            // Loading Cheat Timer - alredy in a thread
            CheatTimer.getInstance().register(AutobanManager.getInstance(), 60000);

            // Loading Shutdown hook - already in a thread
            Runtime.getRuntime().addShutdownHook(new Thread(new Shutdown()));

            // Loading respawn - Already in its own thread by channel
            World.registerRespawn();
        });
        threads.add(t3);

        // ChannelServer.getInstance(1).getMapFactory().getMap(910000000).spawnRandDrop();
        // //start it off
        // Loading ShudownServer Mbean register
        ShutdownServer.registerMBean();
        // ServerConstants.registerMBean();

        MapleMonsterInformationProvider.getInstance().addExtra();
        LoginServer.setOn(); // now or later
        RankingWorker.run();
        Thread tdc = new Thread(new DiseaseChecker());
        threads.add(tdc);
        threads.parallelStream().forEach(tx -> {
            tx.start();
        });
    }

    public static class Shutdown implements Runnable {

        @Override
        public void run() {
            ShutdownServer.getInstance().run();
            ShutdownServer.getInstance().run();
        }
    }

    public static void main(final String args[]) throws InterruptedException {
        instance.run();
    }

    public static class DiseaseChecker implements Runnable {

        @Override
        public void run() {
            System.out.println("Starting Diseases checker thread...");
            try {
                while (true) {
                    // Remove parallelStream(). if the processor suffers xD
                    // System.out.println("Checking diseases...");
                    ChannelServer.getAllInstances().parallelStream().forEach((chs) -> {
                        chs.getPlayerStorage().getAllCharacters().parallelStream().forEach((chr) -> {
                            MapleMap map = chr.getMap();
                            if (map != null) {
                                if (chr.getDiseaseSize() > 0) {
                                    chr.getAllDiseases().parallelStream().forEach((m) -> {
                                        // System.out.print(">removing " + m.disease);
                                        chr.dispelDebuff(m.disease);
                                    });
                                }
                            }
                        });

                    });

                    Thread.sleep(2000);
                }
            } catch (Exception e) {
            }

        }
    }
}
