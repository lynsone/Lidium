/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>
 
 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as
 published by the Free Software Foundation version 3 as published by
 the Free Software Foundation. You may not use, modify or distribute
 this program under any other version of the GNU Affero General Public
 License.
 
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.
 
 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

importPackage(Packages.client);
importPackage(Packages.tools);
importPackage(Packages.server.life);

var status;
var option = 0;
var item = 0;
var mPoint = 0;
var amount = 0;
var slots = new Array(-1, -2, -3, -4, -5, -6, -7, -8, -9, -10, -11, -12, -13, -15, -16, -50, -112, -113, -115, -116);
var cost = 0;


function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode < 0)
        cm.dispose();
    else {
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0 && mode == 1) {
            mPoint = cm.getPlayer().getCashShop().getCash(2);
            //(#b10% off#k) - sale tag
            cm.sendSimple("How would you like to spend your Maple Points?\r\n\You currently have " + mPoint + " Maple Points.\r\n\#L1#Convert MP to NX#l\r\n#L15#Spawn Frenzy Totem - 50 MP#l\r\n\#L2#Spawn Guardian Totem - 50 MP#l\r\n\#L3#Spawn Raging Totem - 50 MP#l\r\n\#L4#Buy GM Buff - 5 MP#l\r\n#L5#Use NX Gachapon - 5 MP#l\r\n");
        } else if (status == 1) {
            option = selection;

            if (option == 4) {// gm buff
                if (mPoint > 0) {
                    if (mPoint >= 1) {
                        cm.sendYesNo("Would you like to purchase a GM Buff? This is non refundable.");
                    } else {
                        cm.sendOk("Sorry, you dont seem to have any maple points.");
                        cm.dispose();
                    }
                }
            }

			if (option == 5) {//gach
                if (mPoint >= 10) {
                    cm.sendYesNo("NX Gacha gives 5 Random NX Equips and 1 Random NX Weapon.\r\n Do you confirm you want to spend 5 MP for this?");
                } else {
                    cm.sendOk("Sorry, you dont seem to have any maple points.");
                    cm.dispose();
                }
            }
			
            if (option == 15) {//frenzy totem
                cost = 50;//100 base
                if (mPoint >= cost) {
                    cm.sendYesNo("Are you sure you want to spend "+cost+" MP for a Frenzy Totem? This totem increases spawn amount and counts by 50%.");
                } else {
                    cm.sendOk("Sorry, you dont seem to have enough maple points.");
                    cm.dispose();
                }
            }

            if (option == 2) {//guardian totem
                cost = 50;//250 base
                if (mPoint >= cost) {
                    cm.sendYesNo("Are you sure you want to spend "+cost+" MP for a Guardin Totem? This totem reduces all damage by 50%.");
                } else {
                    cm.sendOk("Sorry, you dont seem to have enough maple points.");
                    cm.dispose();
                }
            }

            if (option == 3) {//raging totem
                cost = 50;//500 base
                if (mPoint >= cost) {
                    cm.sendYesNo("Are you sure you want to spend "+cost+" MP for a Frenzy Totem? This totem forces all monsters to spawn where totem is spawned.");
                } else {
                    cm.sendOk("Sorry, you dont seem to have enough maple points.");
                    cm.dispose();
                }
            }

            if (option == 4) {//gm buff
                cm.getPlayer().getCashShop().gainCash(2, -1);
                cm.getPlayer().basicBuff();
                cm.sendOk("5 Maple point have been removed from your account. Enjoy your GM Buff.");
                cm.dispose();
            }
  
            if (option == 5) {
                if (cm.getPlayer().getInventory(Packages.client.inventory.MapleInventoryType.SETUP).getNumFreeSlot() >= 1 && cm.getPlayer().getInventory(Packages.client.inventory.MapleInventoryType.EQUIP).getNumFreeSlot() >= 6) {

                    var text = "You have recieved the following items:\r\n\r\n";
                    text += "NX Equips:\r\n";
                    for (var i = 0; i < 5; i++) {
                        var nx = cm.getRandomNx();
                        cm.gainItem(nx, 1);
                        text += "#i" + nx + "#";
                    }
                    text += "\r\n\r\nNX Weapon:\r\n";
                    var weapon = cm.getRandomNxWeapon();
                    cm.gainItem(weapon, 1);
                    text += "#i" + weapon + "#";

                    var chance = Randomizer.rand(1, 5);
                    if (chance == 1) {
                        text += "\r\n\r\nLegendary Chair:\r\n";
                        var chair = cm.getRandomChair();
                        cm.gainItem(chair, 1);
                        text += "#i" + chair + "#";
                    }

                    cm.getPlayer().getCashShop().gainCash(2, -10);
                    cm.sendOk(text);
                    cm.dispose();
                } else {
                    cm.sendOk("Not Enough space. Requires 6 free equip slots and 1 setup slot.");
                    cm.dispose();
                }
            }
			
            if (option == 15) {
                    cm.getPlayer().getCashShop().gainCash(2, -cost);
                    cm.sendOk("You have spawned a Frenzy Totem.");
                    cm.dispose();
            }
			
            if (option == 2) {
                    cm.getPlayer().getCashShop().gainCash(2, -cost);
                    cm.sendOk("You have spawned a Guarding Totem).");
                    cm.dispose();
            }
			
            if (option == 3) {
                cm.getPlayer().getCashShop().gainCash(2, -cost);
                cm.sendOk("You have spawned a Raging Totem.");
                cm.dispose();
            }
            
        } else if (status == 3) {
            cm.dispose();
			//wat
        } else {
            cm.sendOk("Have a good day.");
            cm.dispose();
        }
    }
}