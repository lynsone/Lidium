var maps = Array(952000000, 952010000, 952020000, 952030000, 952040000);
var ticket = 4001514; //zebra stripe ticket

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
        cm.sendNext("Which dungeon would you like to enter ?\r\n#r(Dungeons available for players Lv. 13-69)#k \r\n\r\n #L0##bGolem's Temple (Monsters Lv.20 - 30)#k\r\n#L1##bKerning Square (Monsters Lv.45 - 55)#k\r\n#L2##bWitch Snowfield (Monsters Lv.50 - 60)#k\r\n#L3##bSilent Sea (Monsters Lv.55 - 65)#k\r\n#L4##bTemple of Darkness (Monsters Lv. 60-70)#k");
    } else if (status == 1) {
        if(cm.getParty() == null || !cm.isLeader()){
            cm.sendOk("You're either not in a party or you're not the leader.");
            cm.dispose();
        }else{
            var party = cm.getParty().getMembers().iterator();
            var next = true;
            var cPlayer = party.next();       
            if(cPlayer.getLevel() <13 || cPlayer.getLevel() >69 || cPlayer.getMapid() != cm.getMapId()){
                next = false;            
            }
        }
        if(!next){
            cm.sendOk("Please make sure all party members are in the map and have correct level requirements.");
            cm.dispose();
        }else{
            if(!cm.partyHasItem(ticket, 1)){
                cm.sendOk("Please make sure all party members have a Zebra Stripe ticket.\r\n\r\n#r" + cm.getPlayersMissingItem(ticket, 1) + " \r\n#kDoesn't have a Zebra Stripe Ticket."); 
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