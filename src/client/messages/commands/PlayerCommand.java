package client.messages.commands;

import client.inventory.Item;
import server.RankingWorker;
import client.MapleCharacter;
import constants.ServerConstants.PlayerGMRank;
import client.MapleClient;
import client.MapleStat;
import client.inventory.MapleInventoryType;
import client.messages.commands.CommandExecute.TradeExecute;
import constants.GameConstants;
import java.util.Arrays;
import java.util.List;
import scripting.NPCScriptManager;
import server.MapleItemInformationProvider;
import server.RankingWorker.RankingInformation;
import server.life.MapleMonster;

import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.SavedLocationType;
import tools.FileoutputUtil;
import tools.StringUtil;
import tools.packet.CWvsContext;

/**
 *
 * @author Emilyx3
 */
public class PlayerCommand {

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.NORMAL;
    }

    public static class STR extends DistributeStatCommands {

        public STR() {
            stat = MapleStat.STR;
        }
    }

    public static class DEX extends DistributeStatCommands {

        public DEX() {
            stat = MapleStat.DEX;
        }
    }

    public static class INT extends DistributeStatCommands {

        public INT() {
            stat = MapleStat.INT;
        }
    }

    public static class LUK extends DistributeStatCommands {

        public LUK() {
            stat = MapleStat.LUK;
        }
    }

    public abstract static class DistributeStatCommands extends CommandExecute {

        protected MapleStat stat = null;
        private static int statLim = 999;

        private void setStat(MapleCharacter player, int amount) {
            switch (stat) {
                case STR:
                    player.getStat().setStr((short) amount, player);
                    player.updateSingleStat(MapleStat.STR, player.getStat().getStr());
                    break;
                case DEX:
                    player.getStat().setDex((short) amount, player);
                    player.updateSingleStat(MapleStat.DEX, player.getStat().getDex());
                    break;
                case INT:
                    player.getStat().setInt((short) amount, player);
                    player.updateSingleStat(MapleStat.INT, player.getStat().getInt());
                    break;
                case LUK:
                    player.getStat().setLuk((short) amount, player);
                    player.updateSingleStat(MapleStat.LUK, player.getStat().getLuk());
                    break;
            }
        }

        private int getStat(MapleCharacter player) {
            switch (stat) {
                case STR:
                    return player.getStat().getStr();
                case DEX:
                    return player.getStat().getDex();
                case INT:
                    return player.getStat().getInt();
                case LUK:
                    return player.getStat().getLuk();
                default:
                    throw new RuntimeException(); //Will never happen.
            }
        }

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(5, "Invalid number entered.");
                return 0;
            }
            int change = 0;
            try {
                change = Integer.parseInt(splitted[1]);
            } catch (NumberFormatException nfe) {
                c.getPlayer().dropMessage(5, "Invalid number entered.");
                return 0;
            }
            if (change <= 0) {
                c.getPlayer().dropMessage(5, "You must enter a number greater than 0.");
                return 0;
            }
            if (c.getPlayer().getRemainingAp() < change) {
                c.getPlayer().dropMessage(5, "You don't have enough AP for that.");
                return 0;
            }
            if (getStat(c.getPlayer()) + change > statLim) {
                c.getPlayer().dropMessage(5, "The stat limit is " + statLim + ".");
                return 0;
            }
            setStat(c.getPlayer(), getStat(c.getPlayer()) + change);
            c.getPlayer().setRemainingAp((short) (c.getPlayer().getRemainingAp() - change));
            c.getPlayer().updateSingleStat(MapleStat.AVAILABLEAP, c.getPlayer().getRemainingAp());
            c.getPlayer().dropMessage(5, StringUtil.makeEnumHumanReadable(stat.name()) + " has been raised by " + change + ".");
            return 1;
        }
    }

    public static class Mob extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            MapleMonster mob = null;
            for (final MapleMapObject monstermo : c.getPlayer().getMap().getMapObjectsInRange(c.getPlayer().getPosition(), 100000, Arrays.asList(MapleMapObjectType.MONSTER))) {
                mob = (MapleMonster) monstermo;
                if (mob.isAlive()) {
                    c.getPlayer().dropMessage(6, "Monster " + mob.toString());
                    break; //only one
                }
            }
            if (mob == null) {
                c.getPlayer().dropMessage(6, "No monster was found.");
            }
            return 1;
        }
    }

    public abstract static class OpenNPCCommand extends CommandExecute {

        protected int npc = -1;
        private static int[] npcs = { //Ish yur job to make sure these are in order and correct ;(
            9270035,
            9010017,
            9000000,
            9000030,
            9010000,
            9000085,
            9000018};

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (npc != 6 && npc != 5 && npc != 4 && npc != 3 && npc != 1 && c.getPlayer().getMapId() != 910000000) { //drpcash can use anywhere
                if (c.getPlayer().getLevel() < 10 && c.getPlayer().getJob() != 200) {
                    c.getPlayer().dropMessage(5, "You must be over level 10 to use this command.");
                    return 0;
                }
                if (c.getPlayer().isInBlockedMap()) {
                    c.getPlayer().dropMessage(5, "You may not use this command here.");
                    return 0;
                }
            } else if (npc == 1) {
                if (c.getPlayer().getLevel() < 70) {
                    c.getPlayer().dropMessage(5, "You must be over level 70 to use this command.");
                    return 0;
                }
            }
            if (c.getPlayer().hasBlockedInventory()) {
                c.getPlayer().dropMessage(5, "You may not use this command here.");
                return 0;
            }
            NPCScriptManager.getInstance().start(c, npcs[npc]);
            return 1;
        }
    }

    public static class Npc extends OpenNPCCommand {

        public Npc() {
            npc = 0;
        }
    }

    public static class DCash extends OpenNPCCommand {

        public DCash() {
            npc = 1;
        }
    }

    public static class Event extends OpenNPCCommand {

        public Event() {
            npc = 2;
        }
    }

    public static class CheckDrop extends OpenNPCCommand {

        public CheckDrop() {
            npc = 4;
        }
    }

    /*public static class ClearSlot extends CommandExecute {
    
    private static MapleInventoryType[] invs = {
    MapleInventoryType.EQUIP,
    MapleInventoryType.USE,
    MapleInventoryType.SETUP,
    MapleInventoryType.ETC,
    MapleInventoryType.CASH,};
    
    @Override
    public int execute(MapleClient c, String[] splitted) {
    MapleCharacter player = c.getPlayer();
    if (splitted.length < 2 || player.hasBlockedInventory()) {
    c.getPlayer().dropMessage(5, "@clearslot <eq/use/setup/etc/cash/all>");
    return 0;
    } else {
    MapleInventoryType type;
    if (splitted[1].equalsIgnoreCase("eq")) {
    type = MapleInventoryType.EQUIP;
    } else if (splitted[1].equalsIgnoreCase("use")) {
    type = MapleInventoryType.USE;
    } else if (splitted[1].equalsIgnoreCase("setup")) {
    type = MapleInventoryType.SETUP;
    } else if (splitted[1].equalsIgnoreCase("etc")) {
    type = MapleInventoryType.ETC;
    } else if (splitted[1].equalsIgnoreCase("cash")) {
    type = MapleInventoryType.CASH;
    } else if (splitted[1].equalsIgnoreCase("all")) {
    type = null;
    } else {
    c.getPlayer().dropMessage(5, "Invalid. @clearslot <eq/use/setup/etc/cash/all>");
    return 0;
    }
    if (type == null) { //All, a bit hacky, but it's okay
    for (MapleInventoryType t : invs) {
    type = t;
    MapleInventory inv = c.getPlayer().getInventory(type);
    byte start = -1;
    for (byte i = 0; i < inv.getSlotLimit(); i++) {
    if (inv.getItem(i) != null) {
    start = i;
    break;
    }
    }
    if (start == -1) {
    c.getPlayer().dropMessage(5, "There are no items in that inventory.");
    return 0;
    }
    int end = 0;
    for (byte i = start; i < inv.getSlotLimit(); i++) {
    if (inv.getItem(i) != null) {
    MapleInventoryManipulator.removeFromSlot(c, type, i, inv.getItem(i).getQuantity(), true);
    } else {
    end = i;
    break;//Break at first empty space.
    }
    }
    c.getPlayer().dropMessage(5, "Cleared slots " + start + " to " + end + ".");
    }
    } else {
    MapleInventory inv = c.getPlayer().getInventory(type);
    byte start = -1;
    for (byte i = 0; i < inv.getSlotLimit(); i++) {
    if (inv.getItem(i) != null) {
    start = i;
    break;
    }
    }
    if (start == -1) {
    c.getPlayer().dropMessage(5, "There are no items in that inventory.");
    return 0;
    }
    byte end = 0;
    for (byte i = start; i < inv.getSlotLimit(); i++) {
    if (inv.getItem(i) != null) {
    MapleInventoryManipulator.removeFromSlot(c, type, i, inv.getItem(i).getQuantity(), true);
    } else {
    end = i;
    break;//Break at first empty space.
    }
    }
    c.getPlayer().dropMessage(5, "Cleared slots " + start + " to " + end + ".");
    }
    return 1;
    }
    }
    }*/
    public static class FM extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            for (int i : GameConstants.blockedMaps) {
                if (c.getPlayer().getMapId() == i) {
                    c.getPlayer().dropMessage(5, "You may not use this command here.");
                    return 0;
                }
            }
            if (c.getPlayer().getLevel() < 10 && c.getPlayer().getJob() != 200) {
                c.getPlayer().dropMessage(5, "You must be over level 10 to use this command.");
                return 0;
            }
            if (c.getPlayer().hasBlockedInventory() || c.getPlayer().getMap().getSquadByMap() != null || c.getPlayer().getEventInstance() != null || c.getPlayer().getMap().getEMByMap() != null || c.getPlayer().getMapId() >= 990000000/* || FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit())*/) {
                c.getPlayer().dropMessage(5, "You may not use this command here.");
                return 0;
            }
            if ((c.getPlayer().getMapId() >= 680000210 && c.getPlayer().getMapId() <= 680000502) || (c.getPlayer().getMapId() / 1000 == 980000 && c.getPlayer().getMapId() != 980000000) || (c.getPlayer().getMapId() / 100 == 1030008) || (c.getPlayer().getMapId() / 100 == 922010) || (c.getPlayer().getMapId() / 10 == 13003000)) {
                c.getPlayer().dropMessage(5, "You may not use this command here.");
                return 0;
            }
            c.getPlayer().saveLocation(SavedLocationType.FREE_MARKET, c.getPlayer().getMap().getReturnMap().getId());
            MapleMap map = c.getChannelServer().getMapFactory().getMap(910000000);
            c.getPlayer().changeMap(map, map.getPortal(0));
            return 1;
        }
    }

    public static class EA extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            c.removeClickedNPC();
            NPCScriptManager.getInstance().dispose(c);
            c.getSession().write(CWvsContext.enableActions());
            return 1;
        }
    }

    public static class TSmega extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().setSmega();
            return 1;
        }
    }

    public static class Ranking extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 4) { //job start end
                c.getPlayer().dropMessage(5, "Use @ranking [job] [start number] [end number] where start and end are ranks of the players");
                final StringBuilder builder = new StringBuilder("JOBS: ");
                for (String b : RankingWorker.getJobCommands().keySet()) {
                    builder.append(b);
                    builder.append(" ");
                }
                c.getPlayer().dropMessage(5, builder.toString());
            } else {
                int start = 1, end = 20;
                try {
                    start = Integer.parseInt(splitted[2]);
                    end = Integer.parseInt(splitted[3]);
                } catch (NumberFormatException e) {
                    c.getPlayer().dropMessage(5, "You didn't specify start and end number correctly, the default values of 1 and 20 will be used.");
                }
                if (end < start || end - start > 20) {
                    c.getPlayer().dropMessage(5, "End number must be greater, and end number must be within a range of 20 from the start number.");
                } else {
                    final Integer job = RankingWorker.getJobCommand(splitted[1]);
                    if (job == null) {
                        c.getPlayer().dropMessage(5, "Please use @ranking to check the job names.");
                    } else {
                        final List<RankingInformation> ranks = RankingWorker.getRankingInfo(job.intValue());
                        if (ranks == null || ranks.size() <= 0) {
                            c.getPlayer().dropMessage(5, "Please try again later.");
                        } else {
                            int num = 0;
                            for (RankingInformation rank : ranks) {
                                if (rank.rank >= start && rank.rank <= end) {
                                    if (num == 0) {
                                        c.getPlayer().dropMessage(6, "Rankings for " + splitted[1] + " - from " + start + " to " + end);
                                        c.getPlayer().dropMessage(6, "--------------------------------------");
                                    }
                                    c.getPlayer().dropMessage(6, rank.toString());
                                    num++;
                                }
                            }
                            if (num == 0) {
                                c.getPlayer().dropMessage(5, "No ranking was returned.");
                            }
                        }
                    }
                }
            }
            return 1;
        }
    }

    public static class Check extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {

            c.getPlayer().dropMessage(6, "You currently have " + c.getPlayer().getCSPoints(MapleCharacter.CashShopType.NX_PREPAID) + " Cash.");
            c.getPlayer().dropMessage(6, "You currently have " + c.getPlayer().getPoints() + " donation points.");
            c.getPlayer().dropMessage(6, "You currently have " + c.getPlayer().getVPoints() + " voting points.");
            c.getPlayer().dropMessage(6, "You currently have " + c.getPlayer().getIntNoRecord(GameConstants.BOSS_PQ) + " Boss Party Quest points.");
            c.getPlayer().dropMessage(6, "The time is currently " + FileoutputUtil.CurrentReadable_TimeGMT() + " GMT.");
            return 1;
        }
    }

    public static class EnablePic extends CommandExecute{

        @Override
        public int execute(MapleClient c, String[] splitted) {
            boolean picEnable = c.isPicEnable();
            picEnable=!picEnable;
            c.setPicEnable(picEnable);
            c.getPlayer().dropMessage(-1, "Your PIC has been " + (picEnable?"enabled.":"deactivated."));
            return 0;
        }
        
    }

    public static class Help extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().dropMessage(5, "@str, @dex, @int, @luk <amount to add>");
            c.getPlayer().dropMessage(5, "@mob < Information on the closest monster >");
            c.getPlayer().dropMessage(5, "@check < Displays various information >");
            c.getPlayer().dropMessage(5, "@fm < Warp to FM >");
            /*c.getPlayer().dropMessage(5, "@changesecondpass - Change second password, @changesecondpass <current Password> <new password> <Confirm new password> ");*/
            c.getPlayer().dropMessage(5, "@npc < Universal Town Warp / Event NPC>");
            c.getPlayer().dropMessage(5, "@dcash < Universal Cash Item Dropper >");
            /*if (!GameConstants.GMS) {
           
            c.getPlayer().dropMessage(5, "@challenge < playername, or accept/decline or block/unblock >");
            }*/
            c.getPlayer().dropMessage(5, "@tsmega < Toggle super megaphone on/off >");
            c.getPlayer().dropMessage(5, "@ea < If you are unable to attack or talk to NPC >");
            /*c.getPlayer().dropMessage(5, "@clearslot < Cleanup that trash in your inventory >");*/
            c.getPlayer().dropMessage(5, "@ranking < Use @ranking for more details >");
            c.getPlayer().dropMessage(5, "@checkdrop < Use @checkdrop for more details >");
            return 1;
        }
    }

    public static class TradeHelp extends TradeExecute {

        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().dropMessage(-2, "[System] : <@offerequip, @offeruse, @offersetup, @offeretc, @offercash> <quantity> <name of the item>");
            return 1;
        }
    }

    public abstract static class OfferCommand extends TradeExecute {

        protected int invType = -1;

        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 3) {
                c.getPlayer().dropMessage(-2, "[Error] : <quantity> <name of item>");
            } else if (c.getPlayer().getLevel() < 70) {
                c.getPlayer().dropMessage(-2, "[Error] : Only level 70+ may use this command");
            } else {
                int quantity = 1;
                try {
                    quantity = Integer.parseInt(splitted[1]);
                } catch (Exception e) { //swallow and just use 1
                }
                String search = StringUtil.joinStringFrom(splitted, 2).toLowerCase();
                Item found = null;
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                for (Item inv : c.getPlayer().getInventory(MapleInventoryType.getByType((byte) invType))) {
                    if (ii.getName(inv.getItemId()) != null && ii.getName(inv.getItemId()).toLowerCase().contains(search)) {
                        found = inv;
                        break;
                    }
                }
                if (found == null) {
                    c.getPlayer().dropMessage(-2, "[Error] : No such item was found (" + search + ")");
                    return 0;
                }
                if (GameConstants.isPet(found.getItemId()) || GameConstants.isRechargable(found.getItemId())) {
                    c.getPlayer().dropMessage(-2, "[Error] : You may not trade this item using this command");
                    return 0;
                }
                if (quantity > found.getQuantity() || quantity <= 0 || quantity > ii.getSlotMax(found.getItemId())) {
                    c.getPlayer().dropMessage(-2, "[Error] : Invalid quantity");
                    return 0;
                }
                if (!c.getPlayer().getTrade().setItems(c, found, (byte) -1, quantity)) {
                    c.getPlayer().dropMessage(-2, "[Error] : This item could not be placed");
                    return 0;
                } else {
                    c.getPlayer().getTrade().chatAuto("[System] : " + c.getPlayer().getName() + " offered " + ii.getName(found.getItemId()) + " x " + quantity);
                }
            }
            return 1;
        }
    }

    public static class OfferEquip extends OfferCommand {

        public OfferEquip() {
            invType = 1;
        }
    }

    public static class OfferUse extends OfferCommand {

        public OfferUse() {
            invType = 2;
        }
    }

    public static class OfferSetup extends OfferCommand {

        public OfferSetup() {
            invType = 3;
        }
    }

    public static class OfferEtc extends OfferCommand {

        public OfferEtc() {
            invType = 4;
        }
    }

    public static class OfferCash extends OfferCommand {

        public OfferCash() {
            invType = 5;
        }
    }

}
