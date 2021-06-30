package client.messages;

import client.MapleClient;
import database.DatabaseConnection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import tools.FileoutputUtil;
import tools.Pair;
import client.messages.commands.player.*;
import client.messages.commands.intern.*;
import client.messages.commands.gm.*;
import client.messages.commands.headgm.*;
import client.messages.commands.developer.*;
import client.messages.commands.admin.*;

public class CommandsExecutor {

    public static enum GMLevel {

        PLAYER('@', 0),
        INTERN('!', 1),
        GAMEMASTER('!', 2),
        HEAD_GAMEMASTER('!', 3),
        DEVELOPER('!', 4),
        ADMINISTRATOR('!', 5);
        private char commandPrefix;
        private int level;

        GMLevel(char ch, int level) {
            commandPrefix = ch;
            this.level = level;
        }

        public char getCommandPrefix() {
            return commandPrefix;
        }

        public int getLevel() {
            return level;
        }
    }

    public static CommandsExecutor instance = new CommandsExecutor();

    public static CommandsExecutor getInstance() {
        return instance;
    }

    private static final char USER_HEADING = '@';
    private static final char GM_HEADING = '!';

    public static boolean isCommand(MapleClient client, String content) {
        char heading = content.charAt(0);
        if (client.getPlayer().isIntern()) {
            return heading == USER_HEADING || heading == GM_HEADING;
        }
        return heading == USER_HEADING;
    }

    private HashMap<String, Command> registeredCommands = new HashMap<>();
    private Pair<List<String>, List<String>> levelCommandsCursor;
    private List<Pair<List<String>, List<String>>> commandsNameDesc = new ArrayList<>();

    private CommandsExecutor() {
        registerLv0Commands();//Player
        registerLv1Commands();//Intern
        registerLv2Commands();//GM
        registerLv3Commands();//Head GM
        registerLv4Commands();//Developer
        registerLv5Commands();//Admin
    }

    public List<Pair<List<String>, List<String>>> getGmCommands() {
        return commandsNameDesc;
    }

    public void handle(MapleClient client, String message) {
        try {
            handleInternal(client, message);
        } catch (Exception e) {
            client.getPlayer().blueMessage("Something went wrong trying to execute your command. Please notify the administrator.");
            e.printStackTrace();
        }
    }

    private void handleInternal(MapleClient client, String message) {
        if (client.getPlayer().getMapId() == 300000012) {
            client.getPlayer().blueMessage("You do not have permission to use commands while in jail.");
            return;
        }
        final String splitRegex = "[ ]";
        String[] splitedMessage = message.substring(1).split(splitRegex, 2);
        if (splitedMessage.length < 2) {
            splitedMessage = new String[]{splitedMessage[0], ""};
        }

        client.getPlayer().setLastCommandMessage(splitedMessage[1]);    // thanks Tochi & Nulliphite for noticing string messages being marshalled lowercase
        final String commandName = splitedMessage[0].toLowerCase();
        final String[] lowercaseParams = splitedMessage[1].toLowerCase().split(splitRegex);

        final Command command = registeredCommands.get(commandName);

        if (command == null) {
            client.getPlayer().blueMessage("Command '" + commandName + "' is not available.");
            return;
        }

        if (client.getPlayer().getGMLevel() < command.getRank()) {
            client.getPlayer().blueMessage("You do not have permission to use this command.");
            return;
        }
        String[] params;
        if (lowercaseParams.length > 0 && !lowercaseParams[0].isEmpty()) {
            params = Arrays.copyOfRange(lowercaseParams, 0, lowercaseParams.length);
        } else {
            params = new String[]{};
        }

        command.execute(client, params);
        writeLog(client, message);
    }

