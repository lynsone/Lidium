importPackage(Packages.tools.packet);
importPackage(Packages.client.inventory);

function init() {
}

function setup(player) {
    var returnMapId;
    var monsterId;
    var mapId;
    
    if (player.getJob() == 110 || // FIGHTER
	player.getJob() == 120 || // PAGE
	player.getJob() == 130) { // SPEARMAN
	mapId = 910540100;
	returnMapId = 211040401;
	monsterId = 9001000;
    } else if (player.getJob() == 210 || // FP_WIZARD
	player.getJob() == 220 || // IL_WIZARD
	player.getJob() == 230) { // CLERIC
	mapId = 910540200;
	returnMapId = 211040401;
	monsterId = 9001001;
    } else if (player.getJob() == 310 || // HUNTER
	player.getJob() == 320) { // CROSSBOWMAN
	mapId = 910540300;
	returnMapId = 211040401;
	monsterId = 9001002;    
    } else if (player.getJob() == 410 || // ASSASIN
	player.getJob() == 420) { // BANDIT
	mapId = 910540400;
	returnMapId = 211040401;
	monsterId = 9001003; 
    } else if (player.getJob() == 510 || // BRAWLER
	player.getJob() == 520) { // GUNSLINGER
	mapId = 910540500;
	returnMapId = 211040401;
	monsterId = 9001004;
    }
    
    return new Array(mapId, returnMapId, monsterId);
}

function playerEntry(eim, player) {
    var info = setup(player);
    var mapId = info[0];
    var returnMapId = info[1];
    var monsterId = info[2];
    var map = eim.getMapInstance(mapId);
    map.toggleDrops();
    player.changeMap(map, map.getPortal(0));
    var mob = em.getMonster(monsterId);
    eim.registerMonster(mob);
    map.spawnMonsterOnGroundBelow(mob, new java.awt.Point(200, 20));
}

function playerRevive(eim, player) {
    eim.unregisterPlayer(player);
    eim.dispose();
}

function scheduledTimeout(eim) {
}

function changedMap(eim, player, mapid) {
    if (mapid != 910540100 && mapid != 910540101 && mapid == 910540102 && mapid == 910540103 && mapid == 910540104) {
        eim.unregisterPlayer(player);
        eim.dispose();
    }
}

function playerDisconnected(eim, player) {
    eim.unregisterPlayer(player);
    player.getMap().removePlayer(player);
    player.setMap(returnMap);
}

function monsterValue(eim, mobId) {
    return 1;
}

function playerExit(eim, player) {
    eim.unregisterPlayer(player);
    eim.disposeIfPlayerBelow(0, 0);
}

function end(eim) {
}

function clearPQ(eim) {
    var iter = eim.getPlayers().iterator();
    while (iter.hasNext()) {
	var player = iter.next();
	var info = setup(player);
	var returnMapId = info[1];
  
	var returnMap = em.getChannelServer().getMapFactory().getMap(returnMapId);
	player.changeMap(returnMap, returnMap.getPortal(0));
	eim.unregisterPlayer(player);
    }
    eim.dispose();
}

function allMonstersDead(eim) {
    var price = new Item(4031059, 0, 1, 0);
    var winner = eim.getPlayers().get(0);
    var info = setup(winner);
    var mapId = info[0];
    var map = eim.getMapInstance(mapId);
    eim.getChannelServer().broadcastPacket(CWvsContext.serverNotice(6, "[Third Job] " + winner.getName() + " just defeated the third job advancement clone!"));
    map.spawnItemDrop(winner, winner, price, winner.getPosition(), true, false);
    eim.schedule("clearPQ", 10000);
    //eim.startEventTimer(10000); //10 seconds
}

function leftParty (eim, player) {
}

function disbandParty (eim) {
}
function playerDead(eim, player) {
    eim.unregisterPlayer(player);
    eim.dispose();
}
function cancelSchedule() {   
}