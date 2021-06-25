var ELLINIA_STATION = 104020110;
var ORBIS_STATION = 200000111;
var ORBIS_STATION_ENTRANCE = 200000100;


var DURING_THE_RIDE_TO_ELLINIA_STATION = 200090000; //OK
var DURING_THE_RIDE_TO_ORBIS_STATION = 200090010; //OK

var BEFORE_STARTING_TO_ORBIS = 104020111; //OK
var BEFORE_STARTING_TO_ELLINIA = 200000112;//OK (Orbis - Pre-Departure <Victoria  Bound>)

var DURING_THE_RIDE_TO_ORBIS_CABIN = 200090011; //OK
var DURING_THE_RIDE_TO_ELLINIA_STATION_CABIN = 200090001; //OK

function init() {
    scheduleNew();
}

function scheduleNew() {
    em.setProperty("docked", "true");
    em.setProperty("entry", "true");
    em.setProperty("haveBalrog", "false");
    em.schedule("stopentry", 240000); //The time to close the gate [4 min]
    em.schedule("takeoff", 300000); // The time to begin the ride [5 min]

    em.getMapFactory().getMap(DURING_THE_RIDE_TO_ELLINIA_STATION).killAllMonsters(false);
    em.getMapFactory().getMap(DURING_THE_RIDE_TO_ORBIS_STATION).killAllMonsters(false);
}

function stopentry() {
    em.setProperty("entry", "false");
    em.getMapFactory().getMap(DURING_THE_RIDE_TO_ORBIS_CABIN).resetReactors();
    em.getMapFactory().getMap(DURING_THE_RIDE_TO_ELLINIA_STATION_CABIN).resetReactors();
}


function takeoff() {

    //From Ellinia to Orbis :3
    em.warpAllPlayer(BEFORE_STARTING_TO_ORBIS, DURING_THE_RIDE_TO_ORBIS_STATION);
    //From Orbis to Ellinia
    em.warpAllPlayer(BEFORE_STARTING_TO_ELLINIA, DURING_THE_RIDE_TO_ELLINIA_STATION);


    em.broadcastShip(ELLINIA_STATION, 8, 2);// ok
    em.broadcastShip(ORBIS_STATION, 8, 2);
    em.setProperty("docked", "false");

    em.schedule("arrived", 420000); // The time that require move to destination [7 min]
    em.schedule("invasion", 60000); // Time to spawn Balrog [1 min]
}

function arrived() {
    em.warpAllPlayer(DURING_THE_RIDE_TO_ORBIS_STATION, ORBIS_STATION_ENTRANCE);//ok boat to orbis station
    em.warpAllPlayer(DURING_THE_RIDE_TO_ORBIS_CABIN, ORBIS_STATION_ENTRANCE);//ok cab to orbis station
    em.warpAllPlayer(DURING_THE_RIDE_TO_ELLINIA_STATION, ELLINIA_STATION);//boat to ellinia st
    em.warpAllPlayer(DURING_THE_RIDE_TO_ELLINIA_STATION_CABIN, ELLINIA_STATION);// cab to 

    em.broadcastShip(ELLINIA_STATION, 12, 6); //ok
    em.broadcastShip(ORBIS_STATION, 12, 6); //ok

    em.getMapFactory().getMap(DURING_THE_RIDE_TO_ORBIS_STATION).killAllMonsters(false);
    em.getMapFactory().getMap(DURING_THE_RIDE_TO_ELLINIA_STATION).killAllMonsters(false);
    em.setProperty("haveBalrog", "false");
    scheduleNew();
}

function invasion() {
    if (Math.floor(Math.random() * 10) < 10) {
        var map1 = em.getMapFactory().getMap(DURING_THE_RIDE_TO_ELLINIA_STATION);
        var pos1 = new java.awt.Point(-538, 143);
        map1.spawnMonsterOnGroundBelow(em.getMonster(8150000), pos1);
        map1.spawnMonsterOnGroundBelow(em.getMonster(8150000), pos1);

        var map2 = em.getMapFactory().getMap(DURING_THE_RIDE_TO_ORBIS_STATION);
        var pos2 = new java.awt.Point(339, 148);
        map2.spawnMonsterOnGroundBelow(em.getMonster(8150000), pos2);
        map2.spawnMonsterOnGroundBelow(em.getMonster(8150000), pos2);

        em.setProperty("haveBalrog", "true");
        em.broadcastShip(DURING_THE_RIDE_TO_ELLINIA_STATION, 10, 4);
        em.broadcastShip(DURING_THE_RIDE_TO_ORBIS_STATION, 10, 4);
    }
}

function cancelSchedule() {
}
