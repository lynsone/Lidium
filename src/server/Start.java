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
import server.Timer.*;
import server.events.MapleOxQuizFactory;
import server.life.MapleLifeFactory;
import server.life.MapleMonsterInformationProvider;
import server.life.MobSkillFactory;
import server.life.PlayerNPC;
import server.quest.MapleQuest;
import java.util.concurrent.atomic.AtomicInteger;

public class Start {

    public static long startTime = System.currentTimeMillis();
    public static final Start instance = new Start();
    public static AtomicInteger CompletedLoadingThreads = new AtomicInteger(0);

    public void run() throws InterruptedException {
        System.setProperty("net.sf.odinms.wzpath", "wz");
        System.setProperty("polyglot.js.nashorn-compat", "true");
        if (Boolean.parseBoolean(ServerProperties.getProperty("net.sf.odinms.world.admin")) || ServerConstants.Use_Localhost) {
            ServerConstants.Use_Fixed_IV = false;
            System.out.println("[!!! Admin Only Mode Active !!!]");
        }

        try {
            try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("UPDATE accounts SET loggedin = 0")) {
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("[EXCEPTION] Please check if the SQL server is active.");
        }

        System.out.println(ServerProperties.getProperty("net.sf.odinms.login.serverName"));
        World.init();
        WorldTimer.getInstance().start();
        EtcTimer.getInstance().start();
        MapTimer.getInstance().start();
        CloneTimer.getInstance().start();
        EventTimer.getInstance().start();
        BuffTimer.getInstance().start();
        PingTimer.getInstance().start();
        System.out.println("Loading guilds...");
        MapleGuildRanking.getInstance().load();
        MapleGuild.loadAll(); //(this); 
        System.out.println("Loading families...");
        MapleFamily.loadAll(); //(this); 
        System.out.println("Loading quest counts...");
        MapleLifeFactory.loadQuestCounts();
        MapleQuest.initQuests();
        System.out.println("Loading etcs...");
        MapleItemInformationProvider.getInstance().runEtc();
        System.out.println("Loading mobs...");
        MapleMonsterInformationProvider.getInstance().load();
        System.out.println("Loading items...");
        MapleItemInformationProvider.getInstance().runItems();
        System.out.println("Loading skills...");
        SkillFactory.load();
        System.out.println("Loader 8...");
        LoginInformationProvider.getInstance();
        RandomRewards.load();
        MapleOxQuizFactory.getInstance();
        MapleCarnivalFactory.getInstance();
        MobSkillFactory.getInstance();
        //SpeedRunner.loadSpeedRuns();
        //MTSStorage.load();
        System.out.println("Loader 9...");
        MapleInventoryIdentifier.getInstance();
        //System.out.println("Loader 10...");
        CashItemFactory.getInstance().initialize();  
        MapleServerHandler.initiate();
        System.out.println("[Loading Login]");
        LoginServer.run_startup_configurations();
        System.out.println("[Login Initialized]");

        System.out.println("[Loading Channel]");
        ChannelServer.startChannel_Main();
        System.out.println("[Channel Initialized]");

        System.out.println("[Loading CS]");
        CashShopServer.run_startup_configurations();
        System.out.println("[CS Initialized]");
        CheatTimer.getInstance().register(AutobanManager.getInstance(), 60000);
        Runtime.getRuntime().addShutdownHook(new Thread(new Shutdown()));
        World.registerRespawn();
        //ChannelServer.getInstance(1).getMapFactory().getMap(910000000).spawnRandDrop(); //start it off
        ShutdownServer.registerMBean();
        //ServerConstants.registerMBean();
        PlayerNPC.loadAll();// touch - so we see database problems early...
        MapleMonsterInformationProvider.getInstance().addExtra();
        LoginServer.setOn(); //now or later
        System.out.println("[Fully Initialized in " + ((System.currentTimeMillis() - startTime) / 1000) + " seconds]");
        RankingWorker.run();
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
}
