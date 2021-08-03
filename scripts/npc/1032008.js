//NPC Cherry (to Orbis)
//ID: 1032008
var TICKET_TO_ORBIS = 4031045;
function start() {
    if (cm.haveItem(TICKET_TO_ORBIS)) {
        var em = cm.getEventManager("Boats");
        if (em.getProperty("entry") == "true")
            cm.sendYesNo("Do you want to go to Orbis?");
        else {
            cm.sendOk("The boat to Orbis is already travelling, please be patient for the next one.");
            cm.dispose();
        }
    } else {
        cm.sendOk("Make sure you got a Orbis ticket to travel in this boat. Check your inventory.");
        cm.dispose();
    }
}
function action(mode, type, selection) {
    if (mode <= 0) {
        cm.sendOk("Okay, talk to me if you change your mind!");
        cm.dispose();
        return;
    }
    var em = cm.getEventManager("Boats");
    if (em.getProperty("entry") == "true") {
        //cm.warp(104020111);
		cm.warp(200000100);
        cm.gainItem(TICKET_TO_ORBIS, -1);
        cm.dispose();
    } else {
        cm.sendOk("The boat to Orbis is ready to take off, please be patient for the next one.");
        cm.dispose();
    }
}	
