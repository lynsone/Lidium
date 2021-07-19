package handling.channel.handler;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import client.MapleCharacter;
import client.MapleCharacterUtil;
import client.MapleClient;
import client.MapleQuestStatus;
import client.MapleStat;
import client.MonsterFamiliar;
import client.PlayerStats;
import client.Skill;
import client.SkillEntry;
import client.SkillFactory;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.inventory.Equip.ScrollResult;
import client.inventory.MaplePet.PetFlag;
import constants.GameConstants;
import handling.channel.ChannelServer;
import handling.world.World;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MapleShopFactory;
import server.RandomRewards;
import server.Randomizer;
import server.StructFamiliar;
import server.StructItemOption;
import server.StructRewardItem;
import server.events.MapleEvent;
import server.events.MapleEventType;
import server.life.MapleLifeFactory;
import server.maps.FieldLimitType;
import server.maps.MapleMap;
import server.maps.MapleMist;
import server.quest.MapleQuest;
import server.shops.HiredMerchant;
import tools.FileoutputUtil;
import tools.Pair;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;
import tools.packet.CWvsContext;
import tools.packet.MTSCSPacket;
import tools.packet.PetPacket;
import tools.packet.CField.EffectPacket;
import tools.packet.CWvsContext.InventoryPacket;
import java.awt.Rectangle;

public class InventoryHandlerAction {

    final LittleEndianAccessor slea;
    final MapleClient c;
    final int itemId;

    public InventoryHandlerAction(final LittleEndianAccessor slea, final MapleClient c, final int itemId) {
        this.c = c;
        this.slea = slea;
        this.itemId = itemId;
    }

