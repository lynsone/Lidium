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
package server.life;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import client.inventory.MapleInventoryType;
import constants.GameConstants;
import database.DatabaseConnection;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import server.MapleItemInformationProvider;
import server.StructFamiliar;
import server.ThreadManager;

public class MapleMonsterInformationProvider {

    private static final MapleMonsterInformationProvider instance = new MapleMonsterInformationProvider();
    private final Map<Integer, List<MonsterDropEntry>> drops = new HashMap<>();
    private final List<MonsterGlobalDropEntry> globaldrops = new ArrayList<>();
    private final List<MonsterLevelDropEntry> leveldrops = new ArrayList<MonsterLevelDropEntry>();
    private static final MapleDataProvider stringDataWZ = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/String.wz"));
    private static final MapleData mobStringData = stringDataWZ.getData("MonsterBook.img");
    private final Map<Integer, String> mobNameCache = new HashMap<>();

    public static MapleMonsterInformationProvider getInstance() {
        return instance;
    }

    public List<MonsterGlobalDropEntry> getGlobalDrop() {
        return globaldrops;
    }

    public List<MonsterLevelDropEntry> getLevelDrop() {
        return leveldrops;
    }

    public void load() {

        long start = System.currentTimeMillis();
        Map<Integer, List<MonsterDropEntry>> tmpDropInfo = new HashMap<>();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            final Connection con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("SELECT * FROM drop_data_global WHERE chance > 0");
            rs = ps.executeQuery();

            while (rs.next()) {
                globaldrops.add(new MonsterGlobalDropEntry(rs.getInt("itemid"), rs.getInt("chance"),
                        rs.getInt("continent"), rs.getByte("dropType"), rs.getInt("minimum_quantity"),
                        rs.getInt("maximum_quantity"), rs.getInt("questid")));
            }
            rs.close();
            ps.close();
            /*
            ps = con.prepareStatement("SELECT * FROM drop_data_level WHERE chance > 0");
            rs = ps.executeQuery();

            while (rs.next()) {
                leveldrops.add(new MonsterLevelDropEntry(rs.getInt("itemid"), rs.getInt("chance"),
                        rs.getInt("moblevel"), rs.getByte("dropType"), rs.getInt("minimum_quantity"),
                        rs.getInt("maximum_quantity"), rs.getInt("questid")));
            }
            rs.close();
            ps.close();
            */
            ps = con.prepareStatement("SELECT * FROM drop_data");
            rs = ps.executeQuery();
            while (rs.next()) {
                int dropperId = rs.getInt("dropperid");
                List<MonsterDropEntry> dropList;
                if (tmpDropInfo.containsKey(dropperId)) {
                    dropList = tmpDropInfo.get(dropperId);
                } else {
                    dropList = new ArrayList();
                }
                dropList.add(new MonsterDropEntry(rs.getInt("itemid"),
                        rs.getInt("chance"),
                        rs.getInt("minimum_quantity"),
                        rs.getInt("maximum_quantity"),
                        rs.getInt("questid")));
                tmpDropInfo.put(dropperId, dropList);
            }
            boolean hasMeso;
            for (Entry<Integer, List<MonsterDropEntry>> entry : tmpDropInfo.entrySet()) {
                hasMeso = false;
                for (MonsterDropEntry dropEntry : entry.getValue()) {
                    if (dropEntry.getItemId() == 0) {
                        hasMeso = true;
                        break;
                    }
                }
                if (!hasMeso) {
                    final MapleMonsterStats mons = MapleLifeFactory.getMonsterStats(entry.getKey());
                    if (mons != null) {
                        addMeso(mons, entry.getValue());
                    }
                }
            }
            drops.putAll(tmpDropInfo);
        } catch (SQLException e) {
            System.err.println("Error retrieving drop" + e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ignore) {
            }
        }
        System.out.println("Drop loaded in " + (System.currentTimeMillis() - start) + "ms.");

    }

    public List<MonsterDropEntry> retrieveDrop(final int monsterId) {
        return drops.get(monsterId);
    }

    private void loadDrop(Connection con, final int monsterId) {

        final ArrayList<MonsterDropEntry> ret = new ArrayList<MonsterDropEntry>();

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            final MapleMonsterStats mons = MapleLifeFactory.getMonsterStats(monsterId);
            if (mons == null) {
                return;
            }
            ps = con.prepareStatement("SELECT * FROM drop_data WHERE dropperid = ?");
            ps.setInt(1, monsterId);
            rs = ps.executeQuery();
            int itemid;
            int chance;
            boolean doneMesos = false;
            while (rs.next()) {
                itemid = rs.getInt("itemid");
                chance = rs.getInt("chance");
                if (GameConstants.getInventoryType(itemid) == MapleInventoryType.EQUIP) {
                    chance *= 10; // in GMS/SEA it was raised
                }
                ret.add(new MonsterDropEntry(itemid, chance, rs.getInt("minimum_quantity"),
                        rs.getInt("maximum_quantity"), rs.getInt("questid")));
                if (itemid == 0) {
                    doneMesos = true;
                }
            }
            if (!doneMesos) {
                addMeso(mons, ret);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ignore) {
                return;
            }
        }
        drops.put(monsterId, ret);

    }

    public void addExtra() {
        ThreadManager.getInstance().newTask(() -> {
            long start = System.currentTimeMillis();
            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            for (Entry<Integer, List<MonsterDropEntry>> e : drops.entrySet()) {
                for (int i = 0; i < e.getValue().size(); i++) {
                    if (e.getValue().get(i).getItemId() != 0 && !ii.itemExists(e.getValue().get(i).getItemId())) {
                        e.getValue().remove(i);
                    }
                }
                final MapleMonsterStats mons = MapleLifeFactory.getMonsterStats(e.getKey());
                Integer item = ii.getItemIdByMob(e.getKey());
                if (item != null && item.intValue() > 0) {
                    e.getValue().add(new MonsterDropEntry(item.intValue(), mons.isBoss() ? 1000000 : 10000, 1, 1, 0));
                }
                StructFamiliar f = ii.getFamiliarByMob(e.getKey().intValue());
                if (f != null) {
                    e.getValue().add(new MonsterDropEntry(f.itemid, mons.isBoss() ? 10000 : 100, 1, 1, 0));
                }
            }
            for (Entry<Integer, Integer> i : ii.getMonsterBook().entrySet()) {
                if (!drops.containsKey(i.getKey())) {
                    final MapleMonsterStats mons = MapleLifeFactory.getMonsterStats(i.getKey());
                    ArrayList<MonsterDropEntry> e = new ArrayList<MonsterDropEntry>();
                    e.add(new MonsterDropEntry(i.getValue().intValue(), mons.isBoss() ? 1000000 : 10000, 1, 1, 0));
                    StructFamiliar f = ii.getFamiliarByMob(i.getKey().intValue());
                    if (f != null) {
                        e.add(new MonsterDropEntry(f.itemid, mons.isBoss() ? 10000 : 100, 1, 1, 0));
                    }
                    addMeso(mons, e);

                    drops.put(i.getKey(), e);
                }
            }
            for (StructFamiliar f : ii.getFamiliars().values()) {
                if (!drops.containsKey(f.mob)) {
                    MapleMonsterStats mons = MapleLifeFactory.getMonsterStats(f.mob);
                    ArrayList<MonsterDropEntry> e = new ArrayList<MonsterDropEntry>();
                    e.add(new MonsterDropEntry(f.itemid, mons.isBoss() ? 10000 : 100, 1, 1, 0));
                    addMeso(mons, e);
                    drops.put(f.mob, e);
                }
            }
            if (GameConstants.GMS) { // kinda costly, i advise against !reloaddrops often
                for (Entry<Integer, List<MonsterDropEntry>> e : drops.entrySet()) { // yes, we're going through it
                    // twice
                    if (e.getKey() == 100000 && mobStringData.getChildByPath(String.valueOf(e.getKey())) != null) {
                        for (MapleData d : mobStringData.getChildByPath(e.getKey() + "/reward")) {
                            final int toAdd = MapleDataTool.getInt(d, 0);
                            if (toAdd == 0 && !contains(e.getValue(), toAdd) && ii.itemExists(toAdd)) {
                                e.getValue().add(new MonsterDropEntry(toAdd, chanceLogic(toAdd), 1, 1, 0));
                            }
                        }
                    }
                }
            }
            System.out.println("Extra Mob Info loaded in " + (System.currentTimeMillis() - start) + "ms.");

        });

    }

    public void addMeso(MapleMonsterStats mons, List<MonsterDropEntry> ret) {
        final double divided = (mons.getLevel() < 100 ? (mons.getLevel() < 10 ? (double) mons.getLevel() : 10.0)
                : (mons.getLevel() / 10.0));
        final int max = mons.isBoss() && !mons.isPartyBonus() ? (mons.getLevel() * mons.getLevel())
                : (mons.getLevel() * (int) Math.ceil(mons.getLevel() / divided));
        for (int i = 0; i < mons.dropsMeso(); i++) {
            ret.add(new MonsterDropEntry(0,
                    mons.isBoss() && !mons.isPartyBonus() ? 1000000 : (mons.isPartyBonus() ? 100000 : 200000),
                    (int) Math.floor(0.66 * max), max, 0));
        }
    }

    public void clearDrops() {
        drops.clear();
        globaldrops.clear();
        leveldrops.clear();
        load();
        addExtra();
    }

    public boolean contains(List<MonsterDropEntry> e, int toAdd) {
        for (MonsterDropEntry f : e) {
            if (f.getItemId() == toAdd) {
                return true;
            }
        }
        return false;
    }

    public int chanceLogic(int itemId) { // not much logic in here. most of the drops should already be there anyway.
        if (GameConstants.getInventoryType(itemId) == MapleInventoryType.EQUIP) {
            return 50000; // with *10
        } else if (GameConstants.getInventoryType(itemId) == MapleInventoryType.SETUP
                || GameConstants.getInventoryType(itemId) == MapleInventoryType.CASH) {
            return 500;
        } else {
            switch (itemId / 10000) {
                case 204:
                case 207:
                case 233:
                case 229:
                    return 500;
                case 401:
                case 402:
                    return 5000;
                case 403:
                    return 5000; // lol
            }
            return 20000;
        }
    }
    // MESO DROP: level * (level / 10) = max, min = 0.66 * max
    // explosive Reward = 7 meso drops
    // boss, ffaloot = 2 meso drops
    // boss = level * level = max
    // no mesos if: mobid / 100000 == 97 or 95 or 93 or 91 or 90 or removeAfter > 0
    // or invincible or onlyNormalAttack or friendly or dropitemperiod > 0 or cp > 0
    // or point > 0 or fixeddamage > 0 or selfd > 0 or mobType != null and
    // mobType.charat(0) == 7 or PDRate <= 0

    public String getMobNameFromId(int id) {

        String mobName = mobNameCache.get(id);
        if (mobName == null) {
            MapleDataProvider dataProvider = MapleDataProviderFactory.getDataProvider(new File("wz/String.wz"));
            MapleData mobData = dataProvider.getData("Mob.img");

            mobName = MapleDataTool.getString(mobData.getChildByPath(id + "/name"), "");
            mobNameCache.put(id, mobName);
        }

        return mobName;
    }
}
