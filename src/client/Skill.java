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
package client;

import constants.GameConstants;
import java.util.ArrayList;
import java.util.List;

import provider.MapleData;
import provider.MapleDataTool;
import server.MapleStatEffect;
import server.Randomizer;
import server.life.Element;
import tools.Pair;

public class Skill {

    private String name = "";
    private final List<MapleStatEffect> effects = new ArrayList<MapleStatEffect>();
    private List<MapleStatEffect> pvpEffects = null;
    private List<Integer> animation = null;
    private final List<Pair<Integer, Byte>> requiredSkill = new ArrayList<Pair<Integer, Byte>>();
    private Element element = Element.NEUTRAL;
    private int id, animationTime = 0, masterLevel = 0, maxLevel = 0, delay = 0, trueMax = 0, eventTamingMob = 0, skillType = 0; //4 is alert
    private boolean invisible = false, chargeskill = false, timeLimited = false, combatOrders = false, pvpDisabled = false, magic = false, casterMove = false, pushTarget = false, pullTarget = false;

    public Skill(final int id) {
        super();
        this.id = id;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static final Skill loadFromData(final int id, final MapleData data, final MapleData delayData) {
        Skill ret = new Skill(id);

        boolean isBuff = false;
        final int skillType = MapleDataTool.getInt("skillType", data, -1);
        final String elem = MapleDataTool.getString("elemAttr", data, null);
        if (elem != null) {
            ret.element = Element.getFromChar(elem.charAt(0));
        }
        ret.skillType = skillType;
        ret.invisible = MapleDataTool.getInt("invisible", data, 0) > 0;
        ret.timeLimited = MapleDataTool.getInt("timeLimited", data, 0) > 0;
        ret.combatOrders = MapleDataTool.getInt("combatOrders", data, 0) > 0;
        ret.masterLevel = MapleDataTool.getInt("masterLevel", data, 0);
        ret.eventTamingMob = MapleDataTool.getInt("eventTamingMob", data, 0);
        final MapleData inf = data.getChildByPath("info");
        if (inf != null) {
            ret.pvpDisabled = MapleDataTool.getInt("pvp", inf, 1) <= 0;
            ret.magic = MapleDataTool.getInt("magicDamage", inf, 0) > 0;
            ret.casterMove = MapleDataTool.getInt("casterMove", inf, 0) > 0;
            ret.pushTarget = MapleDataTool.getInt("pushTarget", inf, 0) > 0;
            ret.pullTarget = MapleDataTool.getInt("pullTarget", inf, 0) > 0;
        }
        final MapleData effect = data.getChildByPath("effect");
        switch (skillType) {
            case 2 -> isBuff = true;
            case 3 -> {
                //final attack
                ret.animation = new ArrayList<>();
                ret.animation.add(0);
                isBuff = effect != null;
            }
            default -> {
                MapleData action_ = data.getChildByPath("action");
                final MapleData hit = data.getChildByPath("hit");
                final MapleData ball = data.getChildByPath("ball");
                boolean action = false;
                if (action_ == null) {
                    if (data.getChildByPath("prepare/action") != null) {
                        action_ = data.getChildByPath("prepare/action");
                        action = true;
                    }
                }
                isBuff = effect != null && hit == null && ball == null;
                if (action_ != null) {
                    String d = null;
                    if (action) { //prepare
                        d = MapleDataTool.getString(action_, null);
                    } else {
                        d = MapleDataTool.getString("0", action_, null);
                    }
                    if (d != null) {
                        isBuff |= d.equals("alert2");
                        final MapleData dd = delayData.getChildByPath(d);
                        if (dd != null) {
                            for (MapleData del : dd) {
                                ret.delay += Math.abs(MapleDataTool.getInt("delay", del, 0));
                            }
                            if (ret.delay > 30) { //then, faster(2) = (10+2)/16 which is basically 3/4
                                ret.delay = (int) Math.round(ret.delay * 11.0 / 16.0); //fastest(1) lolol
                                ret.delay -= (ret.delay % 30); //round to 30ms
                            }
                        }
                        if (SkillFactory.getDelay(d) != null) { //this should return true always
                            ret.animation = new ArrayList<>();
                            ret.animation.add(SkillFactory.getDelay(d));
                            if (!action) {
                                for (MapleData ddc : action_) {
                                    if (!MapleDataTool.getString(ddc, d).equals(d)) {
                                        String c = MapleDataTool.getString(ddc);
                                        if (SkillFactory.getDelay(c) != null) {
                                            ret.animation.add(SkillFactory.getDelay(c));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                switch (id) {
                    case 2301002, 2111003, 12111005, 22161003, 32121006, 11076, 2111002, 4211001, 2121001, 2221001, 2321001, 1076 -> // heal is alert2 but not overtime...
                        isBuff = false;
                    case 1004, 10001004, 20001004, 20011004, 80001000, 1026, 10001026, 20001026, 20011026, 20021026, 30001026, 30011026, 93, 10000093, 20000093, 20010093, 20020093, 30000093, 30010093, 9101004, 1111002, 4211003, 4111001, 15111002, 5111005, 5121003, 13111005, 21000000, 21101003, 5211001, 5211002, 5220002, 5001005, 15001003, 5211006, 5220011, 5110001, 15100004, 5121009, 15111005, 22121001, 22131001, 22141002, 2311006, 22151002, 22151003, 22161002, 22171000, 22171004, 22181000, 22181004, 22161004, 22181003, 4331003, 15101006, 15111006, 4321000, 1320009, 35120000, 35001002, 9001004, 4341002, 32001003, 32120000, 32111012, 32110000, 32101003, 32120001, 35101007, 35121006, 35001001, 35101009, 35121005, 35121013, 35111004, 33111003, 1211009, 1111007, 1311007, 32121003, 5111007, 5211007, 5311005, 5320007, 35111013, 32111006, 5120011, 5220012, 1220013, 33101006, 32110007, 32110008, 32110009, 32111005, 31121005, 35121003, 35121009, 35121010, 35111005, 35111001, 35111010, 35111009, 35111011, 35111002, 35101005, 3120006, 3220005, 2121009, 2120010, 2221009, 2220010, 2321010, 2320011, 5321003, 5321004, 80001089, 24101005, 24121009, 24121008 -> // poison mist
                        // maple warrior
                        isBuff = true;
                }
                // heal is alert2 but not overtime...
                // poison mist
                // Flame Gear
                // explosion
                // chakra
                // Big bang
                // Big bang
                // Big bang
                // monster riding
                // hide is a buff -.- atleast for us o.o"
                // combo
                // pickpocket
                // mesoup
                // Super Transformation
                // Transformation
                // Super Transformation
                // Alabtross
                // Aran Combo
                // Body Pressure
                // Pirate octopus summon
                // wrath of the octopi
                //dash
                //homing beacon
                //bullseye
                //energy charge
                //speed infusion
                //element reset
                //magic shield
                //magic booster
                //magic booster
                //killer wing
                //magic resist
                //imprint
                //maple warrior
                //hero will
                //onyx blessing
                //soul stone
                //case 22121000:
                //case 22141003:
                //case 22151001:
                //case 22161002:
                //owl spirit
                //spark
                //spark
                //tornado spin
                //beholder's buff.. passive
                //TEMP. mech
                // hide
                //dark aura
                //blue aura
                //yellow aura
                //perfect armor
                //satellite safety
                //flame
                //missile
                //siege
                //puppet ?
                //magic,armor,atk crash
                //twister
                //dice
                //jaguar oshi
                // booster
                // hero's will
            }
        }
        ret.chargeskill = data.getChildByPath("keydown") != null;
        //some skills have old system, some new
        final MapleData level = data.getChildByPath("common");
        if (level != null) {
            ret.maxLevel = MapleDataTool.getInt("maxLevel", level, 1); //10 just a failsafe, shouldn't actually happens
            ret.trueMax = ret.maxLevel + (ret.combatOrders ? 2 : 0);
            for (int i = 1; i <= ret.trueMax; i++) {
                ret.effects.add(MapleStatEffect.loadSkillEffectFromData(level, id, isBuff, i, "x"));
            }

        } else {
            for (final MapleData leve : data.getChildByPath("level")) {
                ret.effects.add(MapleStatEffect.loadSkillEffectFromData(leve, id, isBuff, Byte.parseByte(leve.getName()), null));
            }
            ret.maxLevel = ret.effects.size();
            ret.trueMax = ret.effects.size();
        }
        final MapleData level2 = data.getChildByPath("PVPcommon");
        if (level2 != null) {
            ret.pvpEffects = new ArrayList<>();
            for (int i = 1; i <= ret.trueMax; i++) {
                ret.pvpEffects.add(MapleStatEffect.loadSkillEffectFromData(level2, id, isBuff, i, "x"));
            }
        }
        final MapleData reqDataRoot = data.getChildByPath("req");
        if (reqDataRoot != null) {
            reqDataRoot.getChildren().forEach(reqData -> {
                ret.requiredSkill.add(new Pair<>(Integer.parseInt(reqData.getName()), (byte) MapleDataTool.getInt(reqData, 1)));
            });
        }
        ret.animationTime = 0;
        if (effect != null) {
            for (final MapleData effectEntry : effect) {
                ret.animationTime += MapleDataTool.getIntConvert("delay", effectEntry, 0);
            }
        }
        return ret;
    }

    public MapleStatEffect getEffect(final int level) {
        if (effects.size() < level) {
            if (effects.size() > 0) { //incAllskill
                return effects.get(effects.size() - 1);
            }
            return null;
        } else if (level <= 0) {
            return effects.get(0);
        }
        return effects.get(level - 1);
    }

    public MapleStatEffect getPVPEffect(final int level) {
        if (pvpEffects == null) {
            return getEffect(level);
        }
        if (pvpEffects.size() < level) {
            if (pvpEffects.size() > 0) { //incAllskill
                return pvpEffects.get(pvpEffects.size() - 1);
            }
            return null;
        } else if (level <= 0) {
            return pvpEffects.get(0);
        }
        return pvpEffects.get(level - 1);
    }

    public int getSkillType() {
        return skillType;
    }

    public List<Integer> getAllAnimation() {
        return animation;
    }

    public int getAnimation() {
        if (animation == null) {
            return -1;
        }
        return animation.get(Randomizer.nextInt(animation.size()));
    }

    public boolean isPVPDisabled() {
        return pvpDisabled;
    }

    public boolean isChargeSkill() {
        return chargeskill;
    }

    public boolean isInvisible() {
        return invisible;
    }

    public boolean hasRequiredSkill() {
        return requiredSkill.size() > 0;
    }

    public List<Pair<Integer, Byte>> getRequiredSkills() {
        return requiredSkill;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getTrueMax() {
        return trueMax;
    }

    public boolean combatOrders() {
        return combatOrders;
    }

    public boolean canBeLearnedBy(int job) {
        int jid = job;
        int skillForJob = id / 10000;
        if (skillForJob == 2001) {
            return GameConstants.isEvan(job); //special exception for beginner -.-
        } else if (skillForJob == 0) {
            return GameConstants.isAdventurer(job); //special exception for beginner
        } else if (skillForJob == 1000) {
            return GameConstants.isKOC(job); //special exception for beginner
        } else if (skillForJob == 2000) {
            return GameConstants.isAran(job); //special exception for beginner
        } else if (skillForJob == 3000) {
            return GameConstants.isResist(job); //special exception for beginner
        } else if (skillForJob == 1) {
            return GameConstants.isCannon(job); //special exception for beginner
        } else if (skillForJob == 3001) {
            return GameConstants.isDemon(job); //special exception for beginner
        } else if (skillForJob == 2002) {
            return GameConstants.isMercedes(job); //special exception for beginner
        } else if (jid / 100 != skillForJob / 100) { // wrong job
            return false;
        } else if (jid / 1000 != skillForJob / 1000) { // wrong job
            return false;
        } else if (GameConstants.isCannon(skillForJob) && !GameConstants.isCannon(job)) {
            return false;
        } else if (GameConstants.isDemon(skillForJob) && !GameConstants.isDemon(job)) {
            return false;
        } else if (GameConstants.isAdventurer(skillForJob) && !GameConstants.isAdventurer(job)) {
            return false;
        } else if (GameConstants.isKOC(skillForJob) && !GameConstants.isKOC(job)) {
            return false;
        } else if (GameConstants.isAran(skillForJob) && !GameConstants.isAran(job)) {
            return false;
        } else if (GameConstants.isEvan(skillForJob) && !GameConstants.isEvan(job)) {
            return false;
        } else if (GameConstants.isMercedes(skillForJob) && !GameConstants.isMercedes(job)) {
            return false;
        } else if (GameConstants.isResist(skillForJob) && !GameConstants.isResist(job)) {
            return false;
        } else if ((jid / 10) % 10 == 0 && (skillForJob / 10) % 10 > (jid / 10) % 10) { // wrong 2nd job
            return false;
        } else if ((skillForJob / 10) % 10 != 0 && (skillForJob / 10) % 10 != (jid / 10) % 10) { //wrong 2nd job
            return false;
        } else if (skillForJob % 10 > jid % 10) { // wrong 3rd/4th job
            return false;
        }
        return true;
    }

    public boolean isTimeLimited() {
        return timeLimited;
    }

    public boolean isFourthJob() {
		switch (id) { // I guess imma make an sql table to store these, so that we could max them all out.
			case 3220010:
			case 3120011:
			case 33120010:
			case 32120009:
			case 5321006:
			case 21120011:
			case 22181004:
			case 4340010:
			case 22111001:
			case 22140000:
			case 22141002:
				return true;
		}			
        //resurrection has master level while ult.strafe does not.. wtf, impossible to tell from WZ
        if ((id / 10000) == 2312) { //all 10 skills.
            return true;
        }
        if ((getMaxLevel() <= 15 && !invisible && getMasterLevel() <= 0)) {
            return false;
        }
        if (id / 10000 >= 2212 && id / 10000 < 3000) { //evan skill
            return ((id / 10000) % 10) >= 7;
        }
        if (id / 10000 >= 430 && id / 10000 <= 434) { //db skill
            return ((id / 10000) % 10) == 4 || getMasterLevel() > 0;
        }
        return ((id / 10000) % 10) == 2 && id < 90000000 && !isBeginnerSkill();
    }

    public Element getElement() {
        return element;
    }

    public int getAnimationTime() {
        return animationTime;
    }

    public int getMasterLevel() {
        return masterLevel;
    }

    public int getDelay() {
        return delay;
    }

    public int getTamingMob() {
        return eventTamingMob;
    }

    public boolean isBeginnerSkill() {
        int jobId = id / 10000;
        return GameConstants.isBeginnerJob(jobId);
    }

    public boolean isMagic() {
        return magic;
    }

    public boolean isMovement() {
        return casterMove;
    }

    public boolean isPush() {
        return pushTarget;
    }

    public boolean isPull() {
        return pullTarget;
    }

    public boolean isSpecialSkill() {
        int jobId = id / 10000;
        return jobId == 900 || jobId == 800 || jobId == 9000 || jobId == 9200 || jobId == 9201 || jobId == 9202 || jobId == 9203 || jobId == 9204;
    }
}