    public boolean NpcTeleportRock() {
        // NPC Teleport Rock
        final short questid = slea.readShort();
        final int npcid = slea.readInt();
        final MapleQuest quest = MapleQuest.getInstance(questid);

        if (c.getPlayer().getQuest(quest).getStatus() == 1 && quest.canComplete(c.getPlayer(), npcid)) {
            final int mapId = MapleLifeFactory.getNPCLocation(npcid);
            if (mapId != -1) {
                final MapleMap map = c.getChannelServer().getMapFactory().getMap(mapId);

                if (map.containsNPC(npcid) && !FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit())
                        && !FieldLimitType.VipRock.check(map.getFieldLimit()) && !c.getPlayer().isInBlockedMap()) {
                    c.getPlayer().changeMap(map, map.getPortal(0));
                }
                return true;
            } else {
                c.getPlayer().dropMessage(1, "Unknown error has occurred.");
            }
        }
        return false;
    }

    public final boolean UseTeleRock() {
        boolean used = false;
        byte type = slea.readByte();
        if (type == 0) { // Rocktype
            slea.readByte(); // useless byte

            final MapleMap target = c.getChannelServer().getMapFactory().getMap(slea.readInt());
            if (target != null && ((itemId == 5041000 && c.getPlayer().isRockMap(target.getId()))
                    || ((itemId == 5040000 || itemId == 5040001) && c.getPlayer().isRegRockMap(target.getId()))
                    || ((itemId == 5040004 || itemId == 5041001) && (c.getPlayer().isHyperRockMap(target.getId())
                    || GameConstants.isHyperTeleMap(target.getId()))))) {
                // sure this map doesn't have a forced return map

                if (!FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit())
                        && !FieldLimitType.VipRock.check(target.getFieldLimit()) && !c.getPlayer().isInBlockedMap()) { // Makes

                    c.getPlayer().changeMap(target, target.getPortal(0));
                    used = true;
                } else {
                    c.getPlayer().dropMessage(1, "You cannot go to that place.");
                }
            } else {
                c.getPlayer().dropMessage(1, "You cannot go to that place.");
            }
        } else {
            final String name = slea.readMapleAsciiString();
            final MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(name);
            if (victim != null && !victim.isIntern() && c.getPlayer().getEventInstance() == null
                    && victim.getEventInstance() == null) {
                if (!FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit())
                        && !FieldLimitType.VipRock
                                .check(c.getChannelServer().getMapFactory().getMap(victim.getMapId()).getFieldLimit())
                        && !victim.isInBlockedMap() && !c.getPlayer().isInBlockedMap()) {
                    if (itemId == 5041000 || itemId == 5040004 || itemId == 5041001
                            || (victim.getMapId() / 100000000) == (c.getPlayer().getMapId() / 100000000)) { // Viprock
                        // or same
                        // continent
                        c.getPlayer().changeMap(victim.getMap(),
                                victim.getMap().findClosestPortal(victim.getTruePosition()));
                        used = true;
                    } else {
                        c.getPlayer().dropMessage(1, "You cannot go to that place.");
                    }
                } else {
                    c.getPlayer().dropMessage(1, "You cannot go to that place.");
                }
            } else {
                c.getPlayer().dropMessage(1,
                        "(" + name + ") is currently difficult to locate, so the teleport will not take place.");
            }
        }
        return used;
    }

    public boolean sendStorage() {
        c.getPlayer().setConversation(4);
        c.getPlayer().getStorage().sendStorage(c, 1022005);
        return true;
    }

    public boolean ApReset() {
        boolean used = false;
        // AP Reset
        Map<MapleStat, Integer> statupdate = new EnumMap<>(MapleStat.class);
        final int apto = GameConstants.GMS ? (int) slea.readLong() : slea.readInt();
        final int apfrom = GameConstants.GMS ? (int) slea.readLong() : slea.readInt();
        if (apto == apfrom) {
            // Hack
        }
        final int job = c.getPlayer().getJob();
        final PlayerStats playerst = c.getPlayer().getStat();
        used = true;
        switch (apto) { // AP to
            case 64: // str
                if (playerst.getStr() >= 999) {
                    used = false;
                }
                break;
            case 128: // dex
                if (playerst.getDex() >= 999) {
                    used = false;
                }
                break;
            case 256: // int
                if (playerst.getInt() >= 999) {
                    used = false;
                }
                break;
            case 512: // luk
                if (playerst.getLuk() >= 999) {
                    used = false;
                }
                break;
            case 2048: // hp
                if (playerst.getMaxHp() >= 99999) {
                    used = false;
                }
                break;
            case 8192: // mp
                if (playerst.getMaxMp() >= 99999) {
                    used = false;
                }
                break;
        }
        switch (apfrom) { // AP to
            case 64 -> {
                // str
                if (playerst.getStr() <= 4 || (c.getPlayer().getJob() % 1000 / 100 == 1 && playerst.getStr() <= 35)) {
                    used = false;
                }
            }
            case 128 -> {
                // dex
                if (playerst.getDex() <= 4 || (c.getPlayer().getJob() % 1000 / 100 == 3 && playerst.getDex() <= 25)
                        || (c.getPlayer().getJob() % 1000 / 100 == 4 && playerst.getDex() <= 25)
                        || (c.getPlayer().getJob() % 1000 / 100 == 5 && playerst.getDex() <= 20)) {
                    used = false;
                }
            }
            case 256 -> {
                // int
                if (playerst.getInt() <= 4 || (c.getPlayer().getJob() % 1000 / 100 == 2 && playerst.getInt() <= 20)) {
                    used = false;
                }
            }
            case 512 -> {
                // luk
                if (playerst.getLuk() <= 4) {
                    used = false;
                }
            }
            case 2048 -> {
                // hp
                if (/* playerst.getMaxMp() < ((c.getPlayer().getLevel() * 14) + 134) || */c.getPlayer().getHpApUsed() <= 0
                        || c.getPlayer().getHpApUsed() >= 10000) {
                    used = false;
                    c.getPlayer().dropMessage(1, "You need points in HP or MP in order to take points out.");
                }
            }
            case 8192 -> {
                // mp
                if (/* playerst.getMaxMp() < ((c.getPlayer().getLevel() * 14) + 134) || */c.getPlayer().getHpApUsed() <= 0
                        || c.getPlayer().getHpApUsed() >= 10000) {
                    used = false;
                    c.getPlayer().dropMessage(1, "You need points in HP or MP in order to take points out.");
                }
            }
        }
        // AP to
        if (used) {
            switch (apto) { // AP to
                case 64 -> { // str
                    final int toSet = playerst.getStr() + 1;
                    playerst.setStr((short) toSet, c.getPlayer());
                    statupdate.put(MapleStat.STR, toSet);
                }
                case 128 -> { // dex
                    final int toSet = playerst.getDex() + 1;
                    playerst.setDex((short) toSet, c.getPlayer());
                    statupdate.put(MapleStat.DEX, toSet);
                }
                case 256 -> { // int
                    final int toSet = playerst.getInt() + 1;
                    playerst.setInt((short) toSet, c.getPlayer());
                    statupdate.put(MapleStat.INT, toSet);
                }
                case 512 -> { // luk
                    final int toSet = playerst.getLuk() + 1;
                    playerst.setLuk((short) toSet, c.getPlayer());
                    statupdate.put(MapleStat.LUK, toSet);
                }
                case 2048 -> {
                    // hp
                    int maxhp = playerst.getMaxHp();
                    if (GameConstants.isBeginnerJob(job)) { // Beginner
                        maxhp += Randomizer.rand(4, 8);
                    } else if ((job >= 100 && job <= 132) || (job >= 3200 && job <= 3212) || (job >= 1100 && job <= 1112)
                            || (job >= 3100 && job <= 3112)) { // Warrior
                        maxhp += Randomizer.rand(36, 42);
                    } else if ((job >= 200 && job <= 232) || (GameConstants.isEvan(job)) || (job >= 1200 && job <= 1212)) { // Magician
                        maxhp += Randomizer.rand(10, 12);
                    } else if ((job >= 300 && job <= 322) || (job >= 400 && job <= 434) || (job >= 1300 && job <= 1312)
                            || (job >= 1400 && job <= 1412) || (job >= 3300 && job <= 3312)
                            || (job >= 2300 && job <= 2312)) { // Bowman
                        maxhp += Randomizer.rand(14, 18);
                    } else if ((job >= 510 && job <= 512) || (job >= 1510 && job <= 1512)) {
                        maxhp += Randomizer.rand(24, 28);
                    } else if ((job >= 500 && job <= 532) || (job >= 3500 && job <= 3512) || job == 1500) { // Pirate
                        maxhp += Randomizer.rand(16, 20);
                    } else if (job >= 2000 && job <= 2112) { // Aran
                        maxhp += Randomizer.rand(34, 38);
                    } else { // GameMaster
                        maxhp += Randomizer.rand(50, 100);
                    }
                    maxhp = Math.min(99999, Math.abs(maxhp));
                    c.getPlayer().setHpApUsed((short) (c.getPlayer().getHpApUsed() + 1));
                    playerst.setMaxHp(maxhp, c.getPlayer());
                    statupdate.put(MapleStat.MAXHP, (int) maxhp);
                }

                case 8192 -> {
                    // mp
                    int maxmp = playerst.getMaxMp();

                    if (GameConstants.isBeginnerJob(job)) { // Beginner
                        maxmp += Randomizer.rand(6, 8);
                    } else if (job >= 3100 && job <= 3112) {
                        break;
                    } else if ((job >= 100 && job <= 132) || (job >= 1100 && job <= 1112) || (job >= 2000 && job <= 2112)) { // Warrior
                        maxmp += Randomizer.rand(4, 9);
                    } else if ((job >= 200 && job <= 232) || (GameConstants.isEvan(job)) || (job >= 3200 && job <= 3212)
                            || (job >= 1200 && job <= 1212)) { // Magician
                        maxmp += Randomizer.rand(32, 36);
                    } else if ((job >= 300 && job <= 322) || (job >= 400 && job <= 434) || (job >= 500 && job <= 532)
                            || (job >= 3200 && job <= 3212) || (job >= 3500 && job <= 3512) || (job >= 1300 && job <= 1312)
                            || (job >= 1400 && job <= 1412) || (job >= 1500 && job <= 1512)
                            || (job >= 2300 && job <= 2312)) { // Bowman
                        maxmp += Randomizer.rand(8, 10);
                    } else { // GameMaster
                        maxmp += Randomizer.rand(50, 100);
                    }
                    maxmp = Math.min(99999, Math.abs(maxmp));
                    c.getPlayer().setHpApUsed((short) (c.getPlayer().getHpApUsed() + 1));
                    playerst.setMaxMp(maxmp, c.getPlayer());
                    statupdate.put(MapleStat.MAXMP, (int) maxmp);
                }
            }
            // AP to
            switch (apfrom) { // AP from
                case 64: { // str
                    final int toSet = playerst.getStr() - 1;
                    playerst.setStr((short) toSet, c.getPlayer());
                    statupdate.put(MapleStat.STR, toSet);
                    break;
                }
                case 128: { // dex
                    final int toSet = playerst.getDex() - 1;
                    playerst.setDex((short) toSet, c.getPlayer());
                    statupdate.put(MapleStat.DEX, toSet);
                    break;
                }
                case 256: { // int
                    final int toSet = playerst.getInt() - 1;
                    playerst.setInt((short) toSet, c.getPlayer());
                    statupdate.put(MapleStat.INT, toSet);
                    break;
                }
                case 512: { // luk
                    final int toSet = playerst.getLuk() - 1;
                    playerst.setLuk((short) toSet, c.getPlayer());
                    statupdate.put(MapleStat.LUK, toSet);
                    break;
                }
                case 2048: // HP
                    int maxhp = playerst.getMaxHp();
                    if (GameConstants.isBeginnerJob(job)) { // Beginner
                        maxhp -= 12;
                    } else if ((job >= 200 && job <= 232) || (job >= 1200 && job <= 1212)) { // Magician
                        maxhp -= 10;
                    } else if ((job >= 300 && job <= 322) || (job >= 400 && job <= 434) || (job >= 1300 && job <= 1312)
                            || (job >= 1400 && job <= 1412) || (job >= 3300 && job <= 3312) || (job >= 3500 && job <= 3512)
                            || (job >= 2300 && job <= 2312)) { // Bowman, Thief
                        maxhp -= 15;
                    } else if ((job >= 500 && job <= 532) || (job >= 1500 && job <= 1512)) { // Pirate
                        maxhp -= 22;
                    } else if (((job >= 100 && job <= 132) || job >= 1100 && job <= 1112) || (job >= 3100 && job <= 3112)) { // Soul
                        // Master
                        maxhp -= 32;
                    } else if ((job >= 2000 && job <= 2112) || (job >= 3200 && job <= 3212)) { // Aran
                        maxhp -= 40;
                    } else { // GameMaster
                        maxhp -= 20;
                    }
                    c.getPlayer().setHpApUsed((short) (c.getPlayer().getHpApUsed() - 1));
                    playerst.setMaxHp(maxhp, c.getPlayer());
                    statupdate.put(MapleStat.MAXHP, (int) maxhp);
                    break;
                case 8192: // MP
                    int maxmp = playerst.getMaxMp();
                    if (GameConstants.isBeginnerJob(job)) { // Beginner
                        maxmp -= 8;
                    } else if (job >= 3100 && job <= 3112) {
                        break;
                    } else if ((job >= 100 && job <= 132) || (job >= 1100 && job <= 1112)) { // Warrior
                        maxmp -= 4;
                    } else if ((job >= 200 && job <= 232) || (job >= 1200 && job <= 1212)) { // Magician
                        maxmp -= 30;
                    } else if ((job >= 500 && job <= 532) || (job >= 300 && job <= 322) || (job >= 400 && job <= 434)
                            || (job >= 1300 && job <= 1312) || (job >= 1400 && job <= 1412) || (job >= 1500 && job <= 1512)
                            || (job >= 3300 && job <= 3312) || (job >= 3500 && job <= 3512)
                            || (job >= 2300 && job <= 2312)) { // Pirate, Bowman. Thief
                        maxmp -= 10;
                    } else if (job >= 2000 && job <= 2112) { // Aran
                        maxmp -= 5;
                    } else { // GameMaster
                        maxmp -= 20;
                    }
                    c.getPlayer().setHpApUsed((short) (c.getPlayer().getHpApUsed() - 1));
                    playerst.setMaxMp(maxmp, c.getPlayer());
                    statupdate.put(MapleStat.MAXMP, (int) maxmp);
                    break;
            }
            c.getSession().write(CWvsContext.updatePlayerStats(statupdate, true, c.getPlayer()));
        }
        return used;
    }

    public boolean StarterPack() {
        // starter pack
        boolean used = true;
        MapleItemInformationProvider.getInstance().getFamiliars().entrySet().stream()
                .filter(f -> (f.getValue().itemid == 2870055 || f.getValue().itemid == 2871002
                || f.getValue().itemid == 2870235 || f.getValue().itemid == 2870019))
                .map(f -> {
                    MonsterFamiliar mf = c.getPlayer().getFamiliars().get(f.getKey());
                    if (mf != null) {
                        if (mf.getVitality() >= 3) {
                            mf.setExpiry((long) Math.min(System.currentTimeMillis() + 90 * 24 * 60 * 60000L,
                                    mf.getExpiry() + 30 * 24 * 60 * 60000L));
                        } else {
                            mf.setVitality(mf.getVitality() + 1);
                            mf.setExpiry((long) (mf.getExpiry() + 30 * 24 * 60 * 60000L));
                        }
                    } else {
                        mf = new MonsterFamiliar(c.getPlayer().getId(), f.getKey(),
                                (long) (System.currentTimeMillis() + 30 * 24 * 60 * 60000L));
                        c.getPlayer().getFamiliars().put(f.getKey(), mf);
                    }
                    return mf;
                }).forEachOrdered(mf -> {
            c.getSession().write(CField.registerFamiliar(mf));
        });
        return used;
    }

    public boolean BoosterPack() {
        boolean used;
        // booster pack

        if (c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 3) {
            c.getPlayer().dropMessage(5, "Make 3 USE space.");
        }
        used = true;
        int[] familiars = new int[3];
        while (true) {
            for (int i = 0; i < familiars.length; i++) {
                if (familiars[i] > 0) {
                    continue;
                }
                for (Map.Entry<Integer, StructFamiliar> f : MapleItemInformationProvider.getInstance().getFamiliars()
                        .entrySet()) {
                    if (Randomizer.nextInt(500) == 0
                            && ((i < 2 && f.getValue().grade == 0 || (i == 2 && f.getValue().grade != 0)))) {
                        MapleInventoryManipulator.addById(c, f.getValue().itemid, (short) 1, "Booster Pack");
                        // c.getSession().write(CField.getBoosterFamiliar(c.getPlayer().getId(),
                        // f.getKey(), 0));
                        familiars[i] = f.getValue().itemid;
                        break;
                    }
                }
            }
            if (familiars[0] > 0 && familiars[1] > 0 && familiars[2] > 0) {
                break;
            }
        }
        c.getSession().write(MTSCSPacket.getBoosterPack(familiars[0], familiars[1], familiars[2]));
        c.getSession().write(MTSCSPacket.getBoosterPackClick());
        c.getSession().write(MTSCSPacket.getBoosterPackReveal());
        return used;
    }

    public boolean SpResetScroll() {
        boolean used = false;
        if (itemId >= 5050005 && !GameConstants.isEvan(c.getPlayer().getJob())) {
            c.getPlayer().dropMessage(1, "This reset is only for Evans.");
        } // well i dont really care other than this o.o
        if (itemId < 5050005 && GameConstants.isEvan(c.getPlayer().getJob())) {
            c.getPlayer().dropMessage(1, "This reset is only for non-Evans.");
        } // well i dont really care other than this o.o
        int skill1 = slea.readInt();
        int skill2 = slea.readInt();
        for (int i : GameConstants.blockedSkills) {
            if (skill1 == i) {
                c.getPlayer().dropMessage(1, "You may not add this skill.");
                return false;
            }
        }
        Skill skillSPTo = SkillFactory.getSkill(skill1);
        Skill skillSPFrom = SkillFactory.getSkill(skill2);
        if (skillSPTo.isBeginnerSkill() || skillSPFrom.isBeginnerSkill()) {
            c.getPlayer().dropMessage(1, "You may not add beginner skills.");
        }
        if (GameConstants.getSkillBookForSkill(skill1) != GameConstants.getSkillBookForSkill(skill2)) {
            // resistance evan
            c.getPlayer().dropMessage(1, "You may not add different job skills.");
        }
        // if (GameConstants.getJobNumber(skill1 / 10000) >
        // GameConstants.getJobNumber(skill2 / 10000)) { //putting 3rd job skillpoints
        // into 4th job for example
        // c.getPlayer().dropMessage(1, "You may not add skillpoints to a higher job.");
        // break;
        // }
        if ((c.getPlayer().getSkillLevel(skillSPTo) + 1 <= skillSPTo.getMaxLevel())
                && c.getPlayer().getSkillLevel(skillSPFrom) > 0 && skillSPTo.canBeLearnedBy(c.getPlayer().getJob())) {
            if (skillSPTo.isFourthJob()
                    && (c.getPlayer().getSkillLevel(skillSPTo) + 1 > c.getPlayer().getMasterLevel(skillSPTo))) {
                c.getPlayer().dropMessage(1, "You will exceed the master level.");
            }
            if (itemId >= 5050005) {
                if (GameConstants.getSkillBookForSkill(skill1) != (itemId - 5050005) * 2
                        && GameConstants.getSkillBookForSkill(skill1) != (itemId - 5050005) * 2 + 1) {
                    c.getPlayer().dropMessage(1, "You may not add this job SP using this reset.");
                }
            } else {
                int theJob = GameConstants.getJobNumber(skill2 / 10000);
                switch (skill2 / 10000) {
                    case 430 ->
                        theJob = 1;
                    case 432, 431 ->
                        theJob = 2;
                    case 433 ->
                        theJob = 3;
                    case 434 ->
                        theJob = 4;
                }
                if (theJob != itemId - 5050000) {
                    // you may only subtract from the skill if the ID matches Sp reset
                    c.getPlayer().dropMessage(1, "You may not subtract from this skill. Use the appropriate SP reset.");
                }
            }
            final Map<Skill, SkillEntry> sa = new HashMap<>();
            sa.put(skillSPFrom, new SkillEntry((byte) (c.getPlayer().getSkillLevel(skillSPFrom) - 1),
                    c.getPlayer().getMasterLevel(skillSPFrom), SkillFactory.getDefaultSExpiry(skillSPFrom)));
            sa.put(skillSPTo, new SkillEntry((byte) (c.getPlayer().getSkillLevel(skillSPTo) + 1),
                    c.getPlayer().getMasterLevel(skillSPTo), SkillFactory.getDefaultSExpiry(skillSPTo)));
            c.getPlayer().changeSkillsLevel(sa);
            used = true;
        }
        return used;
    }

    public boolean HourGlass1() {
        boolean used = false;
        // Magic Hourglass 1 day
        final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(slea.readShort());
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final int days = 1;
        if (item != null && !GameConstants.isAccessory(item.getItemId()) && item.getExpiration() > -1
                && !ii.isCash(item.getItemId()) && System.currentTimeMillis()
                + (100 * 24 * 60 * 60 * 1000L) > item.getExpiration() + (days * 24 * 60 * 60 * 1000L)) {
            boolean change = true;
            for (String z : GameConstants.RESERVED) {
                if (c.getPlayer().getName().indexOf(z) != -1 || item.getOwner().indexOf(z) != -1) {
                    change = false;
                }
            }
            if (change) {
                item.setExpiration(item.getExpiration() + (days * 24 * 60 * 60 * 1000));
                c.getPlayer().forceReAddItem(item, MapleInventoryType.EQUIPPED);
                used = true;
            } else {
                c.getPlayer().dropMessage(1, "It may not be used on this item.");
            }
        }
        return used;
    }

    public boolean HourGlass7() {
        boolean used = false;
        // Magic Hourglass 7 day
        final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(slea.readShort());
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final int days = 7;
        if (item != null && !GameConstants.isAccessory(item.getItemId()) && item.getExpiration() > -1
                && !ii.isCash(item.getItemId()) && System.currentTimeMillis()
                + (100 * 24 * 60 * 60 * 1000L) > item.getExpiration() + (days * 24 * 60 * 60 * 1000L)) {
            boolean change = true;
            for (String z : GameConstants.RESERVED) {
                if (c.getPlayer().getName().indexOf(z) != -1 || item.getOwner().indexOf(z) != -1) {
                    change = false;
                }
            }
            if (change) {
                item.setExpiration(item.getExpiration() + (days * 24 * 60 * 60 * 1000));
                c.getPlayer().forceReAddItem(item, MapleInventoryType.EQUIPPED);
                used = true;
            } else {
                c.getPlayer().dropMessage(1, "It may not be used on this item.");
            }
        }
        return used;
    }

    public boolean HourGlass20() {
        boolean used = false;
        final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(slea.readShort());
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final int days = 20;
        if (item != null && !GameConstants.isAccessory(item.getItemId()) && item.getExpiration() > -1
                && !ii.isCash(item.getItemId()) && System.currentTimeMillis()
                + (100 * 24 * 60 * 60 * 1000L) > item.getExpiration() + (days * 24 * 60 * 60 * 1000L)) {
            boolean change = true;
            for (String z : GameConstants.RESERVED) {
                if (c.getPlayer().getName().indexOf(z) != -1 || item.getOwner().indexOf(z) != -1) {
                    change = false;
                }
            }
            if (change) {
                item.setExpiration(item.getExpiration() + (days * 24 * 60 * 60 * 1000));
                c.getPlayer().forceReAddItem(item, MapleInventoryType.EQUIPPED);
                used = true;
            } else {
                c.getPlayer().dropMessage(1, "It may not be used on this item.");
            }
        }
        return used;
    }

    public boolean HourGlass50() {
        boolean used = false;
        final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(slea.readShort());
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final int days = 50;
        if (item != null && !GameConstants.isAccessory(item.getItemId()) && item.getExpiration() > -1
                && !ii.isCash(item.getItemId()) && System.currentTimeMillis()
                + (100 * 24 * 60 * 60 * 1000L) > item.getExpiration() + (days * 24 * 60 * 60 * 1000L)) {
            boolean change = true;
            for (String z : GameConstants.RESERVED) {
                if (c.getPlayer().getName().contains(z) || item.getOwner().contains(z)) {
                    change = false;
                }
            }
            if (change) {
                item.setExpiration(item.getExpiration() + (days * 24 * 60 * 60 * 1000));
                c.getPlayer().forceReAddItem(item, MapleInventoryType.EQUIPPED);
                used = true;
            } else {
                c.getPlayer().dropMessage(1, "It may not be used on this item.");
            }
        }
        return used;
    }

    public boolean HourGlass99() {
        boolean used = false;
        final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(slea.readShort());
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final int days = 99;
        if (item != null && !GameConstants.isAccessory(item.getItemId()) && item.getExpiration() > -1
                && !ii.isCash(item.getItemId()) && System.currentTimeMillis()
                + (100 * 24 * 60 * 60 * 1000L) > item.getExpiration() + (days * 24 * 60 * 60 * 1000L)) {
            boolean change = true;
            for (String z : GameConstants.RESERVED) {
                if (c.getPlayer().getName().contains(z) || item.getOwner().contains(z)) {
                    change = false;
                }
            }
            if (change) {
                item.setExpiration(item.getExpiration() + (days * 24 * 60 * 60 * 1000));
                c.getPlayer().forceReAddItem(item, MapleInventoryType.EQUIPPED);
                used = true;
            } else {
                c.getPlayer().dropMessage(1, "It may not be used on this item.");
            }
        }
        return used;
    }

    public boolean ItemTag() {
        boolean used = false;
        final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(slea.readShort());
        if (item != null && item.getOwner().equals("")) {
            boolean change = true;
            for (String z : GameConstants.RESERVED) {
                if (c.getPlayer().getName().contains(z)) {
                    change = false;
                }
            }
            if (change) {
                item.setOwner(c.getPlayer().getName());
                c.getPlayer().forceReAddItem(item, MapleInventoryType.EQUIPPED);
                used = true;
            }
        }
        return used;
    }

    public boolean FatigueResetDrink() {
        if (c.getPlayer().getFatigue() > 0) {
            c.getPlayer().setFatigue(0);
            return true;
        }
        return false;
    }

    public boolean TimsSecretLab(Item toUse) {
        boolean used = false;
        final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte) slea.readInt());
        if (item != null) {
            final Equip eq = (Equip) item;
            if (eq.getState() == 0) {
                eq.resetPotential();
                c.getPlayer().getMap()
                        .broadcastMessage(CField.showPotentialReset(false, c.getPlayer().getId(), true, itemId));
                c.getSession().write(InventoryPacket.scrolledItem(toUse, item, false, true));
                c.getPlayer().forceReAddItem_NoUpdate(item, MapleInventoryType.EQUIP);
                used = true;
            } else {
                c.getPlayer().dropMessage(5, "This item's Potential cannot be reset.");
            }
        } else {
            c.getPlayer().getMap()
                    .broadcastMessage(CField.showPotentialReset(false, c.getPlayer().getId(), false, itemId));
        }
        return used;
    }

    public boolean MiracleCube(Item toUse) {
        boolean used = false;
        // miracle cube
        if (c.getPlayer().getLevel() < 50) {
            c.getPlayer().dropMessage(1, "You may not use this until level 50.");
        } else {
            final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte) slea.readInt());
            if (item != null && c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() >= 1) {
                final Equip eq = (Equip) item;
                if (eq.getState() >= 17 && eq.getState() != 20) {
                    eq.renewPotential(0);
                    c.getPlayer().getMap()
                            .broadcastMessage(CField.showPotentialReset(false, c.getPlayer().getId(), true, itemId));
                    c.getSession().write(InventoryPacket.scrolledItem(toUse, item, false, true));
                    c.getPlayer().forceReAddItem_NoUpdate(item, MapleInventoryType.EQUIP);
                    MapleInventoryManipulator.addById(c, 2430112, (short) 1,
                            "Cube" + " on " + FileoutputUtil.CurrentReadable_Date());
                    used = true;
                } else {
                    c.getPlayer().dropMessage(5, "This item's Potential cannot be reset.");
                }
            } else {
                c.getPlayer().getMap()
                        .broadcastMessage(CField.showPotentialReset(false, c.getPlayer().getId(), false, itemId));
            }
        }
        return used;
    }

    public boolean PremiumMiracleCube(Item toUse) {
        boolean used = false;
        // premium cube
        if (c.getPlayer().getLevel() < 70) {
            c.getPlayer().dropMessage(1, "You may not use this until level 70.");
        } else {
            final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte) slea.readInt());
            if (item != null && c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() >= 1) {
                final Equip eq = (Equip) item;
                if (eq.getState() >= 17 && eq.getState() != 20) {
                    eq.renewPotential(1);
                    c.getPlayer().getMap()
                            .broadcastMessage(CField.showPotentialReset(false, c.getPlayer().getId(), true, itemId));
                    c.getSession().write(InventoryPacket.scrolledItem(toUse, item, false, true));
                    c.getPlayer().forceReAddItem_NoUpdate(item, MapleInventoryType.EQUIP);
                    MapleInventoryManipulator.addById(c, 2430112, (short) 1,
                            "Cube" + " on " + FileoutputUtil.CurrentReadable_Date());
                    used = true;
                } else {
                    c.getPlayer().dropMessage(5, "This item's Potential cannot be reset.");
                }
            } else {
                c.getPlayer().getMap()
                        .broadcastMessage(CField.showPotentialReset(false, c.getPlayer().getId(), false, itemId));
            }
        }
        return used;
    }

    public boolean SuperMiracleCube(Item toUse) {
        boolean used = false;
        // super miracle cube
        if (c.getPlayer().getLevel() < 100) {
            c.getPlayer().dropMessage(1, "You may not use this until level 100.");
        } else {
            final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte) slea.readInt());
            if (item != null && c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() >= 1) {
                final Equip eq = (Equip) item;
                if (eq.getState() >= 17) {
                    eq.renewPotential(3);
                    c.getPlayer().getMap()
                            .broadcastMessage(CField.showPotentialReset(false, c.getPlayer().getId(), true, itemId));
                    c.getSession().write(InventoryPacket.scrolledItem(toUse, item, false, true));
                    c.getPlayer().forceReAddItem_NoUpdate(item, MapleInventoryType.EQUIP);
                    MapleInventoryManipulator.addById(c, 2430481, (short) 1,
                            "Cube" + " on " + FileoutputUtil.CurrentReadable_Date());
                    used = true;
                } else {
                    c.getPlayer().dropMessage(5, "This item's Potential cannot be reset.");
                }
            } else {
                c.getPlayer().getMap()
                        .broadcastMessage(CField.showPotentialReset(false, c.getPlayer().getId(), false, itemId));
            }
        }
        return used;
    }

    public boolean AlienCube() {
        boolean used = false;
        // alien cube
        if (c.getPlayer().getLevel() < 10) {
            c.getPlayer().dropMessage(1, "You may not use this until level 10.");
        } else {
            final Item item = c.getPlayer().getInventory(MapleInventoryType.SETUP).getItem((byte) slea.readInt());
            if (item != null && c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() >= 1
                    && c.getPlayer().getInventory(MapleInventoryType.SETUP).getNumFreeSlot() >= 1) {
                final int grade = GameConstants.getNebuliteGrade(item.getItemId());
                if (grade != -1 && grade < 4) {
                    final int rank = Randomizer.nextInt(100) < 7
                            ? (Randomizer.nextInt(100) < 2 ? (grade + 1) : (grade != 3 ? (grade + 1) : grade))
                            : grade;

                    final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                    final List<StructItemOption> pots = new LinkedList<>(ii.getAllSocketInfo(rank).values());
                    int newId = 0;
                    while (newId == 0) {
                        StructItemOption pot = pots.get(Randomizer.nextInt(pots.size()));
                        if (pot != null) {
                            newId = pot.opID;
                        }
                    }
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.SETUP, item.getPosition(), (short) 1,
                            false);
                    MapleInventoryManipulator.addById(c, newId, (short) 1,
                            "Upgraded from alien cube on " + FileoutputUtil.CurrentReadable_Date());
                    MapleInventoryManipulator.addById(c, 2430691, (short) 1,
                            "Alien Cube" + " on " + FileoutputUtil.CurrentReadable_Date());
                    used = true;
                } else {
                    c.getPlayer().dropMessage(1, "Grade S Nebulite cannot be added.");
                }
            } else {
                c.getPlayer().dropMessage(5, "You do not have sufficient inventory slot.");
            }
        }
        return used;
    }

    public boolean NebuliteDiffuser(Item toUse) {
        boolean used = false;
        if (c.getPlayer().getLevel() < 10) {
            c.getPlayer().dropMessage(1, "You may not use this until level 10.");
        } else {
            final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte) slea.readInt());
            if (item != null) {
                final Equip eq = (Equip) item;
                if (eq.getSocket1() > 0) { // first slot only.
                    eq.setSocket1(0);
                    c.getSession().write(InventoryPacket.scrolledItem(toUse, item, false, true));
                    c.getPlayer().forceReAddItem_NoUpdate(item, MapleInventoryType.EQUIP);
                    used = true;
                } else {
                    c.getPlayer().dropMessage(5, "This item do not have a socket.");
                }
            } else {
                c.getPlayer().dropMessage(5, "This item's nebulite cannot be removed.");
            }
        }
        return used;
    }

    public boolean ScissorOfKarma() {
        boolean used = false;
        // Karma
        final MapleInventoryType type = MapleInventoryType.getByType((byte) slea.readInt());
        final Item item = c.getPlayer().getInventory(type).getItem((byte) slea.readInt());

        if (item != null && !ItemFlag.KARMA_EQ.check(item.getFlag()) && !ItemFlag.KARMA_USE.check(item.getFlag())) {
            if ((itemId == 5520000 && MapleItemInformationProvider.getInstance().isKarmaEnabled(item.getItemId()))
                    || (itemId == 5520001
                    && MapleItemInformationProvider.getInstance().isPKarmaEnabled(item.getItemId()))) {
                short flag = item.getFlag();

                if (ItemFlag.UNTRADEABLE.check(flag)) {
                    flag -= ItemFlag.UNTRADEABLE.getValue();
                } else if (type == MapleInventoryType.EQUIP) {
                    flag |= ItemFlag.KARMA_EQ.getValue();
                } else {
                    flag |= ItemFlag.KARMA_USE.getValue();
                }
                item.setFlag(flag);
                c.getPlayer().forceReAddItem_NoUpdate(item, type);
                c.getSession().write(InventoryPacket.updateSpecialItemUse(item, type.getType(), item.getPosition(),
                        true, c.getPlayer()));
                used = true;
            }
        }
        return used;
    }

    public boolean ViciousHammer() {
        boolean used = false;
        // Vicious Hammer
        slea.readInt(); // Inventory type, Hammered eq is always EQ.
        final Equip item = (Equip) c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte) slea.readInt());
        // another int here, D3 49 DC 00
        if (item != null) {
            if (GameConstants.canHammer(item.getItemId())
                    && MapleItemInformationProvider.getInstance().getSlots(item.getItemId()) > 0
                    && item.getViciousHammer() < 2) {
                item.setViciousHammer((byte) (item.getViciousHammer() + 1));
                item.setUpgradeSlots((byte) (item.getUpgradeSlots() + 1));
                c.getPlayer().forceReAddItem(item, MapleInventoryType.EQUIP);
                c.getSession().write(MTSCSPacket.ViciousHammer(true, (byte) item.getViciousHammer()));
                used = true;
            } else {
                c.getPlayer().dropMessage(5, "You may not use it on this item.");
                c.getSession().write(MTSCSPacket.ViciousHammer(true, (byte) 0));
            }
        }
        return used;
    }

    public boolean VegaSpell() {
        boolean used = false;
        slea.readInt(); // Inventory type, always eq
        final short dst = (short) slea.readInt();
        slea.readInt(); // Inventory type, always use
        final short src = (short) slea.readInt();
        used = UseUpgradeScroll(src, dst, (short) 2, c, c.getPlayer(), itemId, false); // cannot use ws with

        return used;
    }
    /////////////////////////////////////////// methods to work with

    public static final boolean UseUpgradeScroll(final short slot, final short dst, final short ws, final MapleClient c,
            final MapleCharacter chr, final boolean legendarySpirit) {
        return UseUpgradeScroll(slot, dst, ws, c, chr, 0, legendarySpirit);
    }

    public static final boolean UseUpgradeScroll(final short slot, final short dst, final short ws, final MapleClient c,
            final MapleCharacter chr, final int vegas, final boolean legendarySpirit) {
        boolean whiteScroll = false; // white scroll being used?
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        chr.setScrolledPosition((short) 0);
        if ((ws & 2) == 2) {
            whiteScroll = true;
        }
        Equip toScroll = null;
        if (dst < 0) {
            toScroll = (Equip) chr.getInventory(MapleInventoryType.EQUIPPED).getItem(dst);
        } else if (legendarySpirit) {
            toScroll = (Equip) chr.getInventory(MapleInventoryType.EQUIP).getItem(dst);
        }
        if (toScroll == null || c.getPlayer().hasBlockedInventory()) {
            c.getSession().write(CWvsContext.enableActions());
            return false;
        }
        final byte oldLevel = toScroll.getLevel();
        final byte oldEnhance = toScroll.getEnhance();
        final byte oldState = toScroll.getState();
        final short oldFlag = toScroll.getFlag();
        final byte oldSlots = toScroll.getUpgradeSlots();

        Item scroll = chr.getInventory(MapleInventoryType.USE).getItem(slot);
        if (scroll == null) {
            scroll = chr.getInventory(MapleInventoryType.CASH).getItem(slot);
            if (scroll == null) {
                c.getSession().write(InventoryPacket.getInventoryFull());
                c.getSession().write(CWvsContext.enableActions());
                return false;
            }
        }
        if (!GameConstants.isSpecialScroll(scroll.getItemId()) && !GameConstants.isCleanSlate(scroll.getItemId())
                && !GameConstants.isEquipScroll(scroll.getItemId())
                && !GameConstants.isPotentialScroll(scroll.getItemId())) {
            if (toScroll.getUpgradeSlots() < 1) {
                c.getSession().write(InventoryPacket.getInventoryFull());
                c.getSession().write(CWvsContext.enableActions());
                return false;
            }
        } else if (GameConstants.isEquipScroll(scroll.getItemId())) {
            if (toScroll.getUpgradeSlots() >= 1 || toScroll.getEnhance() >= 100 || vegas > 0
                    || ii.isCash(toScroll.getItemId())) {
                c.getSession().write(InventoryPacket.getInventoryFull());
                c.getSession().write(CWvsContext.enableActions());
                return false;
            }
        } else if (GameConstants.isPotentialScroll(scroll.getItemId())) {
            final boolean isEpic = scroll.getItemId() / 100 == 20497;
            if ((!isEpic && toScroll.getState() >= 1) || (isEpic && toScroll.getState() >= 18)
                    || (toScroll.getLevel() == 0 && toScroll.getUpgradeSlots() == 0
                    && toScroll.getItemId() / 10000 != 135 && !isEpic)
                    || vegas > 0 || ii.isCash(toScroll.getItemId())) {
                c.getSession().write(InventoryPacket.getInventoryFull());
                c.getSession().write(CWvsContext.enableActions());
                return false;
            }
        } else if (GameConstants.isSpecialScroll(scroll.getItemId())) {
            if (ii.isCash(toScroll.getItemId()) || toScroll.getEnhance() >= 8) {
                c.getSession().write(InventoryPacket.getInventoryFull());
                c.getSession().write(CWvsContext.enableActions());
                return false;
            }
        }
        if (!GameConstants.canScroll(toScroll.getItemId()) && !GameConstants.isChaosScroll(toScroll.getItemId())) {
            c.getSession().write(InventoryPacket.getInventoryFull());
            c.getSession().write(CWvsContext.enableActions());
            return false;
        }
        if ((GameConstants.isCleanSlate(scroll.getItemId()) || GameConstants.isTablet(scroll.getItemId())
                || GameConstants.isGeneralScroll(scroll.getItemId()) || GameConstants.isChaosScroll(scroll.getItemId()))
                && (vegas > 0 || ii.isCash(toScroll.getItemId()))) {
            c.getSession().write(InventoryPacket.getInventoryFull());
            c.getSession().write(CWvsContext.enableActions());
            return false;
        }
        if (GameConstants.isTablet(scroll.getItemId()) && toScroll.getDurability() < 0) { // not a durability item
            c.getSession().write(InventoryPacket.getInventoryFull());
            c.getSession().write(CWvsContext.enableActions());
            return false;
        } else if ((!GameConstants.isTablet(scroll.getItemId()) && !GameConstants.isPotentialScroll(scroll.getItemId())
                && !GameConstants.isEquipScroll(scroll.getItemId()) && !GameConstants.isCleanSlate(scroll.getItemId())
                && !GameConstants.isSpecialScroll(scroll.getItemId())
                && !GameConstants.isChaosScroll(scroll.getItemId())) && toScroll.getDurability() >= 0) {
            c.getSession().write(InventoryPacket.getInventoryFull());
            c.getSession().write(CWvsContext.enableActions());
            return false;
        }
        Item wscroll = null;

        // Anti cheat and validation
        List<Integer> scrollReqs = ii.getScrollReqs(scroll.getItemId());
        if (scrollReqs != null && scrollReqs.size() > 0 && !scrollReqs.contains(toScroll.getItemId())) {
            c.getSession().write(InventoryPacket.getInventoryFull());
            c.getSession().write(CWvsContext.enableActions());
            return false;
        }

        if (whiteScroll) {
            wscroll = chr.getInventory(MapleInventoryType.USE).findById(2340000);
            if (wscroll == null) {
                whiteScroll = false;
            }
        }
        if (GameConstants.isTablet(scroll.getItemId()) || GameConstants.isGeneralScroll(scroll.getItemId())) {
            switch (scroll.getItemId() % 1000 / 100) {
                case 0: // 1h
                    if (GameConstants.isTwoHanded(toScroll.getItemId()) || !GameConstants.isWeapon(toScroll.getItemId())) {
                        c.getSession().write(CWvsContext.enableActions());
                        return false;
                    }
                    break;
                case 1: // 2h
                    if (!GameConstants.isTwoHanded(toScroll.getItemId()) || !GameConstants.isWeapon(toScroll.getItemId())) {
                        c.getSession().write(CWvsContext.enableActions());
                        return false;
                    }
                    break;
                case 2: // armor
                    if (GameConstants.isAccessory(toScroll.getItemId()) || GameConstants.isWeapon(toScroll.getItemId())) {
                        c.getSession().write(CWvsContext.enableActions());
                        return false;
                    }
                    break;
                case 3: // accessory
                    if (!GameConstants.isAccessory(toScroll.getItemId()) || GameConstants.isWeapon(toScroll.getItemId())) {
                        c.getSession().write(CWvsContext.enableActions());
                        return false;
                    }
                    break;
            }
        } else if (!GameConstants.isAccessoryScroll(scroll.getItemId())
                && !GameConstants.isChaosScroll(scroll.getItemId()) && !GameConstants.isCleanSlate(scroll.getItemId())
                && !GameConstants.isEquipScroll(scroll.getItemId())
                && !GameConstants.isPotentialScroll(scroll.getItemId())
                && !GameConstants.isSpecialScroll(scroll.getItemId())) {
            if (!ii.canScroll(scroll.getItemId(), toScroll.getItemId())) {
                c.getSession().write(CWvsContext.enableActions());
                return false;
            }
        }
        if (GameConstants.isAccessoryScroll(scroll.getItemId()) && !GameConstants.isAccessory(toScroll.getItemId())) {
            c.getSession().write(CWvsContext.enableActions());
            return false;
        }
        if (scroll.getQuantity() <= 0) {
            c.getSession().write(CWvsContext.enableActions());
            return false;
        }

        if (legendarySpirit && vegas == 0) {
            if (chr.getSkillLevel(SkillFactory.getSkill(PlayerStats.getSkillByJob(1003, chr.getJob()))) <= 0) {
                c.getSession().write(CWvsContext.enableActions());
                return false;
            }
        }

        // Scroll Success/ Failure/ Curse
        Equip scrolled = (Equip) ii.scrollEquipWithId(toScroll, scroll, whiteScroll, chr, vegas);
        ScrollResult scrollSuccess;
        if (scrolled == null) {
            if (ItemFlag.SHIELD_WARD.check(oldFlag)) {
                scrolled = toScroll;
                scrollSuccess = Equip.ScrollResult.FAIL;
                scrolled.setFlag((short) (oldFlag - ItemFlag.SHIELD_WARD.getValue()));
            } else {
                scrollSuccess = Equip.ScrollResult.CURSE;
            }
        } else if ((scroll.getItemId() / 100 == 20497 && scrolled.getState() == 1) || scrolled.getLevel() > oldLevel
                || scrolled.getEnhance() > oldEnhance || scrolled.getState() > oldState
                || scrolled.getFlag() > oldFlag) {
            scrollSuccess = Equip.ScrollResult.SUCCESS;
        } else if ((GameConstants.isCleanSlate(scroll.getItemId()) && scrolled.getUpgradeSlots() > oldSlots)) {
            scrollSuccess = Equip.ScrollResult.SUCCESS;
        } else {
            scrollSuccess = Equip.ScrollResult.FAIL;
        }
        // Update
        chr.getInventory(GameConstants.getInventoryType(scroll.getItemId())).removeItem(scroll.getPosition(), (short) 1,
                false);
        if (whiteScroll) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, wscroll.getPosition(), (short) 1, false,
                    false);
        } else if (scrollSuccess == Equip.ScrollResult.FAIL && scrolled.getUpgradeSlots() < oldSlots
                && c.getPlayer().getInventory(MapleInventoryType.CASH).findById(5640000) != null) {
            chr.setScrolledPosition(scrolled.getPosition());
            if (vegas == 0) {
                c.getSession().write(CWvsContext.pamSongUI());
            }
        }

        if (scrollSuccess == Equip.ScrollResult.CURSE) {
            c.getSession().write(InventoryPacket.scrolledItem(scroll, toScroll, true, false));
            if (dst < 0) {
                chr.getInventory(MapleInventoryType.EQUIPPED).removeItem(toScroll.getPosition());
            } else {
                chr.getInventory(MapleInventoryType.EQUIP).removeItem(toScroll.getPosition());
            }
        } else if (vegas == 0) {
            c.getSession().write(InventoryPacket.scrolledItem(scroll, scrolled, false, false));
        }

        chr.getMap().broadcastMessage(chr,
                CField.getScrollEffect(c.getPlayer().getId(), scrollSuccess, legendarySpirit, whiteScroll), vegas == 0);
        // addToScrollLog(chr.getAccountID(), chr.getId(), scroll.getItemId(), itemID,
        // oldSlots, (byte)(scrolled == null ? -1 : scrolled.getUpgradeSlots()), oldVH,
        // scrollSuccess.name(), whiteScroll, legendarySpirit, vegas);
        // equipped item was scrolled and changed
        if (dst < 0 && (scrollSuccess == Equip.ScrollResult.SUCCESS || scrollSuccess == Equip.ScrollResult.CURSE)
                && vegas == 0) {
            chr.equipChanged();
        }
        return true;
    }

    private static boolean getIncubatedItems(MapleClient c, int itemId) {
        if (c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 2
                || c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 2
                || c.getPlayer().getInventory(MapleInventoryType.SETUP).getNumFreeSlot() < 2) {
            c.getPlayer().dropMessage(5, "Please make room in your inventory.");
            return false;
        }
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        int id1 = RandomRewards.getPeanutReward(), id2 = RandomRewards.getPeanutReward();
        while (!ii.itemExists(id1)) {
            id1 = RandomRewards.getPeanutReward();
        }
        while (!ii.itemExists(id2)) {
            id2 = RandomRewards.getPeanutReward();
        }
        c.getSession().write(CWvsContext.getPeanutResult(id1, (short) 1, id2, (short) 1, itemId));
        MapleInventoryManipulator.addById(c, id1, (short) 1,
                ii.getName(itemId) + " on " + FileoutputUtil.CurrentReadable_Date());
        MapleInventoryManipulator.addById(c, id2, (short) 1,
                ii.getName(itemId) + " on " + FileoutputUtil.CurrentReadable_Date());
        return true;
    }

    private static final void addMedalString(final MapleCharacter c, final StringBuilder sb) {
        final Item medal = c.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -46);
        if (medal != null) { // Medal
            sb.append("<");
            if (medal.getItemId() == 1142257 && GameConstants.isAdventurer(c.getJob())) {
                MapleQuestStatus stat = c.getQuestNoAdd(MapleQuest.getInstance(GameConstants.ULT_EXPLORER));
                if (stat != null && stat.getCustomData() != null) {
                    sb.append(stat.getCustomData());
                    sb.append("'s Successor");
                } else {
                    sb.append(MapleItemInformationProvider.getInstance().getName(medal.getItemId()));
                }
            } else {
                sb.append(MapleItemInformationProvider.getInstance().getName(medal.getItemId()));
            }
            sb.append("> ");
        }
    }

    public static final boolean UseSkillBook(final byte slot, final int itemId, final MapleClient c,
            final MapleCharacter chr) {
        final Item toUse = chr.getInventory(GameConstants.getInventoryType(itemId)).getItem(slot);

        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId || chr.hasBlockedInventory()) {
            return false;
        }
        final Map<String, Integer> skilldata = MapleItemInformationProvider.getInstance()
                .getEquipStats(toUse.getItemId());
        if (skilldata == null) { // Hacking or used an unknown item
            return false;
        }
        boolean canuse = false, success = false;
        int skill = 0, maxlevel = 0;

        final Integer SuccessRate = skilldata.get("success");
        final Integer ReqSkillLevel = skilldata.get("reqSkillLevel");
        final Integer MasterLevel = skilldata.get("masterLevel");

        byte i = 0;
        Integer CurrentLoopedSkillId;
        while (true) {
            CurrentLoopedSkillId = skilldata.get("skillid" + i);
            i++;
            if (CurrentLoopedSkillId == null || MasterLevel == null) {
                break; // End of data
            }
            final Skill CurrSkillData = SkillFactory.getSkill(CurrentLoopedSkillId);
            if (CurrSkillData != null && CurrSkillData.canBeLearnedBy(chr.getJob())
                    && (ReqSkillLevel == null || chr.getSkillLevel(CurrSkillData) >= ReqSkillLevel)
                    && chr.getMasterLevel(CurrSkillData) < MasterLevel) {
                canuse = true;
                if (SuccessRate == null || Randomizer.nextInt(100) <= SuccessRate) {
                    success = true;
                    chr.changeSingleSkillLevel(CurrSkillData, chr.getSkillLevel(CurrSkillData),
                            (byte) (int) MasterLevel);
                } else {
                    success = false;
                }
                MapleInventoryManipulator.removeFromSlot(c, GameConstants.getInventoryType(itemId), slot, (short) 1,
                        false);
                break;
            }
        }
        c.getPlayer().getMap().broadcastMessage(CWvsContext.useSkillBook(chr, skill, maxlevel, canuse, success));
        c.getSession().write(CWvsContext.enableActions());
        return canuse;
    }

    public static final boolean UseRewardItem(final byte slot, final int itemId, final MapleClient c,
            final MapleCharacter chr) {
        final Item toUse = c.getPlayer().getInventory(GameConstants.getInventoryType(itemId)).getItem(slot);
        c.getSession().write(CWvsContext.enableActions());
        if (toUse != null && toUse.getQuantity() >= 1 && toUse.getItemId() == itemId && !chr.hasBlockedInventory()) {
            if (chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot() > -1
                    && chr.getInventory(MapleInventoryType.USE).getNextFreeSlot() > -1
                    && chr.getInventory(MapleInventoryType.SETUP).getNextFreeSlot() > -1
                    && chr.getInventory(MapleInventoryType.ETC).getNextFreeSlot() > -1) {
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                final Pair<Integer, List<StructRewardItem>> rewards = ii.getRewardItem(itemId);

                if (rewards != null && rewards.getLeft() > 0) {
                    while (true) {
                        for (StructRewardItem reward : rewards.getRight()) {
                            if (reward.prob > 0 && Randomizer.nextInt(rewards.getLeft()) < reward.prob) { // Total prob
                                if (GameConstants.getInventoryType(reward.itemid) == MapleInventoryType.EQUIP) {
                                    final Item item = ii.getEquipById(reward.itemid);
                                    if (reward.period > 0) {
                                        item.setExpiration(System.currentTimeMillis() + (reward.period * 60 * 60 * 10));
                                    }
                                    item.setGMLog(
                                            "Reward item: " + itemId + " on " + FileoutputUtil.CurrentReadable_Date());
                                    MapleInventoryManipulator.addbyItem(c, item);
                                } else {
                                    MapleInventoryManipulator.addById(c, reward.itemid, reward.quantity,
                                            "Reward item: " + itemId + " on " + FileoutputUtil.CurrentReadable_Date());
                                }
                                MapleInventoryManipulator.removeById(c, GameConstants.getInventoryType(itemId), itemId,
                                        1, false, false);

                                c.getSession()
                                        .write(EffectPacket.showRewardItemAnimation(reward.itemid, reward.effect));
                                chr.getMap().broadcastMessage(chr,
                                        EffectPacket.showRewardItemAnimation(reward.itemid, reward.effect, chr.getId()),
                                        false);
                                return true;
                            }
                        }
                    }
                } else {
                    chr.dropMessage(6, "Unknown error.");
                }
            } else {
                chr.dropMessage(6, "Insufficient inventory slot.");
            }
        }
        return false;
    }

    /////////////////////////////// END ///////////
    public boolean ItemGuard() {
        boolean used = false;
        final MapleInventoryType type = MapleInventoryType.getByType((byte) slea.readInt());
        final Item item = c.getPlayer().getInventory(type).getItem((byte) slea.readInt());
        // another int here, lock = 5A E5 F2 0A, 7 day = D2 30 F3 0A
        if (item != null && item.getExpiration() == -1) {
            short flag = item.getFlag();
            flag |= ItemFlag.LOCK.getValue();
            item.setFlag(flag);

            c.getPlayer().forceReAddItem_Flag(item, type);
            used = true;
        }
        return used;
    }

    public boolean ItemGuard(long expiration) {
        boolean used = false;
        final MapleInventoryType type = MapleInventoryType.getByType((byte) slea.readInt());
        final Item item = c.getPlayer().getInventory(type).getItem((byte) slea.readInt());
        // another int here, lock = 5A E5 F2 0A, 7 day = D2 30 F3 0A
        if (item != null && item.getExpiration() == -1) {
            short flag = item.getFlag();
            flag |= ItemFlag.LOCK.getValue();
            item.setFlag(flag);
            item.setExpiration(expiration);

            c.getPlayer().forceReAddItem_Flag(item, type);
            used = true;
        }
        return used;
    }

    public boolean LucksKey() {
        boolean used = false;
        final MapleInventoryType type = MapleInventoryType.getByType((byte) slea.readInt());
        final Item item = c.getPlayer().getInventory(type).getItem((byte) slea.readInt());
        // another int here, lock = 5A E5 F2 0A, 7 day = D2 30 F3 0A
        if (item != null && item.getType() == 1) { // equip
            short flag = item.getFlag();
            flag |= ItemFlag.LUCKS_KEY.getValue();
            item.setFlag(flag);

            c.getPlayer().forceReAddItem_Flag(item, type);
            used = true;
        }
        return used;
    }

    public boolean ShieldingWard() {
        boolean used = false;
        System.out.println("slea..." + slea.toString());
        final MapleInventoryType type = MapleInventoryType.getByType((byte) slea.readInt());
        final Item item = c.getPlayer().getInventory(type).getItem((byte) slea.readInt());
        // another int here, lock = 5A E5 F2 0A, 7 day = D2 30 F3 0A
        if (item != null && item.getType() == 1) {
            // equip
            if (((Equip) item).getEnhance() >= 8) {
                // cannot be used

            }
            short flag = item.getFlag();
            flag |= ItemFlag.SHIELD_WARD.getValue();
            item.setFlag(flag);
            c.getPlayer().forceReAddItem_Flag(item, type);
            used = true;
        }
        return used;
    }

    public boolean Microwave_Peanut() {
        boolean used = false;
        Item item = c.getPlayer().getInventory(MapleInventoryType.ETC).findById(itemId == 5060003 ? 4170023 : 4170024);
        if (item == null || item.getQuantity() <= 0) { // hacking{
            return false;
        }
        if (getIncubatedItems(c, itemId)) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.ETC, item.getPosition(), (short) 1, false);
            used = true;
        }
        return used;
    }

    public boolean CheapMegaphone() {
        boolean used = false;
        // Megaphone
        if (c.getPlayer().getLevel() < 10) {
            c.getPlayer().dropMessage(5, "Must be level 10 or higher.");
        }
        if (c.getPlayer().getMapId() == GameConstants.JAIL) {
            c.getPlayer().dropMessage(5, "Cannot be used here.");
        }
        if (!c.getPlayer().getCheatTracker().canSmega()) {
            c.getPlayer().dropMessage(5, "You may only use this every 15 seconds.");
        }
        if (!c.getChannelServer().getMegaphoneMuteState()) {
            final String message = slea.readMapleAsciiString();
            if (message.length() > 65) {
            }
            final StringBuilder sb = new StringBuilder();
            addMedalString(c.getPlayer(), sb);
            sb.append(c.getPlayer().getName());
            sb.append(" : ");
            sb.append(message);
            c.getPlayer().getMap().broadcastMessage(CWvsContext.serverNotice(2, sb.toString()));
            used = true;
        } else {
            c.getPlayer().dropMessage(5, "The usage of Megaphone is currently disabled.");
        }
        return used;
    }

    public boolean Megaphone() {
        boolean used = false;
        if (c.getPlayer().getLevel() < 10) {
            c.getPlayer().dropMessage(5, "Must be level 10 or higher.");
        }
        if (c.getPlayer().getMapId() == GameConstants.JAIL) {
            c.getPlayer().dropMessage(5, "Cannot be used here.");
        }
        if (!c.getPlayer().getCheatTracker().canSmega()) {
            c.getPlayer().dropMessage(5, "You may only use this every 15 seconds.");
        }
        if (!c.getChannelServer().getMegaphoneMuteState()) {
            final String message = slea.readMapleAsciiString();
            if (message.length() > 65) {
            }
            final StringBuilder sb = new StringBuilder();
            addMedalString(c.getPlayer(), sb);
            sb.append(c.getPlayer().getName());
            sb.append(" : ");
            sb.append(message);
            c.getChannelServer().broadcastSmegaPacket(CWvsContext.serverNotice(2, sb.toString()));
            used = true;
        } else {
            c.getPlayer().dropMessage(5, "The usage of Megaphone is currently disabled.");
        }
        return used;
    }

    public boolean TripleMegaphone() {
        boolean used = false;
        if (c.getPlayer().getLevel() < 10) {
            c.getPlayer().dropMessage(5, "Must be level 10 or higher.");
        }
        if (c.getPlayer().getMapId() == GameConstants.JAIL) {
            c.getPlayer().dropMessage(5, "Cannot be used here.");
        }
        if (!c.getPlayer().getCheatTracker().canSmega()) {
            c.getPlayer().dropMessage(5, "You may only use this every 15 seconds.");
        }
        if (!c.getChannelServer().getMegaphoneMuteState()) {
            final byte numLines = slea.readByte();
            if (numLines > 3) {
                return false;
            }
            final List<String> messages = new LinkedList<String>();
            String message;
            for (int i = 0; i < numLines; i++) {
                message = slea.readMapleAsciiString();
                if (message.length() > 65) {
                    break;
                }
                messages.add(c.getPlayer().getName() + " : " + message);
            }
            final boolean ear = slea.readByte() > 0;

            World.Broadcast.broadcastSmega(CWvsContext.tripleSmega(messages, ear, c.getChannel()));
            used = true;
        } else {
            c.getPlayer().dropMessage(5, "The usage of Megaphone is currently disabled.");
        }
        return used;
    }

    public boolean EchoMegaphone() {
        boolean used = false;
        if (c.getPlayer().getLevel() < 10) {
            c.getPlayer().dropMessage(5, "Must be level 10 or higher.");
        }
        if (c.getPlayer().getMapId() == GameConstants.JAIL) {
            c.getPlayer().dropMessage(5, "Cannot be used here.");
        }
        if (!c.getPlayer().getCheatTracker().canSmega()) {
            c.getPlayer().dropMessage(5, "You may only use this every 15 seconds.");
        }
        if (!c.getChannelServer().getMegaphoneMuteState()) {
            final String message = slea.readMapleAsciiString();
            if (message.length() > 65) {
            }
            World.Broadcast.broadcastSmega(CWvsContext.echoMegaphone(c.getPlayer().getName(), message));
            used = true;
        } else {
            c.getPlayer().dropMessage(5, "The usage of Megaphone is currently disabled.");
        }
        return used;
    }

    public boolean HeartMegaphone() {
        boolean used = false;
        if (c.getPlayer().getLevel() < 10) {
            c.getPlayer().dropMessage(5, "Must be level 10 or higher.");
        }
        if (c.getPlayer().getMapId() == GameConstants.JAIL) {
            c.getPlayer().dropMessage(5, "Cannot be used here.");
        }
        if (!c.getPlayer().getCheatTracker().canSmega()) {
            c.getPlayer().dropMessage(5, "You may only use this every 15 seconds.");
        }
        if (!c.getChannelServer().getMegaphoneMuteState()) {
            final String message = slea.readMapleAsciiString();
            if (message.length() > 65) {
            }
            final StringBuilder sb = new StringBuilder();
            addMedalString(c.getPlayer(), sb);
            sb.append(c.getPlayer().getName());
            sb.append(" : ");
            sb.append(message);
            final boolean ear = slea.readByte() != 0;
            World.Broadcast.broadcastSmega(CWvsContext.serverNotice(9, c.getChannel(), sb.toString(), ear));
            used = true;
        } else {
            c.getPlayer().dropMessage(5, "The usage of Megaphone is currently disabled.");
        }
        return used;
    }

    public boolean SkullMegaphone() {
        boolean used = false;
        if (c.getPlayer().getLevel() < 10) {
            c.getPlayer().dropMessage(5, "Must be level 10 or higher.");
        }
        if (c.getPlayer().getMapId() == GameConstants.JAIL) {
            c.getPlayer().dropMessage(5, "Cannot be used here.");
        }
        if (!c.getPlayer().getCheatTracker().canSmega()) {
            c.getPlayer().dropMessage(5, "You may only use this every 15 seconds.");
        }
        if (!c.getChannelServer().getMegaphoneMuteState()) {
            final String message = slea.readMapleAsciiString();
            if (message.length() > 65) {
            }
            final StringBuilder sb = new StringBuilder();
            addMedalString(c.getPlayer(), sb);
            sb.append(c.getPlayer().getName());
            sb.append(" : ");
            sb.append(message);
            final boolean ear = slea.readByte() != 0;
            World.Broadcast.broadcastSmega(CWvsContext.serverNotice(22, c.getChannel(), sb.toString(), ear));
            used = true;
        } else {
            c.getPlayer().dropMessage(5, "The usage of Megaphone is currently disabled.");
        }
        return used;
    }

    public boolean SuperMegaphone() {
        boolean used = false;
        // Super Megaphone
        if (c.getPlayer().getLevel() < 10) {
            c.getPlayer().dropMessage(5, "Must be level 10 or higher.");
        }
        if (c.getPlayer().getMapId() == GameConstants.JAIL) {
            c.getPlayer().dropMessage(5, "Cannot be used here.");
        }
        if (!c.getPlayer().getCheatTracker().canSmega()) {
            c.getPlayer().dropMessage(5, "You may only use this every 15 seconds.");
        }
        if (!c.getChannelServer().getMegaphoneMuteState()) {
            final String message = slea.readMapleAsciiString();
            if (message.length() > 65) {
            }
            final StringBuilder sb = new StringBuilder();
            addMedalString(c.getPlayer(), sb);
            sb.append(c.getPlayer().getName());
            sb.append(" : ");
            sb.append(message);
            final boolean ear = slea.readByte() != 0;
            World.Broadcast.broadcastSmega(CWvsContext.serverNotice(3, c.getChannel(), sb.toString(), ear));
            used = true;
        } else {
            c.getPlayer().dropMessage(5, "The usage of Megaphone is currently disabled.");
        }
        return used;
    }

    public boolean ItemMegaphone() {
        boolean used = false;
        if (c.getPlayer().getLevel() < 10) {
            c.getPlayer().dropMessage(5, "Must be level 10 or higher.");
        }
        if (c.getPlayer().getMapId() == GameConstants.JAIL) {
            c.getPlayer().dropMessage(5, "Cannot be used here.");
        }
        if (!c.getPlayer().getCheatTracker().canSmega()) {
            c.getPlayer().dropMessage(5, "You may only use this every 15 seconds.");
        }
        if (!c.getChannelServer().getMegaphoneMuteState()) {
            final String message = slea.readMapleAsciiString();
            if (message.length() > 65) {
            }
            final StringBuilder sb = new StringBuilder();
            addMedalString(c.getPlayer(), sb);
            sb.append(c.getPlayer().getName());
            sb.append(" : ");
            sb.append(message);
            final boolean ear = slea.readByte() > 0;
            Item item = null;
            if (slea.readByte() == 1) { // item
                byte invType = (byte) slea.readInt();
                byte pos = (byte) slea.readInt();
                if (pos <= 0) {
                    invType = -1;
                }
                item = c.getPlayer().getInventory(MapleInventoryType.getByType(invType)).getItem(pos);
            }
            World.Broadcast.broadcastSmega(CWvsContext.itemMegaphone(sb.toString(), ear, c.getChannel(), item));
            used = true;
        } else {
            c.getPlayer().dropMessage(5, "The usage of Megaphone is currently disabled.");
        }
        return used;
    }

    public boolean Megassenger() {
        boolean used = false;
        if (c.getPlayer().getLevel() < 10) {
            c.getPlayer().dropMessage(5, "Must be level 10 or higher.");
        }
        if (c.getPlayer().getMapId() == GameConstants.JAIL) {
            c.getPlayer().dropMessage(5, "Cannot be used here.");
        }
        if (!c.getPlayer().getCheatTracker().canSmega()) {
            c.getPlayer().dropMessage(5, "You may only use this every 15 seconds.");
        }
        int tvType = itemId % 10;
        if (tvType == 3) {
            slea.readByte(); // who knows
        }
        boolean ear = tvType != 1 && tvType != 2 && slea.readByte() > 1; // for tvType 1/2, there is no byte.
        MapleCharacter victim = tvType == 1 || tvType == 4 ? null
                : c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString()); // for
        // tvType
        // 4,
        // there
        // is
        // no
        // string.
        if (tvType == 0 || tvType == 3) {
            // doesn't allow two
            victim = null;
        } else if (victim == null) {
            c.getPlayer().dropMessage(1, "That character is not in the channel.");
            return false;
        }
        String message = slea.readMapleAsciiString();
        World.Broadcast.broadcastSmega(
                CWvsContext.serverNotice(3, c.getChannel(), c.getPlayer().getName() + " : " + message, ear));
        used = true;
        return used;
    }

    public boolean Note() {
        boolean used = false;
        final String sendTo = slea.readMapleAsciiString();
        final String msg = slea.readMapleAsciiString();
        if (MapleCharacterUtil.canCreateChar(sendTo, false)) { // Name does not exist
            c.getSession().write(MTSCSPacket.OnMemoResult((byte) 5, (byte) 1));
        } else {
            int ch = World.Find.findChannel(sendTo);
            if (ch <= 0) { // offline
                c.getPlayer().sendNote(sendTo, msg);
                c.getSession().write(MTSCSPacket.OnMemoResult((byte) 4, (byte) 0));
                used = true;
            } else {
                c.getSession().write(MTSCSPacket.OnMemoResult((byte) 5, (byte) 0));
            }
        }
        return used;
    }

    public boolean PetFlags() {
        boolean used = false;
        final int uniqueid = (int) slea.readLong();
        MaplePet pet = c.getPlayer().getPet(0);
        int slo = 0;
        if (pet == null) {
        }
        if (pet.getUniqueId() != uniqueid) {
            pet = c.getPlayer().getPet(1);
            slo = 1;
            if (pet != null) {
                if (pet.getUniqueId() != uniqueid) {
                    pet = c.getPlayer().getPet(2);
                    slo = 2;
                    if (pet != null) {
                        if (pet.getUniqueId() != uniqueid) {
                        }
                    } else {
                    }
                }
            } else {
            }
        }
        PetFlag zz = PetFlag.getByAddId(itemId);
        if (zz != null && !zz.check(pet.getFlags())) {
            pet.setFlags(pet.getFlags() | zz.getValue());
            c.getSession().write(PetPacket.updatePet(pet,
                    c.getPlayer().getInventory(MapleInventoryType.CASH).getItem((byte) pet.getInventoryPosition()),
                    true));
            c.getSession().write(CWvsContext.enableActions());
            c.getSession().write(MTSCSPacket.changePetFlag(uniqueid, true, zz.getValue()));
            used = true;
        }
        return used;
    }

    public boolean PetFlag2() {
        boolean used = false;
        final int uniqueid = (int) slea.readLong();
        MaplePet pet = c.getPlayer().getPet(0);
        int slo = 0;
        if (pet == null) {
        }
        if (pet.getUniqueId() != uniqueid) {
            pet = c.getPlayer().getPet(1);
            slo = 1;
            if (pet != null) {
                if (pet.getUniqueId() != uniqueid) {
                    pet = c.getPlayer().getPet(2);
                    slo = 2;
                    if (pet != null) {
                        if (pet.getUniqueId() != uniqueid) {
                        }
                    } else {
                    }
                }
            } else {
            }
        }
        PetFlag zz = PetFlag.getByDelId(itemId);
        if (zz != null && zz.check(pet.getFlags())) {
            pet.setFlags(pet.getFlags() - zz.getValue());
            c.getSession().write(PetPacket.updatePet(pet,
                    c.getPlayer().getInventory(MapleInventoryType.CASH).getItem((byte) pet.getInventoryPosition()),
                    true));
            c.getSession().write(CWvsContext.enableActions());
            c.getSession().write(MTSCSPacket.changePetFlag(uniqueid, false, zz.getValue()));
            used = true;
        }
        return used;
    }

    public boolean KentasMagicRope() {
        boolean used = false;
        final Skill skil = SkillFactory.getSkill(slea.readInt());
        if (skil == null || skil.getId() / 10000 != 8000 || c.getPlayer().getSkillLevel(skil) <= 0
                || !skil.isTimeLimited() || GameConstants.getMountItem(skil.getId(), c.getPlayer()) <= 0) {
        }
        final long toAdd = (itemId == 5501001 ? 30 : 60) * 24 * 60 * 60 * 1000L;
        final long expire = c.getPlayer().getSkillExpiry(skil);
        if (expire < System.currentTimeMillis()
                || (long) (expire + toAdd) >= System.currentTimeMillis() + (365 * 24 * 60 * 60 * 1000L)) {
        }
        c.getPlayer().changeSingleSkillLevel(skil, c.getPlayer().getSkillLevel(skil),
                c.getPlayer().getMasterLevel(skil), (long) (expire + toAdd));
        used = true;
        return used;
    }

    public boolean PetNameTag() {
        boolean used = false;
        final int uniqueid = (int) slea.readLong();
        MaplePet pet = c.getPlayer().getPet(0);
        int slo = 0;
        if (pet == null) {
        }
        if (pet.getUniqueId() != uniqueid) {
            pet = c.getPlayer().getPet(1);
            slo = 1;
            if (pet != null) {
                if (pet.getUniqueId() != uniqueid) {
                    pet = c.getPlayer().getPet(2);
                    slo = 2;
                    if (pet != null) {
                        if (pet.getUniqueId() != uniqueid) {
                        }
                    } else {
                    }
                }
            } else {
            }
        }
        String nName = slea.readMapleAsciiString();
        for (String z : GameConstants.RESERVED) {
            if (pet.getName().contains(z) || nName.contains(z)) {
                break;
            }
        }
        if (MapleCharacterUtil.canChangePetName(nName)) {
            pet.setName(nName);
            c.getSession().write(PetPacket.updatePet(pet,
                    c.getPlayer().getInventory(MapleInventoryType.CASH).getItem((byte) pet.getInventoryPosition()),
                    true));
            c.getSession().write(CWvsContext.enableActions());
            c.getPlayer().getMap().broadcastMessage(MTSCSPacket.changePetName(c.getPlayer(), nName, slo));
            used = true;
        }
        return used;

    }

    public boolean AndroidNamingCoupon() {
        boolean used = false;
        slea.skip(8);
        if (c.getPlayer().getAndroid() == null) {
        }
        String nName = slea.readMapleAsciiString();
        for (String z : GameConstants.RESERVED) {
            if (c.getPlayer().getAndroid().getName().indexOf(z) != -1 || nName.indexOf(z) != -1) {
                break;
            }
        }
        if (MapleCharacterUtil.canChangePetName(nName)) {
            c.getPlayer().getAndroid().setName(nName);
            c.getPlayer().setAndroid(c.getPlayer().getAndroid()); // respawn it
            used = true;
        }
        return used;
    }

    public boolean PetSnacks() {
        boolean used = false;
        // Pet food
        MaplePet pet = c.getPlayer().getPet(0);
        if (pet == null) {
        }
        if (!pet.canConsume(itemId)) {
            pet = c.getPlayer().getPet(1);
            if (pet != null) {
                if (!pet.canConsume(itemId)) {
                    pet = c.getPlayer().getPet(2);
                    if (pet != null) {
                        if (!pet.canConsume(itemId)) {
                        }
                    } else {
                    }
                }
            } else {
            }
        }
        final byte petindex = c.getPlayer().getPetIndex(pet);
        pet.setFullness(100);
        if (pet.getCloseness() < 30000) {
            if (pet.getCloseness() + (100 * c.getChannelServer().getTraitRate()) > 30000) {
                pet.setCloseness(30000);
            } else {
                pet.setCloseness(Math.round(pet.getCloseness() + (100 * c.getChannelServer().getTraitRate())));
            }
            if (pet.getCloseness() >= GameConstants.getClosenessNeededForLevel(pet.getLevel() + 1)) {
                pet.setLevel(pet.getLevel() + 1);
                c.getSession().write(EffectPacket.showOwnPetLevelUp(c.getPlayer().getPetIndex(pet)));
                c.getPlayer().getMap().broadcastMessage(PetPacket.showPetLevelUp(c.getPlayer(), petindex));
            }
        }
        c.getSession().write(PetPacket.updatePet(pet,
                c.getPlayer().getInventory(MapleInventoryType.CASH).getItem(pet.getInventoryPosition()), true));
        c.getPlayer().getMap().broadcastMessage(c.getPlayer(),
                PetPacket.commandResponse(c.getPlayer().getId(), (byte) 1, petindex, true, true), true);
        used = true;
        return used;
    }

    public boolean OwlOfMinerva() {
        boolean used = false;
        final int itemSearch = slea.readInt();
        final List<HiredMerchant> hms = c.getChannelServer().searchMerchant(itemSearch);
        if (hms.size() > 0) {
            c.getSession().write(CWvsContext.getOwlSearched(itemSearch, hms));
            used = true;
        } else {
            c.getPlayer().dropMessage(1, "Unable to find the item.");
        }
        return used;
    }

    public boolean FloralScent_PassedGas() {
        boolean used = false;

        Rectangle bounds = new Rectangle((int) c.getPlayer().getPosition().getX(),
                (int) c.getPlayer().getPosition().getY(), 1, 1);
        MapleMist mist = new MapleMist(bounds, c.getPlayer());
        c.getPlayer().getMap().spawnMist(mist, 10000, true);
        c.getSession().write(CWvsContext.enableActions());
        used = true;
        return used;
    }

    public boolean Chalkboard() {

        for (MapleEventType t : MapleEventType.values()) {
            final MapleEvent e = ChannelServer.getInstance(c.getChannel()).getEvent(t);
            if (e.isRunning()) {
                for (int i : e.getType().mapids) {
                    if (c.getPlayer().getMapId() == i) {
                        c.getPlayer().dropMessage(5, "You may not use that here.");
                        c.getSession().write(CWvsContext.enableActions());
                        return false;
                    }
                }
            }
        }
        c.getPlayer().setChalkboard(slea.readMapleAsciiString());
        return true;
    }

    public boolean xMessenger() {
        boolean used = false;
        if (c.getPlayer().getLevel() < 10) {
            c.getPlayer().dropMessage(5, "Must be level 10 or higher.");
        }
        if (c.getPlayer().getMapId() == GameConstants.JAIL) {
            c.getPlayer().dropMessage(5, "Cannot be used here.");
        }
        if (!c.getPlayer().getCheatTracker().canAvatarSmega()) {
            c.getPlayer().dropMessage(5, "You may only use this every 5 minutes.");
        }
        if (!c.getChannelServer().getMegaphoneMuteState()) {
            final List<String> lines = new LinkedList<>();
            for (int i = 0; i < 4; i++) {
                final String text = slea.readMapleAsciiString();
                if (text.length() > 55) {
                    continue;
                }
                lines.add(text);
            }
            final boolean ear = slea.readByte() != 0;
            World.Broadcast
                    .broadcastSmega(CWvsContext.getAvatarMega(c.getPlayer(), c.getChannel(), itemId, lines, ear));
            used = true;
        } else {
            c.getPlayer().dropMessage(5, "The usage of Megaphone is currently disabled.");
        }
        return used;
    }

    public boolean MiuMiuTravelingMerchant() {
        boolean used = false;
        for (int i : GameConstants.blockedMaps) {
            if (c.getPlayer().getMapId() == i) {
                c.getPlayer().dropMessage(5, "You may not use this command here.");
                c.getSession().write(CWvsContext.enableActions());
                return false;
            }
        }
        if (c.getPlayer().getLevel() < 10) {
            c.getPlayer().dropMessage(5, "You must be over level 10 to use this command.");
        } else if (c.getPlayer().hasBlockedInventory() || c.getPlayer().getMap().getSquadByMap() != null
                || c.getPlayer().getEventInstance() != null || c.getPlayer().getMap().getEMByMap() != null
                || c.getPlayer().getMapId() >= 990000000) {
            c.getPlayer().dropMessage(5, "You may not use this command here.");
        } else if ((c.getPlayer().getMapId() >= 680000210 && c.getPlayer().getMapId() <= 680000502)
                || (c.getPlayer().getMapId() / 1000 == 980000 && c.getPlayer().getMapId() != 980000000)
                || (c.getPlayer().getMapId() / 100 == 1030008) || (c.getPlayer().getMapId() / 100 == 922010)
                || (c.getPlayer().getMapId() / 10 == 13003000)) {
            c.getPlayer().dropMessage(5, "You may not use this command here.");
        } else {
            MapleShopFactory.getInstance().getShop(61).sendShop(c);
        }
        // used = true;
        return used;
    }

    public boolean CashMorphs() {
        boolean used = false;
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        ii.getItemEffect(itemId).applyTo(c.getPlayer());
        used = true;
        return used;
    }

    public boolean DefaultActionForCashItem(byte slot) {
        boolean used = false;
        if (itemId / 10000 == 512) {
            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            String msg = ii.getMsg(itemId);
            final String ourMsg = slea.readMapleAsciiString();
            if (!msg.contains("%s")) {
                msg = ourMsg;
            } else {
                msg = msg.replaceFirst("%s", c.getPlayer().getName());
                if (!msg.contains("%s")) {
                    msg = ii.getMsg(itemId).replaceFirst("%s", ourMsg);
                } else {
                    try {
                        msg = msg.replaceFirst("%s", ourMsg);
                    } catch (Exception e) {
                        msg = ii.getMsg(itemId).replaceFirst("%s", ourMsg);
                    }
                }
            }
            c.getPlayer().getMap().startMapEffect(msg, itemId);

            final int buff = ii.getStateChangeItem(itemId);
            if (buff != 0) {
                c.getPlayer().getMap().getCharactersThreadsafe().forEach(mChar -> {
                    ii.getItemEffect(buff).applyTo(mChar);
                });
            }
            used = true;
        } else if (itemId / 10000 == 510) {
            c.getPlayer().getMap().startJukebox(c.getPlayer().getName(), itemId);
            used = true;
        } else if (itemId / 10000 == 520) {
            final int mesars = MapleItemInformationProvider.getInstance().getMeso(itemId);
            if (mesars > 0 && c.getPlayer().getMeso() < (Integer.MAX_VALUE - mesars)) {
                used = true;
                if (Math.random() > 0.1) {
                    final int gainmes = Randomizer.nextInt(mesars);
                    c.getPlayer().gainMeso(gainmes, false);
                    c.getSession().write(MTSCSPacket.sendMesobagSuccess(gainmes));
                } else {
                    c.getSession().write(MTSCSPacket.sendMesobagFailed(false)); // not random
                }
            }
        } else if (itemId / 10000 == 562) {
            if (UseSkillBook(slot, itemId, c, c.getPlayer())) {
                c.getPlayer().gainSP(1);
            } // this should handle removing
        } else if (itemId / 10000 == 553) {
            UseRewardItem(slot, itemId, c, c.getPlayer());// this too
        } else if (itemId / 10000 != 519) {
            System.out.println("Unhandled CS item : " + itemId);
            System.out.println(slea.toString(true));
        }
        return used;
    }

    public final boolean UseTeleRockDay() {
        boolean used = false;
        byte type = slea.readByte();
        if (type == 0) { // Rocktype

            final MapleMap target = c.getChannelServer().getMapFactory().getMap(slea.readInt());
            if (target == null) {
                c.getPlayer().dropMessage(1, "Map does not exist.");
                return false;
            }

            if (c.getPlayer().isRegRockMap(target.getId())) {
                // sure this map doesn't have a forced return map

                if (!FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit())
                        && !FieldLimitType.VipRock.check(target.getFieldLimit()) && !c.getPlayer().isInBlockedMap()) { // Makes

                    c.getPlayer().changeMap(target, target.getPortal(0));
                    used = true;
                } else {
                    c.getPlayer().dropMessage(1, "You cannot go to that place.");
                }
            } else {
                c.getPlayer().dropMessage(1, "This map is not registered.");
            }
        } else {
            final String name = slea.readMapleAsciiString();
            final MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(name);
            if (victim != null && !victim.isIntern() && c.getPlayer().getEventInstance() == null
                    && victim.getEventInstance() == null) {
                if (!FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit())
                        && !FieldLimitType.VipRock
                                .check(c.getChannelServer().getMapFactory().getMap(victim.getMapId()).getFieldLimit())
                        && !victim.isInBlockedMap() && !c.getPlayer().isInBlockedMap()) {
                    if (itemId == 5041000 || itemId == 5040004 || itemId == 5041001
                            || (victim.getMapId() / 100000000) == (c.getPlayer().getMapId() / 100000000)) { // Viprock
                        // or same
                        // continent
                        c.getPlayer().changeMap(victim.getMap(),
                                victim.getMap().findClosestPortal(victim.getTruePosition()));
                        used = true;
                    } else {
                        c.getPlayer().dropMessage(1, "You cannot go to that place.");
                    }
                } else {
                    c.getPlayer().dropMessage(1, "You cannot go to that place.");
                }
            } else {
                c.getPlayer().dropMessage(1,
                        "(" + name + ") is currently difficult to locate, so the teleport will not take place.");
            }
        }
        return used;
    }

}
