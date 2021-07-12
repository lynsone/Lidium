var maps = Array(954000000, 954010000, 954020000, 954030000, 954040000, 954050000);
var ticket = 4001522; //tiger stripe ticket

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
        cm.sendNext("Which dungeon would you like to enter ?\r\n#r(Dungeons available for players Lv. 120+)#k \r\n\r\n #L0##bRuined City (Monsters Lv.120 - 130)#k\r\n#L1##bDead Tree Forest (Monsters Lv.125 - 135)#k\r\n#L2##bWatchman's Tower (Monsters Lv.130 - 140)#k\r\n#L3##bDragon Nest (Monsters Lv.140 - 150)#k\r\n#L4##bTemple of Oblivion (Monsters Lv.150 - 165)#k\r\n#L5##bKnight Stronghold (Monsters Lv.165 - 175)#k");
    } else if (status == 1) {
        if(cm.getParty() == null || !cm.isLeader()){
            cm.sendOk("You're either not in a party or you're not the leader.");
            cm.dispose();
        }else{
            var party = cm.getParty().getMembers().iterator();
            var next = true;
            var cPlayer = party.next();  
            if(cPlayer.getLevel() <120 || cPlayer.getLevel() >200 || cPlayer.getMapid() != cm.getMapId()){
                next = false;            
            }
        }
        if(!next){
            cm.sendOk("Please make sure all party members are in the map and have correct level requirements.");
            cm.dispose();
        }else{
            if(!cm.partyHasItem(ticket, 1)){
                cm.sendOk("Please make sure all party members have a Tiger Stripe Ticket.\r\n\r\n#r" + cm.getPlayersMissingItem(ticket, 1) + " \r\n#kDoesn't have a Tiger Stripe Ticket.");
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