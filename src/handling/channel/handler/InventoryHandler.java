/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package handling.channel.handler;

import java.util.Map;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.awt.Point;

import client.inventory.Equip;
import client.inventory.Equip.ScrollResult;
import client.inventory.Item;
import client.Skill;
import client.inventory.ItemFlag;
import client.inventory.MaplePet;
import client.inventory.MaplePet.PetFlag;
import client.inventory.MapleMount;
import client.MapleCharacter;
import client.MapleCharacterUtil;
import client.MapleClient;
import client.MapleDisease;
import client.MapleQuestStatus;
import client.inventory.MapleInventoryType;
import client.inventory.MapleInventory;
import client.MapleStat;
import client.MapleTrait.MapleTraitType;
import client.MonsterFamiliar;
import client.PlayerStats;
import client.SkillEntry;
import constants.GameConstants;
import client.SkillFactory;
import client.anticheat.CheatingOffense;
import database.DatabaseConnection;
import handling.channel.ChannelServer;
import handling.world.MaplePartyCharacter;
import handling.world.World;
import java.awt.Rectangle;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import server.Randomizer;
import server.RandomRewards;
import server.MapleShopFactory;
import server.MapleStatEffect;
import server.MapleItemInformationProvider;
import server.MapleInventoryManipulator;
import server.StructRewardItem;
import server.quest.MapleQuest;
import server.maps.SavedLocationType;
import server.maps.FieldLimitType;
import server.maps.MapleMap;
import server.maps.MapleMapItem;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.life.MapleMonster;
import server.life.MapleLifeFactory;
import scripting.NPCScriptManager;
import server.StructFamiliar;
import server.StructItemOption;
import server.events.MapleEvent;
import server.events.MapleEventType;
import server.maps.MapleMist;
import server.shops.HiredMerchant;
import server.shops.IMaplePlayerShop;
import tools.FileoutputUtil;
import tools.Pair;
import tools.packet.MTSCSPacket;
import tools.packet.PetPacket;
import tools.data.LittleEndianAccessor;
import tools.packet.CField.EffectPacket;
import tools.packet.CField;
import tools.packet.CWvsContext;
import tools.packet.CWvsContext.InfoPacket;
import tools.packet.CWvsContext.InventoryPacket;
import tools.packet.MobPacket;
import tools.packet.PlayerShopPacket;

public class InventoryHandler {

    private static InventoryHandlerAction action = null;

    public static final void ItemMove(final LittleEndianAccessor slea, final MapleClient c) {
        if (c.getPlayer().hasBlockedInventory()) { // hack
            return;
        }
        c.getPlayer().setScrolledPosition((short) 0);
        c.getPlayer().updateTick(slea.readInt());
        final MapleInventoryType type = MapleInventoryType.getByType(slea.readByte()); // 04
        final short src = slea.readShort(); // 01 00
        final short dst = slea.readShort(); // 00 00
        final short quantity = slea.readShort(); // 53 01

        if (src < 0 && dst > 0) {
            MapleInventoryManipulator.unequip(c, src, dst);
        } else if (dst < 0) {
            MapleInventoryManipulator.equip(c, src, dst);
        } else if (dst == 0) {
            MapleInventoryManipulator.drop(c, type, src, quantity);
        } else {
            MapleInventoryManipulator.move(c, type, src, dst);
        }
    }

    public static final void SwitchBag(final LittleEndianAccessor slea, final MapleClient c) {
        if (c.getPlayer().hasBlockedInventory()) { // hack
            return;
        }
        c.getPlayer().setScrolledPosition((short) 0);
        c.getPlayer().updateTick(slea.readInt());
        final short src = (short) slea.readInt(); // 01 00
        final short dst = (short) slea.readInt(); // 00 00
        if (src < 100 || dst < 100) {
            return;
        }
        MapleInventoryManipulator.move(c, MapleInventoryType.ETC, src, dst);
    }