    private void writeLog(MapleClient client, String command) {
        PreparedStatement ps = null;
        try {
            ps = DatabaseConnection.getConnection().prepareStatement("INSERT INTO " + "gmlog" + " (cid, command, mapid) VALUES (?, ?, ?)");
            ps.setInt(1, client.getPlayer().getId());
            ps.setString(2, command);
            ps.setInt(3, client.getPlayer().getMap().getId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            FileoutputUtil.outputFileError(FileoutputUtil.PacketEx_Log, ex);
            ex.printStackTrace();
        } finally {
            try {
                ps.close();
            } catch (SQLException e) {/*Err.. Fuck?*/

            }
        }
    }

    private void addCommandInfo(String name, Class<? extends Command> commandClass) {
        try {
            levelCommandsCursor.getRight().add(commandClass.newInstance().getDescription());
            levelCommandsCursor.getLeft().add(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addCommand(String[] syntaxs, Class<? extends Command> commandClass) {
        for (String syntax : syntaxs) {
            addCommand(syntax, 0, commandClass);
        }
    }

    private void addCommand(String syntax, Class<? extends Command> commandClass) {
        //for (String syntax : syntaxs){
        addCommand(syntax, 0, commandClass);
        //}
    }

    private void addCommand(String[] surtaxes, int rank, Class<? extends Command> commandClass) {
        for (String syntax : surtaxes) {
            addCommand(syntax, rank, commandClass);
        }
    }

    private void addCommand(String syntax, int rank, Class<? extends Command> commandClass) {
        if (registeredCommands.containsKey(syntax.toLowerCase())) {
            System.out.println("Error on register command with name: " + syntax + ". Already exists.");
            return;
        }

        String commandName = syntax.toLowerCase();
        addCommandInfo(commandName, commandClass);

        try {
            Command commandInstance = commandClass.newInstance();     // thanks Halcyon for noticing commands getting reinstanced every call
            commandInstance.setRank(rank);

            registeredCommands.put(commandName, commandInstance);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void registerLv0Commands() {
        levelCommandsCursor = new Pair<>((List<String>) new ArrayList<String>(), (List<String>) new ArrayList<String>());
        addCommand("dispose", 0, DisposeCommand.class);
        addCommand("checkdrops", 0, CheckDropsCommand.class);
        addCommand("dex", 0, StatDexCommand.class);
        addCommand("int", 0, StatIntCommand.class);
        addCommand("luk", 0, StatLukCommand.class);
        addCommand("str", 0, StatStrCommand.class);
        addCommand("enablepic", 0, EnablePicCommand.class);
        addCommand("mob", 0, MobCommand.class);
        addCommand("clearslot", 0, ClearSlotCommand.class);
        addCommand("check", 0, CheckCommand.class);
        addCommand("help", 0, HelpCommand.class);
        addCommand("ranking", 0, RankingCommand.class);
        addCommand("togglesmega", 0, ToggleSmegaCommand.class);
        addCommand("fm", 0, FMCommand.class);
        //addCommand("", 0, Command.class);
        commandsNameDesc.add(levelCommandsCursor);
    }

    private void registerLv1Commands() {
        levelCommandsCursor = new Pair<>((List<String>) new ArrayList<String>(), (List<String>) new ArrayList<String>());
        addCommand("job", 1, JobCommand.class);
        //addCommand(new String[]{"song", "music"}, 1, .class);
        commandsNameDesc.add(levelCommandsCursor);
    }

    private void registerLv2Commands() {
        levelCommandsCursor = new Pair<>((List<String>) new ArrayList<String>(), (List<String>) new ArrayList<String>());
        addCommand("cleardrops", 2, ClearDropsCommand.class);
        //addCommand("", 2, Command.class);
        commandsNameDesc.add(levelCommandsCursor);
    }

    private void registerLv3Commands() {
        levelCommandsCursor = new Pair<>((List<String>) new ArrayList<String>(), (List<String>) new ArrayList<String>());
        addCommand("spawn", 3, SpawnCommand.class);
        addCommand("warp", 3, WarpCommand.class);
        addCommand("goto", 3, GoToCommand.class);
        //addCommand("", 3, Command.class);               
        commandsNameDesc.add(levelCommandsCursor);
    }

    private void registerLv4Commands() {
        levelCommandsCursor = new Pair<>((List<String>) new ArrayList<String>(), (List<String>) new ArrayList<String>());
        addCommand("killalldrops", 4, KillAllDropsCommand.class);
        //addCommand("", 4, Command.class);
        commandsNameDesc.add(levelCommandsCursor);
    }

    private void registerLv5Commands() {
        levelCommandsCursor = new Pair<>((List<String>) new ArrayList<String>(), (List<String>) new ArrayList<String>());
        addCommand("servermessage", 5, ServerMessageCommand.class);
        //addCommand("", 5, Command.class);             
        commandsNameDesc.add(levelCommandsCursor);
    }
}
