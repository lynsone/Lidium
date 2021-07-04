package client.messages.commands.intern;

import client.MapleClient;
import client.Skill;
import client.SkillFactory;
import client.messages.Command;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import server.ItemInformation;
import server.MapleItemInformationProvider;
import server.quest.MapleQuest;
import tools.Pair;
import tools.StringUtil;

public class SearchCommand extends Command{
    
    @Override
    public void execute(MapleClient c, String[] splitted){
        if(splitted.length == 0){           
            c.getPlayer().dropMessage(6, "!search <NPC> <MOB> <ITEM> <MAP> <SKILL> <QUEST>");
        }else if (splitted.length == 1){
                c.getPlayer().dropMessage(6, "Provide something to search.");
        }else{       
            String type = splitted[0];
            String search = StringUtil.joinStringFrom(splitted, 1);
            MapleData data = null;
            MapleDataProvider dataProvider = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/" + "String.wz"));
            c.getPlayer().dropMessage(6, "<<Type: " + type + " | Search: " + search + ">>");

            if (type.equalsIgnoreCase("NPC")) {
                List<String> retNpcs = new ArrayList<>();
                data = dataProvider.getData("Npc.img");
                List<Pair<Integer, String>> npcPairList = new LinkedList<>();
                for (MapleData npcIdData : data.getChildren()) {
                    npcPairList.add(new Pair<>(Integer.parseInt(npcIdData.getName()), MapleDataTool.getString(npcIdData.getChildByPath("name"), "NO-NAME")));
                }
                for (Pair<Integer, String> npcPair : npcPairList) {
                    if (npcPair.getRight().toLowerCase().contains(search.toLowerCase())) {
                        retNpcs.add(npcPair.getLeft() + " - " + npcPair.getRight());
                    }
                }
                if (retNpcs != null && retNpcs.size() > 0) {
                    for (String singleRetNpc : retNpcs) {
                        c.getPlayer().dropMessage(6, singleRetNpc);
                    }
                } else {
                    c.getPlayer().dropMessage(6, "No NPC's Found");
                }

            } else if (type.equalsIgnoreCase("MAP")) {
                List<String> retMaps = new ArrayList<>();
                data = dataProvider.getData("Map.img");
                List<Pair<Integer, String>> mapPairList = new LinkedList<>();
                for (MapleData mapAreaData : data.getChildren()) {
                    for (MapleData mapIdData : mapAreaData.getChildren()) {
                        mapPairList.add(new Pair<>(Integer.parseInt(mapIdData.getName()), MapleDataTool.getString(mapIdData.getChildByPath("streetName"), "NO-NAME") + " - " + MapleDataTool.getString(mapIdData.getChildByPath("mapName"), "NO-NAME")));
                    }
                }
                for (Pair<Integer, String> mapPair : mapPairList) {
                    if (mapPair.getRight().toLowerCase().contains(search.toLowerCase())) {
                        retMaps.add(mapPair.getLeft() + " - " + mapPair.getRight());
                    }
                }
                if (retMaps != null && retMaps.size() > 0) {
                    for (String singleRetMap : retMaps) {
                        c.getPlayer().dropMessage(6, singleRetMap);
                    }
                } else {
                    c.getPlayer().dropMessage(6, "No Maps Found");
                }
            } else if (type.equalsIgnoreCase("MOB")) {
                List<String> retMobs = new ArrayList<>();
                data = dataProvider.getData("Mob.img");
                List<Pair<Integer, String>> mobPairList = new LinkedList<>();
                for (MapleData mobIdData : data.getChildren()) {
                    mobPairList.add(new Pair<>(Integer.parseInt(mobIdData.getName()), MapleDataTool.getString(mobIdData.getChildByPath("name"), "NO-NAME")));
                }
                for (Pair<Integer, String> mobPair : mobPairList) {
                    if (mobPair.getRight().toLowerCase().contains(search.toLowerCase())) {
                        retMobs.add(mobPair.getLeft() + " - " + mobPair.getRight());
                    }
                }
                if (retMobs != null && retMobs.size() > 0) {
                    for (String singleRetMob : retMobs) {
                        c.getPlayer().dropMessage(6, singleRetMob);
                    }
                } else {
                    c.getPlayer().dropMessage(6, "No Mobs Found");
                }

            } else if (type.equalsIgnoreCase("ITEM")) {
                List<String> retItems = new ArrayList<>();
                for (ItemInformation itemPair : MapleItemInformationProvider.getInstance().getAllItems()) {
                    if (itemPair != null && itemPair.name != null && itemPair.name.toLowerCase().contains(search.toLowerCase())) {
                        retItems.add(itemPair.itemId + " - " + itemPair.name);
                    }
                }
                if (retItems != null && retItems.size() > 0) {
                    for (String singleRetItem : retItems) {
                        c.getPlayer().dropMessage(6, singleRetItem);
                    }
                } else {
                    c.getPlayer().dropMessage(6, "No Items Found");
                }
            } else if (type.equalsIgnoreCase("QUEST")) {
                List<String> retItems = new ArrayList<>();
                for (MapleQuest itemPair : MapleQuest.getAllInstances()) {
                    if (itemPair.getName().length() > 0 && itemPair.getName().toLowerCase().contains(search.toLowerCase())) {
                        retItems.add(itemPair.getId() + " - " + itemPair.getName());
                    }
                }
                if (retItems != null && retItems.size() > 0) {
                    for (String singleRetItem : retItems) {
                        c.getPlayer().dropMessage(6, singleRetItem);
                    }
                } else {
                    c.getPlayer().dropMessage(6, "No Quests Found");
                }
            } else if (type.equalsIgnoreCase("SKILL")) {
                List<String> retSkills = new ArrayList<>();
                for (Skill skil : SkillFactory.getAllSkills()) {
                    if (skil.getName() != null && skil.getName().toLowerCase().contains(search.toLowerCase())) {
                        retSkills.add(skil.getId() + " - " + skil.getName());
                    }
                }
                if (retSkills != null && retSkills.size() > 0) {
                    for (String singleRetSkill : retSkills) {
                        c.getPlayer().dropMessage(6, singleRetSkill);
                    }
                } else {
                    c.getPlayer().dropMessage(6, "No Skills Found");
                }
            } else {
                c.getPlayer().dropMessage(6, "Sorry, that search call is unavailable");
            }
        }
    }
}