    public static final void MoveBag(final LittleEndianAccessor slea, final MapleClient c) {
        if (c.getPlayer().hasBlockedInventory()) { // hack
            return;
        }
        c.getPlayer().setScrolledPosition((short) 0);
        c.getPlayer().updateTick(slea.readInt());
        final boolean srcFirst = slea.readInt() > 0;
        short dst = (short) slea.readInt(); // 01 00
        if (slea.readByte() != 4) { // must be etc) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        short src = slea.readShort(); // 00 00
        MapleInventoryManipulator.move(c, MapleInventoryType.ETC, srcFirst ? dst : src, srcFirst ? src : dst);
    }

    public static final void ItemSort(final LittleEndianAccessor slea, final MapleClient c) {
        c.getPlayer().updateTick(slea.readInt());
        c.getPlayer().setScrolledPosition((short) 0);
        final MapleInventoryType pInvType = MapleInventoryType.getByType(slea.readByte());
        if (pInvType == MapleInventoryType.UNDEFINED || c.getPlayer().hasBlockedInventory()) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        final MapleInventory pInv = c.getPlayer().getInventory(pInvType); // Mode should correspond with
        // MapleInventoryType
        boolean sorted = false;

        while (!sorted) {
            final byte freeSlot = (byte) pInv.getNextFreeSlot();
            if (freeSlot != -1) {
                byte itemSlot = -1;
                for (byte i = (byte) (freeSlot + 1); i <= pInv.getSlotLimit(); i++) {
                    if (pInv.getItem(i) != null) {
                        itemSlot = i;
                        break;
                    }
                }
                if (itemSlot > 0) {
                    MapleInventoryManipulator.move(c, pInvType, itemSlot, freeSlot);
                } else {
                    sorted = true;
                }
            } else {
                sorted = true;
            }
        }
        c.getSession().write(CWvsContext.finishedSort(pInvType.getType()));
        c.getSession().write(CWvsContext.enableActions());
    }

    public static final void ItemGather(final LittleEndianAccessor slea, final MapleClient c) {
        // [41 00] [E5 1D 55 00] [01]
        // [32 00] [01] [01] // Sent after

        c.getPlayer().updateTick(slea.readInt());
        c.getPlayer().setScrolledPosition((short) 0);
        if (c.getPlayer().hasBlockedInventory()) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        final byte mode = slea.readByte();
        final MapleInventoryType invType = MapleInventoryType.getByType(mode);
        MapleInventory Inv = c.getPlayer().getInventory(invType);

        final List<Item> itemMap = new LinkedList<>();
        Inv.list().forEach(item -> {
            itemMap.add(item.copy()); // clone all items T___T.
        });
        itemMap.forEach(itemStats -> {
            MapleInventoryManipulator.removeFromSlot(c, invType, itemStats.getPosition(), itemStats.getQuantity(), true,
                    false);
        });

        final List<Item> sortedItems = sortItems(itemMap);
        sortedItems.forEach(item -> {
            MapleInventoryManipulator.addFromDrop(c, item, false);
        });
        c.getSession().write(CWvsContext.finishedGather(mode));
        c.getSession().write(CWvsContext.enableActions());
        itemMap.clear();
        sortedItems.clear();
    }

    private static List<Item> sortItems(final List<Item> passedMap) {
        final List<Integer> itemIds = new ArrayList<>(); // empty list.
        passedMap.forEach(item -> {
            itemIds.add(item.getItemId()); // adds all item ids to the empty list to be sorted.
        });
        Collections.sort(itemIds); // sorts item ids

        final List<Item> sortedList = new LinkedList<>(); // ordered list pl0x <3.

        for (Integer val : itemIds) {
            for (Item item : passedMap) {
                if (val == item.getItemId()) { // Goes through every index and finds the first value that matches
                    sortedList.add(item);
                    passedMap.remove(item);
                    break;
                }
            }
        }
        return sortedList;
    }

    public static final void UseItem(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (chr == null || !chr.isAlive() || chr.getMapId() == 749040100 || chr.getMap() == null
                || chr.hasDisease(MapleDisease.POTION) || chr.hasBlockedInventory() || chr.inPVP()) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        final long time = System.currentTimeMillis();
        if (chr.getNextConsume() > time) {
            chr.dropMessage(5, "You may not use this item yet.");
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        c.getPlayer().updateTick(slea.readInt());
        final byte slot = (byte) slea.readShort();
        final int itemId = slea.readInt();
        final Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);

        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        if (!FieldLimitType.PotionUse.check(chr.getMap().getFieldLimit())) { // cwk quick hack
            if (MapleItemInformationProvider.getInstance().getItemEffect(toUse.getItemId()).applyTo(chr)) {
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
                if (chr.getMap().getConsumeItemCoolTime() > 0) {
                    chr.setNextConsume(time + (chr.getMap().getConsumeItemCoolTime() * 1000));
                }
            }

        } else {
            c.getSession().write(CWvsContext.enableActions());
        }
    }

    public static final void UseCosmetic(final LittleEndianAccessor slea, final MapleClient c,
            final MapleCharacter chr) {
        if (chr == null || !chr.isAlive() || chr.getMap() == null || chr.hasBlockedInventory() || chr.inPVP()) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        final byte slot = (byte) slea.readShort();
        final int itemId = slea.readInt();
        final Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);

        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId || itemId / 10000 != 254
                || (itemId / 1000) % 10 != chr.getGender()) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        if (MapleItemInformationProvider.getInstance().getItemEffect(toUse.getItemId()).applyTo(chr)) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
        }
    }

    public static final void UseReturnScroll(final LittleEndianAccessor slea, final MapleClient c,
            final MapleCharacter chr) {
        if (!chr.isAlive() || chr.getMapId() == 749040100 || chr.hasBlockedInventory() || chr.isInBlockedMap()
                || chr.inPVP()) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        c.getPlayer().updateTick(slea.readInt());
        final byte slot = (byte) slea.readShort();
        final int itemId = slea.readInt();
        final Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);

        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }

        if (!FieldLimitType.PotionUse.check(chr.getMap().getFieldLimit())) {
            if (MapleItemInformationProvider.getInstance().getItemEffect(toUse.getItemId()).applyReturnScroll(chr)) {
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
            } else {
                c.getSession().write(CWvsContext.enableActions());
            }
        } else {
            c.getSession().write(CWvsContext.enableActions());
        }
    }

    public static final void UseAlienSocket(final LittleEndianAccessor slea, final MapleClient c) {
        c.getPlayer().updateTick(slea.readInt());
        c.getPlayer().setScrolledPosition((short) 0);
        final Item alienSocket = c.getPlayer().getInventory(MapleInventoryType.USE).getItem((byte) slea.readShort());
        final int alienSocketId = slea.readInt();
        final Item toMount = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte) slea.readShort());
        if (alienSocket == null || alienSocketId != alienSocket.getItemId() || toMount == null
                || c.getPlayer().hasBlockedInventory()) {
            c.getSession().write(InventoryPacket.getInventoryFull());
            return;
        }
        // Can only use once-> 2nd and 3rd must use NPC.
        final Equip eqq = (Equip) toMount;
        if (eqq.getSocketState() != 0) { // Used before
            c.getPlayer().dropMessage(1, "This item already has a socket.");
        } else {
            eqq.setSocket1(0); // First socket, GMS removed the other 2
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, alienSocket.getPosition(), (short) 1,
                    false);
            c.getPlayer().forceReAddItem(toMount, MapleInventoryType.EQUIP);
        }
        c.getSession().write(MTSCSPacket.useAlienSocket(true));
    }

    public static final void UseNebulite(final LittleEndianAccessor slea, final MapleClient c) {
        c.getPlayer().updateTick(slea.readInt());
        c.getPlayer().setScrolledPosition((short) 0);
        final Item nebulite = c.getPlayer().getInventory(MapleInventoryType.SETUP).getItem((byte) slea.readShort());
        final int nebuliteId = slea.readInt();
        final Item toMount = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte) slea.readShort());
        if (nebulite == null || nebuliteId != nebulite.getItemId() || toMount == null
                || c.getPlayer().hasBlockedInventory()) {
            c.getSession().write(InventoryPacket.getInventoryFull());
            return;
        }
        final Equip eqq = (Equip) toMount;
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        boolean success = false;
        if (eqq.getSocket1() == 0/* || eqq.getSocket2() == 0 || eqq.getSocket3() == 0 */) { // GMS removed 2nd and 3rd
            // sockets, we can put into
            // npc.
            final StructItemOption pot = ii.getSocketInfo(nebuliteId);
            if (pot != null && GameConstants.optionTypeFits(pot.optionType, eqq.getItemId())) {
                // if (eqq.getSocket1() == 0) { // priority comes first
                eqq.setSocket1(pot.opID);
                // }// else if (eqq.getSocket2() == 0) {
                // eqq.setSocket2(pot.opID);
                // } else if (eqq.getSocket3() == 0) {
                // eqq.setSocket3(pot.opID);
                // }
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.SETUP, nebulite.getPosition(), (short) 1,
                        false);
                c.getPlayer().forceReAddItem(toMount, MapleInventoryType.EQUIP);
                success = true;
            }
        }
        c.getPlayer().getMap().broadcastMessage(CField.showNebuliteEffect(c.getPlayer().getId(), success));
        c.getSession().write(CWvsContext.enableActions());
    }

    public static final void UseNebuliteFusion(final LittleEndianAccessor slea, final MapleClient c) {
        c.getPlayer().updateTick(slea.readInt());
        c.getPlayer().setScrolledPosition((short) 0);
        final int nebuliteId1 = slea.readInt();
        final Item nebulite1 = c.getPlayer().getInventory(MapleInventoryType.SETUP).getItem((byte) slea.readShort());
        final int nebuliteId2 = slea.readInt();
        final Item nebulite2 = c.getPlayer().getInventory(MapleInventoryType.SETUP).getItem((byte) slea.readShort());
        final int mesos = slea.readInt();
        final int premiumQuantity = slea.readInt();
        if (nebulite1 == null || nebulite2 == null || nebuliteId1 != nebulite1.getItemId()
                || nebuliteId2 != nebulite2.getItemId() || (mesos == 0 && premiumQuantity == 0)
                || (mesos != 0 && premiumQuantity != 0) || mesos < 0 || premiumQuantity < 0
                || c.getPlayer().hasBlockedInventory()) {
            c.getPlayer().dropMessage(1, "Failed to fuse Nebulite.");
            c.getSession().write(InventoryPacket.getInventoryFull());
            return;
        }
        final int grade1 = GameConstants.getNebuliteGrade(nebuliteId1);
        final int grade2 = GameConstants.getNebuliteGrade(nebuliteId2);
        final int highestRank = grade1 > grade2 ? grade1 : grade2;
        if (grade1 == -1 || grade2 == -1 || (highestRank == 3 && premiumQuantity != 2)
                || (highestRank == 2 && premiumQuantity != 1) || (highestRank == 1 && mesos != 5000)
                || (highestRank == 0 && mesos != 3000) || (mesos > 0 && c.getPlayer().getMeso() < mesos)
                || (premiumQuantity > 0 && c.getPlayer().getItemQuantity(4420000, false) < premiumQuantity)
                || grade1 >= 4 || grade2 >= 4
                || (c.getPlayer().getInventory(MapleInventoryType.SETUP).getNumFreeSlot() < 1)) { // 4000 + = S, 3000 +
            // = A, 2000 + = B,
            // 1000 + = C, else =
            // D
            c.getSession().write(CField.useNebuliteFusion(c.getPlayer().getId(), 0, false));
            return; // Most of them were done in client, so we just send the unsuccessfull packet,
            // as it is only here when they packet edit.
        }
        final int avg = (grade1 + grade2) / 2; // have to revise more about grades.
        final int rank = Randomizer.nextInt(100) < 4
                ? (Randomizer.nextInt(100) < 70 ? (avg != 3 ? (avg + 1) : avg) : (avg != 0 ? (avg - 1) : 0))
                : avg;
        // 4 % chance to up/down 1 grade, (70% to up, 30% to down), cannot up to S
        // grade. =)
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final List<StructItemOption> pots = new LinkedList<>(ii.getAllSocketInfo(rank).values());
        int newId = 0;
        while (newId == 0) {
            StructItemOption pot = pots.get(Randomizer.nextInt(pots.size()));
            if (pot != null) {
                newId = pot.opID;
            }
        }
        if (mesos > 0) {
            c.getPlayer().gainMeso(-mesos, true);
        } else if (premiumQuantity > 0) {
            MapleInventoryManipulator.removeById(c, MapleInventoryType.ETC, 4420000, premiumQuantity, false, false);
        }
        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.SETUP, nebulite1.getPosition(), (short) 1,
                false);
        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.SETUP, nebulite2.getPosition(), (short) 1,
                false);
        MapleInventoryManipulator.addById(c, newId, (short) 1,
                "Fused from " + nebuliteId1 + " and " + nebuliteId2 + " on " + FileoutputUtil.CurrentReadable_Date());
        c.getSession().write(CField.useNebuliteFusion(c.getPlayer().getId(), newId, true));
    }

    public static final void UseMagnify(final LittleEndianAccessor slea, final MapleClient c) {
        c.getPlayer().updateTick(slea.readInt());
        c.getPlayer().setScrolledPosition((short) 0);
        final byte src = (byte) slea.readShort();
        final boolean insight = src == 127 && c.getPlayer().getTrait(MapleTraitType.sense).getLevel() >= 30;
        final Item magnify = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(src);
        final Item toReveal = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte) slea.readShort());
        if ((magnify == null && !insight) || toReveal == null || c.getPlayer().hasBlockedInventory()) {
            c.getSession().write(InventoryPacket.getInventoryFull());
            return;
        }
        final Equip eqq = (Equip) toReveal;
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final int reqLevel = ii.getReqLevel(eqq.getItemId()) / 10;
        if (eqq.getState() == 1
                && (insight || magnify.getItemId() == 2460003 || (magnify.getItemId() == 2460002 && reqLevel <= 12)
                || (magnify.getItemId() == 2460001 && reqLevel <= 7)
                || (magnify.getItemId() == 2460000 && reqLevel <= 3))) {
            final List<List<StructItemOption>> pots = new LinkedList<>(ii.getAllPotentialInfo().values());
            int new_state = Math.abs(eqq.getPotential1());
            if (new_state > 20 || new_state < 17) { // incase overflow
                new_state = 17;
            }
            int lines = 2; // default
            if (eqq.getPotential2() != 0) {
                lines++;
            }
            if (eqq.getPotential3() != 0) {
                lines++;
            }
            if (eqq.getPotential4() != 0) {
                lines++;
            }
            while (eqq.getState() != new_state) {
                // 31001 = haste, 31002 = door, 31003 = se, 31004 = hb, 41005 = combat orders,
                // 41006 = advanced blessing, 41007 = speed infusion
                for (int i = 0; i < lines; i++) { // minimum 2 lines, max 5
                    boolean rewarded = false;
                    while (!rewarded) {
                        StructItemOption pot = pots.get(Randomizer.nextInt(pots.size())).get(reqLevel);
                        if (pot != null && pot.reqLevel / 10 <= reqLevel
                                && GameConstants.optionTypeFits(pot.optionType, eqq.getItemId())
                                && GameConstants.potentialIDFits(pot.opID, new_state, i)) { // optionType
                            // have to research optionType before making this truely official-like
                            switch (i) {
                                case 0 ->
                                    eqq.setPotential1(pot.opID);
                                case 1 ->
                                    eqq.setPotential2(pot.opID);
                                case 2 ->
                                    eqq.setPotential3(pot.opID);
                                case 3 ->
                                    eqq.setPotential4(pot.opID);
                                case 4 ->
                                    eqq.setPotential5(pot.opID);
                                default -> {
                                }
                            }
                            rewarded = true;
                        }
                    }
                }
            }
            c.getPlayer().getTrait(MapleTraitType.insight)
                    .addExp((insight ? 10 : ((magnify.getItemId() + 2) - 2460000)) * 2, c.getPlayer());
            c.getPlayer().getMap()
                    .broadcastMessage(CField.showMagnifyingEffect(c.getPlayer().getId(), eqq.getPosition()));
            if (!insight) {
                c.getSession().write(InventoryPacket.scrolledItem(magnify, toReveal, false, true));
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, magnify.getPosition(), (short) 1,
                        false);
            } else {
                c.getPlayer().forceReAddItem(toReveal, MapleInventoryType.EQUIP);
            }
            c.getSession().write(CWvsContext.enableActions());
        } else {
            c.getSession().write(InventoryPacket.getInventoryFull());
        }
    }

    public static final void addToScrollLog(int accountID, int charID, int scrollID, int itemID, byte oldSlots,
            byte newSlots, byte viciousHammer, String result, boolean ws, boolean ls, int vega) {
        try {
            try (PreparedStatement ps = DatabaseConnection.getConnection()
                    .prepareStatement("INSERT INTO scroll_log VALUES(DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                ps.setInt(1, accountID);
                ps.setInt(2, charID);
                ps.setInt(3, scrollID);
                ps.setInt(4, itemID);
                ps.setByte(5, oldSlots);
                ps.setByte(6, newSlots);
                ps.setByte(7, viciousHammer);
                ps.setString(8, result);
                ps.setByte(9, (byte) (ws ? 1 : 0));
                ps.setByte(10, (byte) (ls ? 1 : 0));
                ps.setInt(11, vega);
                ps.execute();
            }
        } catch (SQLException e) {
            FileoutputUtil.outputFileError(FileoutputUtil.PacketEx_Log, e);
        }
    }

    public static final void UseCatchItem(final LittleEndianAccessor slea, final MapleClient c,
            final MapleCharacter chr) {
        c.getPlayer().updateTick(slea.readInt());
        c.getPlayer().setScrolledPosition((short) 0);
        final byte slot = (byte) slea.readShort();
        final int itemid = slea.readInt();
        final MapleMonster mob = chr.getMap().getMonsterByOid(slea.readInt());
        final Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);
        final MapleMap map = chr.getMap();

        if (toUse != null && toUse.getQuantity() > 0 && toUse.getItemId() == itemid && mob != null
                && !chr.hasBlockedInventory() && itemid / 10000 == 227
                && MapleItemInformationProvider.getInstance().getCardMobId(itemid) == mob.getId()) {
            if (!MapleItemInformationProvider.getInstance().isMobHP(itemid) || mob.getHp() <= mob.getMobMaxHp() / 2) {
                map.broadcastMessage(MobPacket.catchMonster(mob.getObjectId(), itemid, (byte) 1));
                map.killMonster(mob, chr, true, false, (byte) 1);
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false, false);
                if (MapleItemInformationProvider.getInstance().getCreateId(itemid) > 0) {
                    MapleInventoryManipulator.addById(c, MapleItemInformationProvider.getInstance().getCreateId(itemid),
                            (short) 1, "Catch item " + itemid + " on " + FileoutputUtil.CurrentReadable_Date());
                }
            } else {
                map.broadcastMessage(MobPacket.catchMonster(mob.getObjectId(), itemid, (byte) 0));
                c.getSession().write(CWvsContext.catchMob(mob.getId(), itemid, (byte) 0));
            }
        }
        c.getSession().write(CWvsContext.enableActions());
    }

    public static final void UseMountFood(final LittleEndianAccessor slea, final MapleClient c,
            final MapleCharacter chr) {
        c.getPlayer().updateTick(slea.readInt());
        final byte slot = (byte) slea.readShort();
        final int itemid = slea.readInt(); // 2260000 usually
        final Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);
        final MapleMount mount = chr.getMount();

        if (itemid / 10000 == 226 && toUse != null && toUse.getQuantity() > 0 && toUse.getItemId() == itemid
                && mount != null && !c.getPlayer().hasBlockedInventory()) {
            final int fatigue = mount.getFatigue();

            boolean levelup = false;
            mount.setFatigue((byte) -30);

            if (fatigue > 0) {
                mount.increaseExp();
                final int level = mount.getLevel();
                if (level < 30 && mount.getExp() >= GameConstants.getMountExpNeededForLevel(level + 1)) {
                    mount.setLevel((byte) (level + 1));
                    levelup = true;
                }
            }
            chr.getMap().broadcastMessage(CWvsContext.updateMount(chr, levelup));
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
        }
        c.getSession().write(CWvsContext.enableActions());
    }

    public static final void UseScriptedNPCItem(final LittleEndianAccessor slea, final MapleClient c,
            final MapleCharacter chr) {
        c.getPlayer().updateTick(slea.readInt());
        final byte slot = (byte) slea.readShort();
        final int itemId = slea.readInt();
        final Item toUse = chr.getInventory(GameConstants.getInventoryType(itemId)).getItem(slot);
        long expiration_days = 0;
        int mountid = 0;

        if (toUse != null && toUse.getQuantity() >= 1 && toUse.getItemId() == itemId && !chr.hasBlockedInventory()
                && !chr.inPVP()) {
            switch (toUse.getItemId()) {
                case 2430007 -> { // Blank Compass
                    final MapleInventory inventory = chr.getInventory(MapleInventoryType.SETUP);
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);

                    if (inventory.countById(3994102) >= 20 // Compass Letter "North"
                            && inventory.countById(3994103) >= 20 // Compass Letter "South"
                            && inventory.countById(3994104) >= 20 // Compass Letter "East"
                            && inventory.countById(3994105) >= 20) { // Compass Letter "West"
                        MapleInventoryManipulator.addById(c, 2430008, (short) 1,
                                "Scripted item: " + itemId + " on " + FileoutputUtil.CurrentReadable_Date()); // Gold
                        // Compass
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.SETUP, 3994102, 20, false, false);
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.SETUP, 3994103, 20, false, false);
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.SETUP, 3994104, 20, false, false);
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.SETUP, 3994105, 20, false, false);
                    } else {
                        MapleInventoryManipulator.addById(c, 2430007, (short) 1,
                                "Scripted item: " + itemId + " on " + FileoutputUtil.CurrentReadable_Date()); // Blank
                        // Compass
                    }
                    NPCScriptManager.getInstance().start(c, 2084001);
                }
                case 2430008 -> { // Gold Compass
                    chr.saveLocation(SavedLocationType.RICHIE);
                    MapleMap map;
                    boolean warped = false;

                    for (int i = 390001000; i <= 390001004; i++) {
                        map = c.getChannelServer().getMapFactory().getMap(i);

                        if (map.getCharactersSize() == 0) {
                            chr.changeMap(map, map.getPortal(0));
                            warped = true;
                            break;
                        }
                    }
                    if (warped) { // Removal of gold compass
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                    } else { // Or mabe some other message.
                        c.getPlayer().dropMessage(5, "All maps are currently in use, please try again later.");
                    }
                }
                case 2430112 -> {
                    // miracle cube fragment
                    if (c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() >= 1) {
                        if (c.getPlayer().getInventory(MapleInventoryType.USE).countById(2430112) >= 25) {
                            if (MapleInventoryManipulator.checkSpace(c, 2049400, 1, "") && MapleInventoryManipulator
                                    .removeById(c, MapleInventoryType.USE, toUse.getItemId(), 25, true, false)) {
                                MapleInventoryManipulator.addById(c, 2049400, (short) 1, "Scripted item: "
                                        + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date());
                            } else {
                                c.getPlayer().dropMessage(5, "Please make some space.");
                            }
                        } else if (c.getPlayer().getInventory(MapleInventoryType.USE).countById(2430112) >= 10) {
                            if (MapleInventoryManipulator.checkSpace(c, 2049400, 1, "") && MapleInventoryManipulator
                                    .removeById(c, MapleInventoryType.USE, toUse.getItemId(), 10, true, false)) {
                                MapleInventoryManipulator.addById(c, 2049401, (short) 1, "Scripted item: "
                                        + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date());
                            } else {
                                c.getPlayer().dropMessage(5, "Please make some space.");
                            }
                        } else {
                            c.getPlayer().dropMessage(5,
                                    "There needs to be 10 Fragments for a Potential Scroll, 25 for Advanced Potential Scroll.");
                        }
                    } else {
                        c.getPlayer().dropMessage(5, "Please make some space.");
                    }
                }
                case 2430481 -> {
                    // super miracle cube fragment
                    if (c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() >= 1) {
                        if (c.getPlayer().getInventory(MapleInventoryType.USE).countById(2430481) >= 30) {
                            if (MapleInventoryManipulator.checkSpace(c, 2049701, 1, "") && MapleInventoryManipulator
                                    .removeById(c, MapleInventoryType.USE, toUse.getItemId(), 30, true, false)) {
                                MapleInventoryManipulator.addById(c, 2049701, (short) 1, "Scripted item: "
                                        + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date());
                            } else {
                                c.getPlayer().dropMessage(5, "Please make some space.");
                            }
                        } else if (c.getPlayer().getInventory(MapleInventoryType.USE).countById(2430481) >= 20) {
                            if (MapleInventoryManipulator.checkSpace(c, 2049300, 1, "") && MapleInventoryManipulator
                                    .removeById(c, MapleInventoryType.USE, toUse.getItemId(), 20, true, false)) {
                                MapleInventoryManipulator.addById(c, 2049300, (short) 1, "Scripted item: "
                                        + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date());
                            } else {
                                c.getPlayer().dropMessage(5, "Please make some space.");
                            }
                        } else {
                            c.getPlayer().dropMessage(5,
                                    "There needs to be 20 Fragments for a Advanced Equip Enhancement Scroll, 30 for Epic Potential Scroll 80%.");
                        }
                    } else {
                        c.getPlayer().dropMessage(5, "Please make some space.");
                    }
                }
                case 2430691 -> {
                    // nebulite diffuser fragment
                    if (c.getPlayer().getInventory(MapleInventoryType.CASH).getNumFreeSlot() >= 1) {
                        if (c.getPlayer().getInventory(MapleInventoryType.USE).countById(2430691) >= 10) {
                            if (MapleInventoryManipulator.checkSpace(c, 5750001, 1, "") && MapleInventoryManipulator
                                    .removeById(c, MapleInventoryType.USE, toUse.getItemId(), 10, true, false)) {
                                MapleInventoryManipulator.addById(c, 5750001, (short) 1, "Scripted item: "
                                        + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date());
                            } else {
                                c.getPlayer().dropMessage(5, "Please make some space.");
                            }
                        } else {
                            c.getPlayer().dropMessage(5, "There needs to be 10 Fragments for a Nebulite Diffuser.");
                        }
                    } else {
                        c.getPlayer().dropMessage(5, "Please make some space.");
                    }
                }
                case 2430748 -> {
                    // premium fusion ticket
                    if (c.getPlayer().getInventory(MapleInventoryType.ETC).getNumFreeSlot() >= 1) {
                        if (c.getPlayer().getInventory(MapleInventoryType.USE).countById(2430748) >= 20) {
                            if (MapleInventoryManipulator.checkSpace(c, 4420000, 1, "") && MapleInventoryManipulator
                                    .removeById(c, MapleInventoryType.USE, toUse.getItemId(), 20, true, false)) {
                                MapleInventoryManipulator.addById(c, 4420000, (short) 1, "Scripted item: "
                                        + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date());
                            } else {
                                c.getPlayer().dropMessage(5, "Please make some space.");
                            }
                        } else {
                            c.getPlayer().dropMessage(5, "There needs to be 20 Fragments for a Premium Fusion Ticket.");
                        }
                    } else {
                        c.getPlayer().dropMessage(5, "Please make some space.");
                    }
                }
                case 2430692 -> {
                    // nebulite box
                    if (c.getPlayer().getInventory(MapleInventoryType.SETUP).getNumFreeSlot() >= 1) {
                        if (c.getPlayer().getInventory(MapleInventoryType.USE).countById(2430692) >= 1) {
                            final int rank = Randomizer.nextInt(100) < 30 ? (Randomizer.nextInt(100) < 4 ? 2 : 1) : 0;
                            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                            final List<StructItemOption> pots = new LinkedList<>(ii.getAllSocketInfo(rank).values());
                            int newId = 0;
                            while (newId == 0) {
                                StructItemOption pot = pots.get(Randomizer.nextInt(pots.size()));
                                if (pot != null) {
                                    newId = pot.opID;
                                }
                            }
                            if (MapleInventoryManipulator.checkSpace(c, newId, 1, "") && MapleInventoryManipulator
                                    .removeById(c, MapleInventoryType.USE, toUse.getItemId(), 1, true, false)) {
                                MapleInventoryManipulator.addById(c, newId, (short) 1, "Scripted item: " + toUse.getItemId()
                                        + " on " + FileoutputUtil.CurrentReadable_Date());
                                c.getSession().write(InfoPacket.getShowItemGain(newId, (short) 1, true));
                            } else {
                                c.getPlayer().dropMessage(5, "Please make some space.");
                            }
                        } else {
                            c.getPlayer().dropMessage(5, "You do not have a Nebulite Box.");
                        }
                    } else {
                        c.getPlayer().dropMessage(5, "Please make some space.");
                    }
                }
                case 5680019 -> {// starling hair
                    // if (c.getPlayer().getGender() == 1) {
                    int hair = 32150 + (c.getPlayer().getHair() % 10);
                    c.getPlayer().setHair(hair);
                    c.getPlayer().updateSingleStat(MapleStat.HAIR, hair);
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.CASH, slot, (byte) 1, false);
                    // }
                }
                case 5680020 -> {// starling hair
                    // if (c.getPlayer().getGender() == 0) {
                    int hair = 32160 + (c.getPlayer().getHair() % 10);
                    c.getPlayer().setHair(hair);
                    c.getPlayer().updateSingleStat(MapleStat.HAIR, hair);
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.CASH, slot, (byte) 1, false);
                    // }
                }
                case 3994225 ->
                    c.getPlayer().dropMessage(5, "Please bring this item to the NPC.");
                case 2430212 -> {
                    // energy drink
                    MapleQuestStatus marr = c.getPlayer().getQuestNAdd(MapleQuest.getInstance(GameConstants.ENERGY_DRINK));
                    if (marr.getCustomData() == null) {
                        marr.setCustomData("0");
                    }
                    long lastTime = Long.parseLong(marr.getCustomData());
                    if (lastTime + (600000) > System.currentTimeMillis()) {
                        c.getPlayer().dropMessage(5, "You can only use one energy drink per 10 minutes.");
                    } else if (c.getPlayer().getFatigue() > 0) {
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                        c.getPlayer().setFatigue(c.getPlayer().getFatigue() - 5);
                    }
                }
                case 2430213 -> {
                    // energy drink
                    MapleQuestStatus marr = c.getPlayer().getQuestNAdd(MapleQuest.getInstance(GameConstants.ENERGY_DRINK));
                    if (marr.getCustomData() == null) {
                        marr.setCustomData("0");
                    }
                    long lastTime = Long.parseLong(marr.getCustomData());
                    if (lastTime + (600000) > System.currentTimeMillis()) {
                        c.getPlayer().dropMessage(5, "You can only use one energy drink per 10 minutes.");
                    } else if (c.getPlayer().getFatigue() > 0) {
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                        c.getPlayer().setFatigue(c.getPlayer().getFatigue() - 10);
                    }
                }
                case 2430220, 2430214 -> // energy drink
                {
                    // energy drink
                    if (c.getPlayer().getFatigue() > 0) {
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                        c.getPlayer().setFatigue(c.getPlayer().getFatigue() - 30);
                    }
                }
                case 2430227 -> {
                    // energy drink
                    if (c.getPlayer().getFatigue() > 0) {
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                        c.getPlayer().setFatigue(c.getPlayer().getFatigue() - 50);
                    }
                }
                case 2430231 -> {
                    // energy drink
                    MapleQuestStatus marr = c.getPlayer().getQuestNAdd(MapleQuest.getInstance(GameConstants.ENERGY_DRINK));
                    if (marr.getCustomData() == null) {
                        marr.setCustomData("0");
                    }
                    long lastTime = Long.parseLong(marr.getCustomData());
                    if (lastTime + (600000) > System.currentTimeMillis()) {
                        c.getPlayer().dropMessage(5, "You can only use one energy drink per 10 minutes.");
                    } else if (c.getPlayer().getFatigue() > 0) {
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                        c.getPlayer().setFatigue(c.getPlayer().getFatigue() - 40);
                    }
                }
                case 2430144 -> {
                    // smb
                    final int itemid = Randomizer.nextInt(373) + 2290000;
                    if (MapleItemInformationProvider.getInstance().itemExists(itemid)
                            && !MapleItemInformationProvider.getInstance().getName(itemid).contains("Special")
                            && !MapleItemInformationProvider.getInstance().getName(itemid).contains("Event")) {
                        MapleInventoryManipulator.addById(c, itemid, (short) 1,
                                "Reward item: " + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date());
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                    }
                }
                case 2430370 -> {
                    if (MapleInventoryManipulator.checkSpace(c, 2028062, (short) 1, "")) {
                        MapleInventoryManipulator.addById(c, 2028062, (short) 1,
                                "Reward item: " + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date());
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                    }
                }
                case 2430158 -> {
                    // lion king
                    if (c.getPlayer().getInventory(MapleInventoryType.ETC).getNumFreeSlot() >= 1) {
                        if (c.getPlayer().getInventory(MapleInventoryType.ETC).countById(4000630) >= 100) {
                            if (MapleInventoryManipulator.checkSpace(c, 4310010, 1, "") && MapleInventoryManipulator
                                    .removeById(c, MapleInventoryType.USE, toUse.getItemId(), 1, true, false)) {
                                MapleInventoryManipulator.addById(c, 4310010, (short) 1, "Scripted item: "
                                        + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date());
                            } else {
                                c.getPlayer().dropMessage(5, "Please make some space.");
                            }
                        } else if (c.getPlayer().getInventory(MapleInventoryType.ETC).countById(4000630) >= 50) {
                            if (MapleInventoryManipulator.checkSpace(c, 4310009, 1, "") && MapleInventoryManipulator
                                    .removeById(c, MapleInventoryType.USE, toUse.getItemId(), 1, true, false)) {
                                MapleInventoryManipulator.addById(c, 4310009, (short) 1, "Scripted item: "
                                        + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date());
                            } else {
                                c.getPlayer().dropMessage(5, "Please make some space.");
                            }
                        } else {
                            c.getPlayer().dropMessage(5,
                                    "There needs to be 50 Purification Totems for a Noble Lion King Medal, 100 for Royal Lion King Medal.");
                        }
                    } else {
                        c.getPlayer().dropMessage(5, "Please make some space.");
                    }
                }
                case 2430159 -> {
                    MapleQuest.getInstance(3182).forceComplete(c.getPlayer(), 2161004);
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                }
                case 2430200 -> {
                    // thunder stone
                    if (c.getPlayer().getQuestStatus(31152) != 2) {
                        c.getPlayer().dropMessage(5, "You have no idea how to use it.");
                    } else {
                        if (c.getPlayer().getInventory(MapleInventoryType.ETC).getNumFreeSlot() >= 1) {
                            if (c.getPlayer().getInventory(MapleInventoryType.ETC).countById(4000660) >= 1
                                    && c.getPlayer().getInventory(MapleInventoryType.ETC).countById(4000661) >= 1
                                    && c.getPlayer().getInventory(MapleInventoryType.ETC).countById(4000662) >= 1
                                    && c.getPlayer().getInventory(MapleInventoryType.ETC).countById(4000663) >= 1) {
                                if (MapleInventoryManipulator.checkSpace(c, 4032923, 1, "")
                                        && MapleInventoryManipulator.removeById(c, MapleInventoryType.USE,
                                                toUse.getItemId(), 1, true, false)
                                        && MapleInventoryManipulator.removeById(c, MapleInventoryType.ETC, 4000660, 1, true,
                                                false)
                                        && MapleInventoryManipulator.removeById(c, MapleInventoryType.ETC, 4000661, 1, true,
                                                false)
                                        && MapleInventoryManipulator.removeById(c, MapleInventoryType.ETC, 4000662, 1, true,
                                                false)
                                        && MapleInventoryManipulator.removeById(c, MapleInventoryType.ETC, 4000663, 1, true,
                                                false)) {
                                    MapleInventoryManipulator.addById(c, 4032923, (short) 1, "Scripted item: "
                                            + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date());
                                } else {
                                    c.getPlayer().dropMessage(5, "Please make some space.");
                                }
                            } else {
                                c.getPlayer().dropMessage(5, "There needs to be 1 of each Stone for a Dream Key.");
                            }
                        } else {
                            c.getPlayer().dropMessage(5, "Please make some space.");
                        }
                    }
                }
                case 2430130, 2430131 -> {
                    // energy charge
                    if (GameConstants.isResist(c.getPlayer().getJob())) {
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                        c.getPlayer().gainExp(
                                Math.round(20000 + (c.getPlayer().getLevel() * 50 * c.getChannelServer().getExpRate())),
                                true, true, false);
                    } else {
                        c.getPlayer().dropMessage(5, "You may not use this item.");
                    }
                }
                case 2430132, 2430133, 2430134, 2430142 -> {
                    if (c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() >= 1) {
                        switch (c.getPlayer().getJob()) {
                            case 3200:
                            case 3210:
                            case 3211:
                            case 3212:
                                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                                MapleInventoryManipulator.addById(c, 1382101, (short) 1,
                                        "Scripted item: " + itemId + " on " + FileoutputUtil.CurrentReadable_Date());
                                break;
                            case 3300:
                            case 3310:
                            case 3311:
                            case 3312:
                                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                                MapleInventoryManipulator.addById(c, 1462093, (short) 1,
                                        "Scripted item: " + itemId + " on " + FileoutputUtil.CurrentReadable_Date());
                                break;
                            case 3500:
                            case 3510:
                            case 3511:
                            case 3512:
                                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                                MapleInventoryManipulator.addById(c, 1492080, (short) 1,
                                        "Scripted item: " + itemId + " on " + FileoutputUtil.CurrentReadable_Date());
                                break;
                            default:
                                c.getPlayer().dropMessage(5, "You may not use this item.");
                                break;
                        }
                    } else {
                        c.getPlayer().dropMessage(5, "Make some space.");
                    }
                }
                case 2430036 -> {
                    // croco 1 day
                    mountid = 1027;
                    expiration_days = 1;
                }
                case 2430170 -> {
                    // croco 7 day
                    mountid = 1027;
                    expiration_days = 7;
                }
                case 2430037 -> {
                    // black scooter 1 day
                    mountid = 1028;
                    expiration_days = 1;
                }
                case 2430038 -> // resistance box
                {
                    // pink scooter 1 day
                    mountid = 1029;
                    expiration_days = 1;
                }
                case 2430039 -> {
                    // clouds 1 day
                    mountid = 1030;
                    expiration_days = 1;
                }
                case 2430040 -> {
                    // balrog 1 day
                    mountid = 1031;
                    expiration_days = 1;
                }
                case 2430223 -> {
                    // balrog 1 day
                    mountid = 1031;
                    expiration_days = 15;
                }
                case 2430259 -> {
                    // balrog 1 day
                    mountid = 1031;
                    expiration_days = 3;
                }
                case 2430242 -> {
                    // motorcycle
                    mountid = 80001018;
                    expiration_days = 10;
                }
                case 2430243 -> {
                    // power suit
                    mountid = 80001019;
                    expiration_days = 10;
                }
                case 2430261 -> {
                    // power suit
                    mountid = 80001019;
                    expiration_days = 3;
                }
                case 2430249 -> {
                    // motorcycle
                    mountid = 80001027;
                    expiration_days = 3;
                }
                case 2430225 -> {
                    // balrog 1 day
                    mountid = 1031;
                    expiration_days = 10;
                }
                case 2430053 -> {
                    // croco 30 day
                    mountid = 1027;
                    expiration_days = 1;
                }
                case 2430054 -> {
                    // black scooter 30 day
                    mountid = 1028;
                    expiration_days = 30;
                }
                case 2430055 -> {
                    // pink scooter 30 day
                    mountid = 1029;
                    expiration_days = 30;
                }
                case 2430257 -> {
                    // pink
                    mountid = 1029;
                    expiration_days = 7;
                }
                case 2430056 -> {
                    // mist rog 30 day
                    mountid = 1035;
                    expiration_days = 30;
                }
                case 2430057 -> {
                    mountid = 1033;
                    expiration_days = 30;
                }
                case 2430072 -> {
                    // ZD tiger 7 day
                    mountid = 1034;
                    expiration_days = 7;
                }
                case 2430073 -> {
                    // lion 15 day
                    mountid = 1036;
                    expiration_days = 15;
                }
                case 2430074 -> {
                    // unicorn 15 day
                    mountid = 1037;
                    expiration_days = 15;
                }
                case 2430272 -> {
                    // low rider 15 day
                    mountid = 1038;
                    expiration_days = 3;
                }
                case 2430275 -> {
                    // spiegelmann
                    mountid = 80001033;
                    expiration_days = 7;
                }
                case 2430075 -> {
                    // low rider 15 day
                    mountid = 1038;
                    expiration_days = 15;
                }
                case 2430076 -> {
                    // red truck 15 day
                    mountid = 1039;
                    expiration_days = 15;
                }
                case 2430077 -> {
                    // gargoyle 15 day
                    mountid = 1040;
                    expiration_days = 15;
                }
                case 2430080 -> {
                    // shinjo 20 day
                    mountid = 1042;
                    expiration_days = 20;
                }
                case 2430082 -> {
                    // orange mush 7 day
                    mountid = 1044;
                    expiration_days = 7;
                }
                case 2430260 -> {
                    // orange mush 7 day
                    mountid = 1044;
                    expiration_days = 3;
                }
                case 2430091 -> {
                    // nightmare 10 day
                    mountid = 1049;
                    expiration_days = 10;
                }
                case 2430092 -> {
                    // yeti 10 day
                    mountid = 1050;
                    expiration_days = 10;
                }
                case 2430263 -> {
                    // yeti 10 day
                    mountid = 1050;
                    expiration_days = 3;
                }
                case 2430093 -> {
                    // ostrich 10 day
                    mountid = 1051;
                    expiration_days = 10;
                }
                case 2430101 -> {
                    // pink bear 10 day
                    mountid = 1052;
                    expiration_days = 10;
                }
                case 2430102 -> {
                    // transformation robo 10 day
                    mountid = 1053;
                    expiration_days = 10;
                }
                case 2430103 -> {
                    // chicken 30 day
                    mountid = 1054;
                    expiration_days = 30;
                }
                case 2430266 -> {
                    // chicken 30 day
                    mountid = 1054;
                    expiration_days = 3;
                }
                case 2430265 -> {
                    // chariot
                    mountid = 1151;
                    expiration_days = 3;
                }
                case 2430258 -> {
                    // law officer
                    mountid = 1115;
                    expiration_days = 365;
                }
                case 2430117 -> {
                    // lion 1 year
                    mountid = 1036;
                    expiration_days = 365;
                }
                case 2430118 -> {
                    // red truck 1 year
                    mountid = 1039;
                    expiration_days = 365;
                }
                case 2430119 -> {
                    // gargoyle 1 year
                    mountid = 1040;
                    expiration_days = 365;
                }
                case 2430120 -> {
                    // unicorn 1 year
                    mountid = 1037;
                    expiration_days = 365;
                }
                case 2430271 -> {
                    // owl 30 day
                    mountid = 1069;
                    expiration_days = 3;
                }
                case 2430136 -> {
                    // owl 30 day
                    mountid = 1069;
                    expiration_days = 30;
                }
                case 2430137 -> {
                    // owl 1 year
                    mountid = 1069;
                    expiration_days = 365;
                }
                case 2430145 -> {
                    // mothership
                    mountid = 1070;
                    expiration_days = 30;
                }
                case 2430146 -> {
                    // mothership
                    mountid = 1070;
                    expiration_days = 365;
                }
                case 2430147 -> {
                    // mothership
                    mountid = 1071;
                    expiration_days = 30;
                }
                case 2430148 -> {
                    // mothership
                    mountid = 1071;
                    expiration_days = 365;
                }
                case 2430135 -> {
                    // os4
                    mountid = 1065;
                    expiration_days = 15;
                }
                case 2430149 -> {
                    // leonardo 30 day
                    mountid = 1072;
                    expiration_days = 30;
                }
                case 2430262 -> {
                    // leonardo 30 day
                    mountid = 1072;
                    expiration_days = 3;
                }
                case 2430179 -> {
                    // witch 15 day
                    mountid = 1081;
                    expiration_days = 15;
                }
                case 2430264 -> {
                    // witch 15 day
                    mountid = 1081;
                    expiration_days = 3;
                }
                case 2430201 -> {
                    // giant bunny 60 day
                    mountid = 1096;
                    expiration_days = 60;
                }
                case 2430228 -> {
                    // tiny bunny 60 day
                    mountid = 1101;
                    expiration_days = 60;
                }
                case 2430276 -> {
                    // tiny bunny 60 day
                    mountid = 1101;
                    expiration_days = 15;
                }
                case 2430277 -> {
                    // tiny bunny 60 day
                    mountid = 1101;
                    expiration_days = 365;
                }
                case 2430283 -> {
                    // trojan
                    mountid = 1025;
                    expiration_days = 10;
                }
                case 2430291 -> {
                    // hot air
                    mountid = 1145;
                    expiration_days = -1;
                }
                case 2430293 -> {
                    // nadeshiko
                    mountid = 1146;
                    expiration_days = -1;
                }
                case 2430295 -> {
                    // pegasus
                    mountid = 1147;
                    expiration_days = -1;
                }
                case 2430297 -> {
                    // dragon
                    mountid = 1148;
                    expiration_days = -1;
                }
                case 2430299 -> {
                    // broom
                    mountid = 1149;
                    expiration_days = -1;
                }
                case 2430301 -> {
                    // cloud
                    mountid = 1150;
                    expiration_days = -1;
                }
                case 2430303 -> {
                    // chariot
                    mountid = 1151;
                    expiration_days = -1;
                }
                case 2430305 -> {
                    // nightmare
                    mountid = 1152;
                    expiration_days = -1;
                }
                case 2430307 -> {
                    // rog
                    mountid = 1153;
                    expiration_days = -1;
                }
                case 2430309 -> {
                    // mist rog
                    mountid = 1154;
                    expiration_days = -1;
                }
                case 2430311 -> {
                    // owl
                    mountid = 1156;
                    expiration_days = -1;
                }
                case 2430313 -> {
                    // helicopter
                    mountid = 1156;
                    expiration_days = -1;
                }
                case 2430315 -> {
                    // pentacle
                    mountid = 1118;
                    expiration_days = -1;
                }
                case 2430317 -> {
                    // frog
                    mountid = 1121;
                    expiration_days = -1;
                }
                case 2430319 -> {
                    // turtle
                    mountid = 1122;
                    expiration_days = -1;
                }
                case 2430321 -> {
                    // buffalo
                    mountid = 1123;
                    expiration_days = -1;
                }
                case 2430323 -> {
                    // tank
                    mountid = 1124;
                    expiration_days = -1;
                }
                case 2430325 -> {
                    // viking
                    mountid = 1129;
                    expiration_days = -1;
                }
                case 2430327 -> {
                    // pachinko
                    mountid = 1130;
                    expiration_days = -1;
                }
                case 2430329 -> {
                    // kurenai
                    mountid = 1063;
                    expiration_days = -1;
                }
                case 2430331 -> {
                    // horse
                    mountid = 1025;
                    expiration_days = -1;
                }
                case 2430333 -> {
                    // tiger
                    mountid = 1034;
                    expiration_days = -1;
                }
                case 2430335 -> {
                    // hyena
                    mountid = 1136;
                    expiration_days = -1;
                }
                case 2430337 -> {
                    // ostrich
                    mountid = 1051;
                    expiration_days = -1;
                }
                case 2430339 -> {
                    // low rider
                    mountid = 1138;
                    expiration_days = -1;
                }
                case 2430341 -> {
                    // napoleon
                    mountid = 1139;
                    expiration_days = -1;
                }
                case 2430343 -> {
                    // croking
                    mountid = 1027;
                    expiration_days = -1;
                }
                case 2430346 -> {
                    // lovely
                    mountid = 1029;
                    expiration_days = -1;
                }
                case 2430348 -> {
                    // retro
                    mountid = 1028;
                    expiration_days = -1;
                }
                case 2430350 -> {
                    // f1
                    mountid = 1033;
                    expiration_days = -1;
                }
                case 2430352 -> {
                    // power suit
                    mountid = 1064;
                    expiration_days = -1;
                }
                case 2430354 -> {
                    // giant rabbit
                    mountid = 1096;
                    expiration_days = -1;
                }
                case 2430356 -> {
                    // small rabit
                    mountid = 1101;
                    expiration_days = -1;
                }
                case 2430358 -> {
                    // rabbit rickshaw
                    mountid = 1102;
                    expiration_days = -1;
                }
                case 2430360 -> {
                    // chicken
                    mountid = 1054;
                    expiration_days = -1;
                }
                case 2430362 -> {
                    // transformer
                    mountid = 1053;
                    expiration_days = -1;
                }
                case 2430292 -> {
                    // hot air
                    mountid = 1145;
                    expiration_days = 90;
                }
                case 2430294 -> {
                    // nadeshiko
                    mountid = 1146;
                    expiration_days = 90;
                }
                case 2430296 -> {
                    // pegasus
                    mountid = 1147;
                    expiration_days = 90;
                }
                case 2430298 -> {
                    // dragon
                    mountid = 1148;
                    expiration_days = 90;
                }
                case 2430300 -> {
                    // broom
                    mountid = 1149;
                    expiration_days = 90;
                }
                case 2430302 -> {
                    // cloud
                    mountid = 1150;
                    expiration_days = 90;
                }
                case 2430304 -> {
                    // chariot
                    mountid = 1151;
                    expiration_days = 90;
                }
                case 2430306 -> {
                    // nightmare
                    mountid = 1152;
                    expiration_days = 90;
                }
                case 2430308 -> {
                    // rog
                    mountid = 1153;
                    expiration_days = 90;
                }
                case 2430310 -> {
                    // mist rog
                    mountid = 1154;
                    expiration_days = 90;
                }
                case 2430312 -> {
                    // owl
                    mountid = 1156;
                    expiration_days = 90;
                }
                case 2430314 -> {
                    // helicopter
                    mountid = 1156;
                    expiration_days = 90;
                }
                case 2430316 -> {
                    // pentacle
                    mountid = 1118;
                    expiration_days = 90;
                }
                case 2430318 -> {
                    // frog
                    mountid = 1121;
                    expiration_days = 90;
                }
                case 2430320 -> {
                    // turtle
                    mountid = 1122;
                    expiration_days = 90;
                }
                case 2430322 -> {
                    // buffalo
                    mountid = 1123;
                    expiration_days = 90;
                }
                case 2430326 -> {
                    // viking
                    mountid = 1129;
                    expiration_days = 90;
                }
                case 2430328 -> {
                    // pachinko
                    mountid = 1130;
                    expiration_days = 90;
                }
                case 2430330 -> {
                    // kurenai
                    mountid = 1063;
                    expiration_days = 90;
                }
                case 2430332 -> {
                    // horse
                    mountid = 1025;
                    expiration_days = 90;
                }
                case 2430334 -> {
                    // tiger
                    mountid = 1034;
                    expiration_days = 90;
                }
                case 2430336 -> {
                    // hyena
                    mountid = 1136;
                    expiration_days = 90;
                }
                case 2430338 -> {
                    // ostrich
                    mountid = 1051;
                    expiration_days = 90;
                }
                case 2430340 -> {
                    // low rider
                    mountid = 1138;
                    expiration_days = 90;
                }
                case 2430342 -> {
                    // napoleon
                    mountid = 1139;
                    expiration_days = 90;
                }
                case 2430344 -> {
                    // croking
                    mountid = 1027;
                    expiration_days = 90;
                }
                case 2430347 -> {
                    // lovely
                    mountid = 1029;
                    expiration_days = 90;
                }
                case 2430349 -> {
                    // retro
                    mountid = 1028;
                    expiration_days = 90;
                }
                case 2430351 -> {
                    // f1
                    mountid = 1033;
                    expiration_days = 90;
                }
                case 2430353 -> {
                    // power suit
                    mountid = 1064;
                    expiration_days = 90;
                }
                case 2430355 -> {
                    // giant rabbit
                    mountid = 1096;
                    expiration_days = 90;
                }
                case 2430357 -> {
                    // small rabit
                    mountid = 1101;
                    expiration_days = 90;
                }
                case 2430359 -> {
                    // rabbit rickshaw
                    mountid = 1102;
                    expiration_days = 90;
                }
                case 2430361 -> {
                    // chicken
                    mountid = 1054;
                    expiration_days = 90;
                }
                case 2430363 -> {
                    // transformer
                    mountid = 1053;
                    expiration_days = 90;
                }
                case 2430324 -> {
                    // high way
                    mountid = 1158;
                    expiration_days = -1;
                }
                case 2430345 -> {
                    // high way
                    mountid = 1158;
                    expiration_days = 90;
                }
                case 2430367 -> {
                    // law off
                    mountid = 1115;
                    expiration_days = 3;
                }
                case 2430365 -> {
                    // pony
                    mountid = 1025;
                    expiration_days = 365;
                }
                case 2430366 -> {
                    // pony
                    mountid = 1025;
                    expiration_days = 15;
                }
                case 2430369 -> {
                    // nightmare
                    mountid = 1049;
                    expiration_days = 10;
                }
                case 2430392 -> {
                    // speedy
                    mountid = 80001038;
                    expiration_days = 90;
                }
                case 2430476 -> {
                    // red truck? but name is pegasus?
                    mountid = 1039;
                    expiration_days = 15;
                }
                case 2430477 -> {
                    // red truck? but name is pegasus?
                    mountid = 1039;
                    expiration_days = 365;
                }
                case 2430232 -> {
                    // fortune
                    mountid = 1106;
                    expiration_days = 10;
                }
                case 2430511 -> {
                    // spiegel
                    mountid = 80001033;
                    expiration_days = 15;
                }
                case 2430512 -> {
                    // rspiegel
                    mountid = 80001033;
                    expiration_days = 365;
                }
                case 2430536 -> {
                    // buddy buggy
                    mountid = 80001114;
                    expiration_days = 365;
                }
                case 2430537 -> {
                    // buddy buggy
                    mountid = 80001114;
                    expiration_days = 15;
                }
                case 2430229 -> {
                    // bunny rickshaw 60 day
                    mountid = 1102;
                    expiration_days = 60;
                }
                case 2430199 -> {
                    // santa sled
                    mountid = 1102;
                    expiration_days = 60;
                }
                case 2430206 -> {
                    // race
                    mountid = 1089;
                    expiration_days = 7;
                }
                case 2430211 -> {
                    // race
                    mountid = 80001009;
                    expiration_days = 30;
                }
                default ->
                    System.out.println("New scripted item : " + toUse.getItemId());
            }
            // energy drink
            // resistance box
        }
        if (mountid > 0) {
            mountid = PlayerStats.getSkillByJob(mountid, c.getPlayer().getJob());
            final int fk = GameConstants.getMountItem(mountid, c.getPlayer());
            if (GameConstants.GMS && fk > 0 && mountid < 80001000) { // TODO JUMP
                for (int i = 80001001; i < 80001999; i++) {
                    final Skill skill = SkillFactory.getSkill(i);
                    if (skill != null && GameConstants.getMountItem(skill.getId(), c.getPlayer()) == fk) {
                        mountid = i;
                        break;
                    }
                }
            }
            if (c.getPlayer().getSkillLevel(mountid) > 0) {
                c.getPlayer().dropMessage(5, "You already have this skill.");
            } else if (SkillFactory.getSkill(mountid) == null
                    || GameConstants.getMountItem(mountid, c.getPlayer()) == 0) {
                c.getPlayer().dropMessage(5, "The skill could not be gained.");
            } else if (expiration_days > 0) {
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                c.getPlayer().changeSingleSkillLevel(SkillFactory.getSkill(mountid), (byte) 1, (byte) 1,
                        System.currentTimeMillis() + (long) (expiration_days * 24 * 60 * 60 * 1000));
                c.getPlayer().dropMessage(5, "The skill has been attained.");
            }
        }
        c.getSession().write(CWvsContext.enableActions());
    }

    public static final void UseSummonBag(final LittleEndianAccessor slea, final MapleClient c,
            final MapleCharacter chr) {
        if (!chr.isAlive() || chr.hasBlockedInventory() || chr.inPVP()) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        c.getPlayer().updateTick(slea.readInt());
        final byte slot = (byte) slea.readShort();
        final int itemId = slea.readInt();
        final Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);

        if (toUse != null && toUse.getQuantity() >= 1 && toUse.getItemId() == itemId
                && (c.getPlayer().getMapId() < 910000000 || c.getPlayer().getMapId() > 910000022)) {
            final Map<String, Integer> toSpawn = MapleItemInformationProvider.getInstance().getEquipStats(itemId);

            if (toSpawn == null) {
                c.getSession().write(CWvsContext.enableActions());
                return;
            }
            MapleMonster ht = null;
            int type = 0;
            for (Entry<String, Integer> i : toSpawn.entrySet()) {
                if (i.getKey().startsWith("mob") && Randomizer.nextInt(99) <= i.getValue()) {
                    ht = MapleLifeFactory.getMonster(Integer.parseInt(i.getKey().substring(3)));
                    chr.getMap().spawnMonster_sSack(ht, chr.getPosition(), type);
                }
            }
            if (ht == null) {
                c.getSession().write(CWvsContext.enableActions());
                return;
            }

            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
        }
        c.getSession().write(CWvsContext.enableActions());
    }

    public static final void UseTreasureChest(final LittleEndianAccessor slea, final MapleClient c,
            final MapleCharacter chr) {
        final short slot = slea.readShort();
        final int itemid = slea.readInt();

        final Item toUse = chr.getInventory(MapleInventoryType.ETC).getItem((byte) slot);
        if (toUse == null || toUse.getQuantity() <= 0 || toUse.getItemId() != itemid || chr.hasBlockedInventory()) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        int reward;
        int keyIDforRemoval = 0;
        String box;

        switch (toUse.getItemId()) {
            case 4280000 -> {
                // Gold box
                reward = RandomRewards.getGoldBoxReward();
                keyIDforRemoval = 5490000;
                box = "Gold";
            }
            case 4280001 -> {
                // Silver box
                reward = RandomRewards.getSilverBoxReward();
                keyIDforRemoval = 5490001;
                box = "Silver";
            }
            default -> {
                // Up to no good
                return;
            }
        }

        // Get the quantity
        int amount = 1;
        switch (reward) {
            case 2000004:
                amount = 200; // Elixir
                break;
            case 2000005:
                amount = 100; // Power Elixir
                break;
        }
        if (chr.getInventory(MapleInventoryType.CASH).countById(keyIDforRemoval) > 0) {
            final Item item = MapleInventoryManipulator.addbyId_Gachapon(c, reward, (short) amount);

            if (item == null) {
                chr.dropMessage(5,
                        "Please check your item inventory and see if you have a Master Key, or if the inventory is full.");
                c.getSession().write(CWvsContext.enableActions());
                return;
            }
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.ETC, (byte) slot, (short) 1, true);
            MapleInventoryManipulator.removeById(c, MapleInventoryType.CASH, keyIDforRemoval, 1, true, false);
            c.getSession().write(InfoPacket.getShowItemGain(reward, (short) amount, true));

            if (GameConstants.gachaponRareItem(item.getItemId()) > 0) {
                World.Broadcast.broadcastSmega(CWvsContext.getGachaponMega(c.getPlayer().getName(), " : got a(n)", item,
                        (byte) 2, "[" + box + " Chest]"));
            }
        } else {
            chr.dropMessage(5,
                    "Please check your item inventory and see if you have a Master Key, or if the inventory is full.");
            c.getSession().write(CWvsContext.enableActions());
        }
    }

    public static final void UseCashItem(final LittleEndianAccessor slea, final MapleClient c) {
        if (c.getPlayer() == null || c.getPlayer().getMap() == null || c.getPlayer().inPVP()) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        c.getPlayer().updateTick(slea.readInt());
        c.getPlayer().setScrolledPosition((short) 0);
        final byte slot = (byte) slea.readShort();
        final int itemId = slea.readInt();

        final Item toUse = c.getPlayer().getInventory(MapleInventoryType.CASH).getItem(slot);
        if (toUse == null || toUse.getItemId() != itemId || toUse.getQuantity() < 1
                || c.getPlayer().hasBlockedInventory()) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }

        boolean used = false, cc = false;
        action = new InventoryHandlerAction(slea, c, itemId);
        switch (itemId) {

            case 5043001, 5043000 -> // NPC Teleport Rock
            {
                used = action.NpcTeleportRock();
            }
            case 5040004, 5040002, 2320000, 5040000 -> { // Teleport Coke
                used = action.UseTeleRock();
            }
            case 5450005 -> {
                used = action.sendStorage();
            }
            case 5050000 -> {
                used = action.ApReset();
            }
            case 5220083 -> {
                used = action.StarterPack();
            }
            case 5220084 -> {
                used = action.BoosterPack();
            }
            case 5050001, 5050002, 5050003, 5050004, 5050005, 5050006, 5050007, 5050008, 5050009 -> {
                used = action.SpResetScroll();
            }
            case 5500000 -> // Magic Hourglass 1 day
            {
                used = action.HourGlass1();
            }
            case 5500001 -> // Magic Hourglass 7 day
            {
                used = action.HourGlass7();
            }
            case 5500002 -> { // Magic Hourglass 20 day
                used = action.HourGlass20();
            }
            case 5500005 -> { // Magic Hourglass 50 day
                used = action.HourGlass50();
            }
            case 5500006 -> { // Magic Hourglass 99 day
                used = action.HourGlass99();
            }
            case 5060000 -> {// Item Tag
                used = action.ItemTag();
            }
            case 5680015 -> {
                used = action.FatigueResetDrink();
            }
            case 5534000 -> // Tim's Secret Lab (PRovide potential)
            {
                used = action.TimsSecretLab(toUse);
            }
            case 5062000 -> // Miracle cube
            {
                used = action.MiracleCube(toUse);
            }
            case 5062100, 5062001 -> // 8th Anniversary Cube and Premium miracle cube
            {
                used = action.PremiumMiracleCube(toUse);
            }
            case 5062002 -> {
                used = action.SuperMiracleCube(toUse);
            }
            case 5750000 -> {
                used = action.AlienCube();
            }
            case 5750001 -> {
                used = action.NebuliteDiffuser(toUse);
            }
            case 5521000 -> {
                // Karma // THIS IS Sharing TAG... o_o
                /*
             * final MapleInventoryType type = MapleInventoryType.getByType((byte)
             * slea.readInt()); final Item item =
             * c.getPlayer().getInventory(type).getItem((byte) slea.readInt());
             * 
             * if (item != null && !ItemFlag.KARMA_ACC.check(item.getFlag()) &&
             * !ItemFlag.KARMA_ACC_USE.check(item.getFlag())) { if
             * (MapleItemInformationProvider.getInstance().isShareTagEnabled(item.getItemId(
             * ))) { short flag = item.getFlag(); if (ItemFlag.UNTRADEABLE.check(flag)) {
             * flag -= ItemFlag.UNTRADEABLE.getValue(); } else if (type ==
             * MapleInventoryType.EQUIP) { flag |= ItemFlag.KARMA_ACC.getValue(); } else {
             * flag |= ItemFlag.KARMA_ACC_USE.getValue(); } item.setFlag(flag);
             * c.getPlayer().forceReAddItem_NoUpdate(item, type);
             * c.getSession().write(InventoryPacket.updateSpecialItemUse(item,
             * type.getType(), item.getPosition(), true, c.getPlayer())); used = true; } }
                 */
                used = false;
            }
            case 5520001, 5520000 -> { // Platinium scissor of karma / scissor of karna
                used = action.ScissorOfKarma();
            }
            case 5570000 -> {
                used = action.ViciousHammer();
            }
            case 5610001, 5610000 -> { // Vega 60 - Vega 10
                used = action.VegaSpell();
                cc = used;

            }
            case 5060001 -> { // Sealing Lock
                used = action.ItemGuard();
            }
            case 5061000 -> { // Sealing Lock 7 days
                used = action.ItemGuard(System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000));
            }
            case 5061001 -> { // Sealing Lock 30 days
                used = action.ItemGuard(System.currentTimeMillis() + (30 * 24 * 60 * 60 * 1000));
            }
            case 5061002 -> { // Sealing Lock 90 days
                used = action.ItemGuard(System.currentTimeMillis() + (90 * 24 * 60 * 60 * 1000));
            }
            case 5061003 -> { // Sealing Lock 365 days
                used = action.ItemGuard(System.currentTimeMillis() + (365 * 24 * 60 * 60 * 1000));
            }
            case 5063000 -> {
                used = action.LucksKey();
            }
            case 5064000 -> {
                used = action.ShieldingWard();
            }
            case 5060004, 5060003 -> { // Microwave - peanut
                used = action.Microwave_Peanut();
            }
            case 5070000 -> { // Cheap megaphone isn't working :S
                used = action.CheapMegaphone();
            }
            case 5071000 -> { // Megaphone
                used = action.Megaphone();
            }
            case 5077000 -> { // 3 line Megaphone
                used = action.TripleMegaphone();
            }
            case 5079004 -> { // Heart Megaphone
                used = action.EchoMegaphone();
            }
            case 5073000 -> { // Heart Megaphone
                used = action.HeartMegaphone();
            }
            case 5074000 -> { // Skull Megaphone
                used = action.SkullMegaphone();
            }
            case 5072000 -> {
                used = action.SuperMegaphone();
            }
            case 5076000 -> { // Item Megaphone
                used = action.ItemMegaphone();
            }
            case 5075000, 5075001, 5075002 -> { // MapleTV Heart Messenger
                c.getPlayer().dropMessage(5, "There are no MapleTVs to broadcast the message to.");
                used = false;
            }
            case 5075003, 5075004, 5075005 -> {
                used = action.Megassenger();
            }
            case 5090100, 5090000 -> { // Note
                used = action.Note();
            }
            case 5100000 -> { // Congratulatory Song
                c.getPlayer().getMap().broadcastMessage(CField.musicChange("Jukebox/Congratulation"));
                used = true;
            }
            case 5190001, 5190002, 5190003, 5190004, 5190005, 5190006, 5190007, 5190008, 5190000 -> { // Pet Flags
                used = action.PetFlags();
            }
            case 5191001, 5191002, 5191003, 5191004, 5191000 -> { // Pet Flags
                used = action.PetFlag2();
            }
            case 5501001, 5501002 -> { // expiry mount
                used = action.KentasMagicRope();

            }
            case 5170000 -> { // Pet name change
                used = action.PetNameTag();

            }
            case 5700000 -> {
                used = action.AndroidNamingCoupon();
            }
            case 5240000, 5240001, 5240002, 5240003, 5240004, 5240005, 5240006, 5240007, 5240008, 5240009, 5240010, 5240011, 5240012, 5240013, 5240014, 5240015, 5240016, 5240017, 5240018, 5240019, 5240020, 5240021, 5240022, 5240023, 5240024, 5240025, 5240026, 5240027, 5240029, 5240030, 5240031, 5240032, 5240033, 5240034, 5240035, 5240036, 5240037, 5240038, 5240039, 5240040, 5240028 -> {
                used = action.PetSnacks();
            }
            case 5230001, 5230000 -> {
                // Rookie owl of minerva / Owl of Minerva
                used = action.OwlOfMinerva();
            }
            case 5281001, 5280001, 5281000 -> {// Floral Scent /[Not exist?] /Passed gas
                used = action.FloralScent_PassedGas();
            }

            case 5370001, 5370000 -> { // Chalkboard
                used = action.Chalkboard();
            }
            case 5079000, 5079001, 5390007, 5390008, 5390009, 5390000, 5390001, 5390002, 5390003, 5390004, 5390005, 5390006 -> {
                // Goal, soccer, Friend finder, Diablo, Cloud 9, Loveholic, cute tiger, roaring
                used = action.xMessenger();
            }
            case 5452001, 5450003, 5450000 -> { // Miu Miu The Rookie Travelling Merchant, Miu Miu The Travelling Merchant
                used = action.MiuMiuTravelingMerchant();
            }
            case 5300000, 5300001, 5300002 -> { // Cash morphs: Oinker, Zeta, Fungus
                used = action.CashMorphs();
            }
            case 5041000,5041001,5041002, 5041003,5041004,5041005,5041006,5041007, 5040001, 5040003, 5040006, 5040007, 5040008 -> {
                used = action.UseTeleRockDay();
            }
            default -> {
                used = action.DefaultActionForCashItem(slot);
            }
        }
        // NPC Teleport Rock
        // The Teleport Rock
        // VIP Teleport Rock
        // The Teleport Rock
        // SP Reset (1st job)
        // SP Reset (2nd job)
        // SP Reset (3rd job)
        // SP Reset (4th job)
        // evan sp resets
        // p.karma
        // MapleTV Messenger
        // MapleTV Star Messenger
        // Wedding Invitation Card
        // idk, but probably
        // Gas Skill
        // Diablo Messenger
        // Cloud 9 Messenger
        // Loveholic Messenger
        // New Year Megassenger 1
        // New Year Megassenger 2
        // Cute Tiger Messenger

        if (used) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.CASH, slot, (short) 1, false, true);
        } else {
            c.getPlayer().dropMessage(1,
                "This item cannot be used. Report this to an administrator:\r\nItemID: " + itemId);
        }
        c.getSession().write(CWvsContext.enableActions());
        if (cc) {
            if (!c.getPlayer().isAlive() || c.getPlayer().getEventInstance() != null
                    || FieldLimitType.ChannelSwitch.check(c.getPlayer().getMap().getFieldLimit())) {
                c.getPlayer().dropMessage(1, "Auto relog failed.");
                return;
            }
            c.getPlayer().dropMessage(5, "Auto relogging. Please wait.");
            c.getPlayer().fakeRelog();
            if (c.getPlayer().getScrolledPosition() != 0) {
                c.getSession().write(CWvsContext.pamSongUI());
            }
        }
    }

    public static final void Pickup_Player(final LittleEndianAccessor slea, MapleClient c, final MapleCharacter chr) {
        if (c.getPlayer().hasBlockedInventory()) { // hack
            return;
        }
        chr.updateTick(slea.readInt());
        c.getPlayer().setScrolledPosition((short) 0);
        slea.skip(1); // or is this before tick?
        final Point Client_Reportedpos = slea.readPos();
        if (chr == null || chr.getMap() == null) {
            return;
        }
        final MapleMapObject ob = chr.getMap().getMapObject(slea.readInt(), MapleMapObjectType.ITEM);

        if (ob == null) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        final MapleMapItem mapitem = (MapleMapItem) ob;
        final Lock lock = mapitem.getLock();
        lock.lock();
        try {
            if (mapitem.isPickedUp()) {
                c.getSession().write(CWvsContext.enableActions());
                return;
            }
            if (mapitem.getQuest() > 0 && chr.getQuestStatus(mapitem.getQuest()) != 1) {
                c.getSession().write(CWvsContext.enableActions());
                return;
            }
            if (mapitem.getOwner() != chr.getId() && ((!mapitem.isPlayerDrop() && mapitem.getDropType() == 0)
                    || (mapitem.isPlayerDrop() && chr.getMap().getEverlast()))) {
                c.getSession().write(CWvsContext.enableActions());
                return;
            }
            if (!mapitem.isPlayerDrop() && mapitem.getDropType() == 1 && mapitem.getOwner() != chr.getId()
                    && (chr.getParty() == null || chr.getParty().getMemberById(mapitem.getOwner()) == null)) {
                c.getSession().write(CWvsContext.enableActions());
                return;
            }
            final double Distance = Client_Reportedpos.distanceSq(mapitem.getPosition());
            if (Distance > 5000 && (mapitem.getMeso() > 0 || mapitem.getItemId() != 4001025)) {
                chr.getCheatTracker().registerOffense(CheatingOffense.ITEMVAC_CLIENT, String.valueOf(Distance));
            } else if (chr.getPosition().distanceSq(mapitem.getPosition()) > 640000.0) {
                chr.getCheatTracker().registerOffense(CheatingOffense.ITEMVAC_SERVER);
            }
            if (mapitem.getMeso() > 0) {
                if (chr.getParty() != null && mapitem.getOwner() != chr.getId()) {
                    final List<MapleCharacter> toGive = new LinkedList<MapleCharacter>();
                    final int splitMeso = mapitem.getMeso() * 40 / 100;
                    for (MaplePartyCharacter z : chr.getParty().getMembers()) {
                        MapleCharacter m = chr.getMap().getCharacterById(z.getId());
                        if (m != null && m.getId() != chr.getId()) {
                            toGive.add(m);
                        }
                    }
                    for (final MapleCharacter m : toGive) {
                        int mesos = splitMeso / toGive.size()
                                + (m.getStat().hasPartyBonus ? (int) (mapitem.getMeso() / 20.0) : 0);
                        if (mapitem.getDropper() instanceof MapleMonster && m.getStat().incMesoProp > 0) {
                            mesos += Math.floor((m.getStat().incMesoProp * mesos) / 100.0f);
                        }
                        m.gainMeso(mesos, true);
                    }
                    int mesos = mapitem.getMeso() - splitMeso;
                    if (mapitem.getDropper() instanceof MapleMonster && chr.getStat().incMesoProp > 0) {
                        mesos += Math.floor((chr.getStat().incMesoProp * mesos) / 100.0f);
                    }
                    chr.gainMeso(mesos, true);
                } else {
                    int mesos = mapitem.getMeso();
                    if (mapitem.getDropper() instanceof MapleMonster && chr.getStat().incMesoProp > 0) {
                        mesos += Math.floor((chr.getStat().incMesoProp * mesos) / 100.0f);
                    }
                    chr.gainMeso(mesos, true);
                }
                removeItem(chr, mapitem, ob);
            } else {
                if (MapleItemInformationProvider.getInstance().isPickupBlocked(mapitem.getItemId())) {
                    c.getSession().write(CWvsContext.enableActions());
                    c.getPlayer().dropMessage(5, "This item cannot be picked up.");
                } else if (c.getPlayer().inPVP() && Integer
                        .parseInt(c.getPlayer().getEventInstance().getProperty("ice")) == c.getPlayer().getId()) {
                    c.getSession().write(InventoryPacket.getInventoryFull());
                    c.getSession().write(InventoryPacket.getShowInventoryFull());
                    c.getSession().write(CWvsContext.enableActions());
                } else if (useItem(c, mapitem.getItemId())) {
                    removeItem(c.getPlayer(), mapitem, ob);
                    // another hack
                    if (mapitem.getItemId() / 10000 == 291) {
                        c.getPlayer().getMap().broadcastMessage(CField.getCapturePosition(c.getPlayer().getMap()));
                        c.getPlayer().getMap().broadcastMessage(CField.resetCapture());
                    }
                } else if (mapitem.getItemId() / 10000 != 291 && MapleInventoryManipulator.checkSpace(c,
                        mapitem.getItemId(), mapitem.getItem().getQuantity(), mapitem.getItem().getOwner())) {
                    if (mapitem.getItem().getQuantity() >= 50 && mapitem.getItemId() == 2340000) {
                        c.setMonitored(true); // hack check
                    }
                    MapleInventoryManipulator.addFromDrop(c, mapitem.getItem(), true,
                            mapitem.getDropper() instanceof MapleMonster);
                    removeItem(chr, mapitem, ob);
                } else {
                    c.getSession().write(InventoryPacket.getInventoryFull());
                    c.getSession().write(InventoryPacket.getShowInventoryFull());
                    c.getSession().write(CWvsContext.enableActions());
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public static final void Pickup_Pet(final LittleEndianAccessor slea, final MapleClient c,
            final MapleCharacter chr) {
        if (chr == null) {
            return;
        }
        if (c.getPlayer().hasBlockedInventory() || c.getPlayer().inPVP()) { // hack
            return;
        }
        c.getPlayer().setScrolledPosition((short) 0);
        final byte petz = (byte) (GameConstants.GMS ? (c.getPlayer().getPetIndex((int) slea.readLong()))
                : slea.readInt());
        final MaplePet pet = chr.getPet(petz);
        slea.skip(1); // [4] Zero, [4] Seems to be tickcount, [1] Always zero
        chr.updateTick(slea.readInt());
        final Point Client_Reportedpos = slea.readPos();
        final MapleMapObject ob = chr.getMap().getMapObject(slea.readInt(), MapleMapObjectType.ITEM);

        if (ob == null || pet == null) {
            return;
        }
        final MapleMapItem mapitem = (MapleMapItem) ob;
        final Lock lock = mapitem.getLock();
        lock.lock();
        try {
            if (mapitem.isPickedUp()) {
                c.getSession().write(InventoryPacket.getInventoryFull());
                return;
            }
            if (mapitem.getOwner() != chr.getId() && mapitem.isPlayerDrop()) {
                return;
            }
            if (mapitem.getOwner() != chr.getId() && ((!mapitem.isPlayerDrop() && mapitem.getDropType() == 0)
                    || (mapitem.isPlayerDrop() && chr.getMap().getEverlast()))) {
                c.getSession().write(CWvsContext.enableActions());
                return;
            }
            if (!mapitem.isPlayerDrop() && mapitem.getDropType() == 1 && mapitem.getOwner() != chr.getId()
                    && (chr.getParty() == null || chr.getParty().getMemberById(mapitem.getOwner()) == null)) {
                c.getSession().write(CWvsContext.enableActions());
                return;
            }
            final double Distance = Client_Reportedpos.distanceSq(mapitem.getPosition());
            if (Distance > 10000 && (mapitem.getMeso() > 0 || mapitem.getItemId() != 4001025)) {
                chr.getCheatTracker().registerOffense(CheatingOffense.PET_ITEMVAC_CLIENT, String.valueOf(Distance));
            } else if (pet.getPos().distanceSq(mapitem.getPosition()) > 640000.0) {
                chr.getCheatTracker().registerOffense(CheatingOffense.PET_ITEMVAC_SERVER);

            }

            if (mapitem.getMeso() > 0) {
                if (chr.getParty() != null && mapitem.getOwner() != chr.getId()) {
                    final List<MapleCharacter> toGive = new LinkedList<MapleCharacter>();
                    final int splitMeso = mapitem.getMeso() * 40 / 100;
                    for (MaplePartyCharacter z : chr.getParty().getMembers()) {
                        MapleCharacter m = chr.getMap().getCharacterById(z.getId());
                        if (m != null && m.getId() != chr.getId()) {
                            toGive.add(m);
                        }
                    }
                    for (final MapleCharacter m : toGive) {
                        m.gainMeso(splitMeso / toGive.size()
                                + (m.getStat().hasPartyBonus ? (int) (mapitem.getMeso() / 20.0) : 0), true);
                    }
                    chr.gainMeso(mapitem.getMeso() - splitMeso, true);
                } else {
                    chr.gainMeso(mapitem.getMeso(), true);
                }
                removeItem_Pet(chr, mapitem, petz);
            } else {
                if (MapleItemInformationProvider.getInstance().isPickupBlocked(mapitem.getItemId())
                        || mapitem.getItemId() / 10000 == 291) {
                    c.getSession().write(CWvsContext.enableActions());
                } else if (useItem(c, mapitem.getItemId())) {
                    removeItem_Pet(chr, mapitem, petz);
                } else if (MapleInventoryManipulator.checkSpace(c, mapitem.getItemId(), mapitem.getItem().getQuantity(),
                        mapitem.getItem().getOwner())) {
                    if (mapitem.getItem().getQuantity() >= 50 && mapitem.getItemId() == 2340000) {
                        c.setMonitored(true); // hack check
                    }
                    MapleInventoryManipulator.addFromDrop(c, mapitem.getItem(), true,
                            mapitem.getDropper() instanceof MapleMonster);
                    removeItem_Pet(chr, mapitem, petz);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public static final boolean useItem(final MapleClient c, final int id) {
        if (GameConstants.isUse(id)) { // TO prevent caching of everything, waste of mem
            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            final MapleStatEffect eff = ii.getItemEffect(id);
            if (eff == null) {
                return false;
            }
            // must hack here for ctf
            if (id / 10000 == 291) {
                boolean area = false;
                for (Rectangle rect : c.getPlayer().getMap().getAreas()) {
                    if (rect.contains(c.getPlayer().getTruePosition())) {
                        area = true;
                        break;
                    }
                }
                if (!c.getPlayer().inPVP() || (c.getPlayer().getTeam() == (id - 2910000) && area)) {
                    return false; // dont apply the consume
                }
            }
            final int consumeval = eff.getConsume();

            if (consumeval > 0) {
                consumeItem(c, eff);
                consumeItem(c, ii.getItemEffectEX(id));
                c.getSession().write(InfoPacket.getShowItemGain(id, (byte) 1));
                return true;
            }
        }
        return false;
    }

    public static final void consumeItem(final MapleClient c, final MapleStatEffect eff) {
        if (eff == null) {
            return;
        }
        if (eff.getConsume() == 2) {
            if (c.getPlayer().getParty() != null && c.getPlayer().isAlive()) {
                c.getPlayer().getParty().getMembers().stream()
                        .map(pc -> c.getPlayer().getMap().getCharacterById(pc.getId()))
                        .filter(chr -> (chr != null && chr.isAlive())).forEachOrdered(chr -> {
                    eff.applyTo(chr);
                });
            } else {
                eff.applyTo(c.getPlayer());
            }
        } else if (c.getPlayer().isAlive()) {
            eff.applyTo(c.getPlayer());
        }
    }

    public static final void removeItem_Pet(final MapleCharacter chr, final MapleMapItem mapitem, int pet) {
        mapitem.setPickedUp(true);
        chr.getMap().broadcastMessage(CField.removeItemFromMap(mapitem.getObjectId(), 5, chr.getId(), pet));
        chr.getMap().removeMapObject(mapitem);
        if (mapitem.isRandDrop()) {
            chr.getMap().spawnRandDrop();
        }
    }

    private static final void removeItem(final MapleCharacter chr, final MapleMapItem mapitem,
            final MapleMapObject ob) {
        mapitem.setPickedUp(true);
        chr.getMap().broadcastMessage(CField.removeItemFromMap(mapitem.getObjectId(), 2, chr.getId()),
                mapitem.getPosition());
        chr.getMap().removeMapObject(ob);
        if (mapitem.isRandDrop()) {
            chr.getMap().spawnRandDrop();
        }
    }

    public static final void OwlMinerva(final LittleEndianAccessor slea, final MapleClient c) {
        final byte slot = (byte) slea.readShort();
        final int itemid = slea.readInt();
        final Item toUse = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slot);
        if (toUse != null && toUse.getQuantity() > 0 && toUse.getItemId() == itemid && itemid == 2310000
                && !c.getPlayer().hasBlockedInventory()) {
            final int itemSearch = slea.readInt();
            final List<HiredMerchant> hms = c.getChannelServer().searchMerchant(itemSearch);
            if (hms.size() > 0) {
                c.getSession().write(CWvsContext.getOwlSearched(itemSearch, hms));
                MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, itemid, 1, true, false);
            } else {
                c.getPlayer().dropMessage(1, "Unable to find the item.");
            }
        }
        c.getSession().write(CWvsContext.enableActions());
    }

    public static final void Owl(final LittleEndianAccessor slea, final MapleClient c) {
        if (c.getPlayer().haveItem(5230000, 1, true, false) || c.getPlayer().haveItem(2310000, 1, true, false)) {
            if (c.getPlayer().getMapId() >= 910000000 && c.getPlayer().getMapId() <= 910000022) {
                c.getSession().write(CWvsContext.getOwlOpen());
            } else {
                c.getPlayer().dropMessage(5, "This can only be used inside the Free Market.");
                c.getSession().write(CWvsContext.enableActions());
            }
        }
    }

    public static final int OWL_ID = 2; // don't change. 0 = owner ID, 1 = store ID, 2 = object ID

    public static final void OwlWarp(final LittleEndianAccessor slea, final MapleClient c) {
        if (!c.getPlayer().isAlive()) {
            c.getSession().write(CWvsContext.getOwlMessage(4));
            return;
        } else if (c.getPlayer().getTrade() != null) {
            c.getSession().write(CWvsContext.getOwlMessage(7));
            return;
        }
        if (c.getPlayer().getMapId() >= 910000000 && c.getPlayer().getMapId() <= 910000022
                && !c.getPlayer().hasBlockedInventory()) {
            final int id = slea.readInt();
            final int map = slea.readInt();
            if (map >= 910000001 && map <= 910000022) {
                c.getSession().write(CWvsContext.getOwlMessage(0));
                final MapleMap mapp = c.getChannelServer().getMapFactory().getMap(map);
                c.getPlayer().changeMap(mapp, mapp.getPortal(0));
                HiredMerchant merchant = null;
                List<MapleMapObject> objects;
                switch (OWL_ID) {
                    case 0 -> {
                        objects = mapp.getAllHiredMerchantsThreadsafe();
                        for (MapleMapObject ob : objects) {
                            if (ob instanceof IMaplePlayerShop iMaplePlayerShop) {
                                final IMaplePlayerShop ips = iMaplePlayerShop;
                                if (ips instanceof HiredMerchant hiredMerchant) {
                                    final HiredMerchant merch = hiredMerchant;
                                    if (merch.getOwnerId() == id) {
                                        merchant = merch;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    case 1 -> {
                        objects = mapp.getAllHiredMerchantsThreadsafe();
                        for (MapleMapObject ob : objects) {
                            if (ob instanceof IMaplePlayerShop iMaplePlayerShop) {
                                final IMaplePlayerShop ips = iMaplePlayerShop;
                                if (ips instanceof HiredMerchant hiredMerchant) {
                                    final HiredMerchant merch = hiredMerchant;
                                    if (merch.getStoreId() == id) {
                                        merchant = merch;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    default -> {
                        final MapleMapObject ob = mapp.getMapObject(id, MapleMapObjectType.HIRED_MERCHANT);
                        if (ob instanceof IMaplePlayerShop iMaplePlayerShop) {
                            final IMaplePlayerShop ips = iMaplePlayerShop;
                            if (ips instanceof HiredMerchant hiredMerchant) {
                                merchant = hiredMerchant;
                            }
                        }
                    }
                }
                if (merchant != null) {
                    if (merchant.isOwner(c.getPlayer())) {
                        merchant.setOpen(false);
                        merchant.removeAllVisitors((byte) 16, (byte) 0);
                        c.getPlayer().setPlayerShop(merchant);
                        c.getSession().write(PlayerShopPacket.getHiredMerch(c.getPlayer(), merchant, false));
                    } else {
                        if (!merchant.isOpen() || !merchant.isAvailable()) {
                            c.getPlayer().dropMessage(1,
                                    "The owner of the store is currently undergoing store maintenance. Please try again in a bit.");
                        } else {
                            if (merchant.getFreeSlot() == -1) {
                                c.getPlayer().dropMessage(1, "You can't enter the room due to full capacity.");
                            } else if (merchant.isInBlackList(c.getPlayer().getName())) {
                                c.getPlayer().dropMessage(1, "You may not enter this store.");
                            } else {
                                c.getPlayer().setPlayerShop(merchant);
                                merchant.addVisitor(c.getPlayer());
                                c.getSession().write(PlayerShopPacket.getHiredMerch(c.getPlayer(), merchant, false));
                            }
                        }
                    }
                } else {
                    c.getPlayer().dropMessage(1, "The room is already closed.");
                }
            } else {
                c.getSession().write(CWvsContext.getOwlMessage(23));
            }
        } else {
            c.getSession().write(CWvsContext.getOwlMessage(23));
        }
    }

    public static final void PamSong(LittleEndianAccessor slea, MapleClient c) {
        final Item pam = c.getPlayer().getInventory(MapleInventoryType.CASH).findById(5640000);
        if (slea.readByte() > 0 && c.getPlayer().getScrolledPosition() != 0 && pam != null && pam.getQuantity() > 0) {
            final MapleInventoryType inv = c.getPlayer().getScrolledPosition() < 0 ? MapleInventoryType.EQUIPPED
                    : MapleInventoryType.EQUIP;
            final Item item = c.getPlayer().getInventory(inv).getItem(c.getPlayer().getScrolledPosition());
            c.getPlayer().setScrolledPosition((short) 0);
            if (item != null) {
                final Equip eq = (Equip) item;
                eq.setUpgradeSlots((byte) (eq.getUpgradeSlots() + 1));
                c.getPlayer().forceReAddItem_Flag(eq, inv);
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.CASH, pam.getPosition(), (short) 1, true,
                        false);
                c.getPlayer().getMap().broadcastMessage(CField.pamsSongEffect(c.getPlayer().getId()));
            }
        } else {
            c.getPlayer().setScrolledPosition((short) 0);
        }
    }

    public static final void TeleRock(LittleEndianAccessor slea, MapleClient c) {
        final byte slot = (byte) slea.readShort();
        final int itemId = slea.readInt();
        final Item toUse = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slot);

        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId || itemId / 10000 != 232
                || c.getPlayer().hasBlockedInventory()) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        boolean used = action.UseTeleRock();
        if (used) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
        }
        c.getSession().write(CWvsContext.enableActions());
    }

}
