var ROAD_TO_LUDI = 200090100;
var ROAD_TO_ORBIS = 200090110;
var LUDI_STATION = 220000110;
var ORBIS_STATION_TO_LUDI = 200000121;

function init() {
    scheduleNew();
}

function scheduleNew() {
    em.setProperty("docked", "true");
    em.setProperty("entry", "true");
    em.schedule("stopEntry", 240000); //The time to close the gate
    em.schedule("takeoff", 300000); //The time to begin the ride
}

function stopEntry() {
    em.setProperty("entry", "false");
}

function takeoff() {
    em.warpAllPlayer(200000122, ROAD_TO_LUDI);
    em.warpAllPlayer(220000111, ROAD_TO_ORBIS);
    em.broadcastShip(ORBIS_STATION_TO_LUDI, 8, 2);
    em.broadcastShip(LUDI_STATION, 8, 2);
    em.setProperty("docked", "false");
    em.schedule("arrived", 420000); //The time that require move to destination
}
function arrived() {
    em.warpAllPlayer(ROAD_TO_LUDI, LUDI_STATION); // from orbis
    em.warpAllPlayer(ROAD_TO_ORBIS, ORBIS_STATION_TO_LUDI); // from ludi
    em.broadcastShip(ORBIS_STATION_TO_LUDI, 12, 6);
    em.broadcastShip(LUDI_STATION, 12, 6);
    scheduleNew();
}

function cancelSchedule() {
}
