var Orbis_btf;
var Leafre_btf;
var Cabin_to_Orbis;
var Cabin_to_Leafre;
var Orbis_docked;
var Leafre_docked;

//Time Setting is in millisecond
var closeTime = 4 * 60 * 1000; //The time to close the gate
var beginTime = 5 * 60 * 1000; //The time to begin the ride
var  rideTime = 5 * 60 * 1000; //The time that require move to destination

function init() {
    closeTime = em.getTransportationTime(closeTime);
    beginTime = em.getTransportationTime(beginTime);
     rideTime = em.getTransportationTime(rideTime);
    
    Orbis_btf = em.getChannelServer().getMapFactory().getMap(200000132);
    Leafre_btf = em.getChannelServer().getMapFactory().getMap(240000111);
    Cabin_to_Orbis = em.getChannelServer().getMapFactory().getMap(200090210);
    Cabin_to_Leafre = em.getChannelServer().getMapFactory().getMap(200090200);
    Orbis_docked = em.getChannelServer().getMapFactory().getMap(200000131);
    Leafre_docked = em.getChannelServer().getMapFactory().getMap(240000110);
    Orbis_Station = em.getChannelServer().getMapFactory().getMap(200000100);
    Leafre_Station = em.getChannelServer().getMapFactory().getMap(240000100);
    
    scheduleNew();
}

function scheduleNew() {
    em.setProperty("docked", "true");
    Orbis_docked.setDocked(true);
    Leafre_docked.setDocked(true);
    
    em.setProperty("entry", "true");
    em.schedule("stopEntry", closeTime); //The time to close the gate
    em.schedule("takeoff", beginTime); //The time to begin the ride
}

function stopEntry() {
    em.setProperty("entry","false");
}

function takeoff() {
    Orbis_btf.warpEveryone(Cabin_to_Leafre.getId());
    Leafre_btf.warpEveryone(Cabin_to_Orbis.getId());
    
    Orbis_docked.broadcastShip(false);
    Leafre_docked.broadcastShip(false);
    
    em.setProperty("docked","false");
    Orbis_docked.setDocked(false);
    Leafre_docked.setDocked(false);
    
    em.schedule("arrived", rideTime); //The time that require move to destination
}

function arrived() {
    Cabin_to_Orbis.warpEveryone(Orbis_Station.getId(), 0);
    Cabin_to_Leafre.warpEveryone(Leafre_Station.getId(), 0);
    
    Orbis_docked.broadcastShip(true);
    Leafre_docked.broadcastShip(true);
    
    scheduleNew();
}

function cancelSchedule() {}