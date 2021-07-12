var maps = Array(953000000, 953010000, 953020000, 953030000, 953040000, 953050000);
var ticket = 4001516; //leopard stripe ticket

var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 0) {
            cm.sendOk("Come back when you want to do some Monsterpark !");
	    cm.dispose();
	}
	status--;
    }
    
    if (status == 0) {
        cm.sendNext("Which dungeon would you like to enter ?\r\n#r(Dungeons available for players Lv. 70-119)#k \r\n\r\n #L0##bBlack Mountain hill (Monsters Lv.70 - 80)#k\r\n#L1##bGray's Hideout (Monsters Lv.75 - 85)#k\r\n#L2##bAuto Security Area (Monsters Lv.85 - 95)#k\r\n#L3##bMossy Tree Forest (Monsters Lv.95 - 105)#k\r\n#L4##bSky Forest Training Center (Monsters Lv.100 - 110)#k\r\n#L5##bForbidden Time (Monsters Lv.110 - 120)#k");

    } else if (status == 1) {
        if(cm.getParty() == null || !cm.isLeader()){
            cm.sendOk("You're either not in a party or you're not the leader.");
            cm.dispose();
        }else{
            var party = cm.getParty().getMembers().iterator();
            var next = true;
            var cPlayer = party.next();       
            if(cPlayer.getLevel() <70 || cPlayer.getLevel() >119 || cPlayer.getMapid() != cm.getMapd()){
                next = false;            
            }
        }
        if(!next){
            cm.sendOk("Please make sure all party members are in the map and have correct level requirements.");
            cm.dispose();
        }else{
            if(!cm.partyHasItem(ticket, 1)){
                cm.sendOk("Please make sure all party members have a Leopard Stripe ticket.\r\n\r\n#r" + cm.getPlayersMissingItem(ticket, 1) + " \r\n#kDoesn't have a Leopard Stripe Ticket.");                
                cm.dispose();
            }else{
                var em = cm.getEventManager("MonsterPark");
                if (em == null || em.getInstance("MonsterPark" + maps[selection]) != null){                   
                    cm.sendOk("Someone is already attempting Monsterpark.");
                    cm.dispose();
                } else{
                   cm.givePartyItems(ticket, -1);
                   em.startInstance_Party("" + maps[selection], cm.getPlayer());
                   cm.dispose();
                }                
            }                                    
        }    
    }
}