//NPC Agatha 2012000
var status = 0;
var cost = 5000;
var TICKET_TO_ELLINIA = 4031047;

function start() {
    cm.sendYesNo("Hello, I'm in charge of selling tickets for the ship ride to Ellinia Station of Victoria Island. The ride to Ellinia takes off every 15 minutes, beginning on the hour, and it'll cost you #b" + cost + " mesos#k. Are you sure you want to purchase #b#t" + TICKET_TO_ELLINIA + "##k?");
}

function action(mode, type, selection) {
    if (mode == -1)
        cm.dispose();
    else {
        if (mode == 0) {
            cm.sendNext("You must have some business to take care of here, right?");
            cm.dispose();
            return;
        }
        status++;
        if (status == 1) {
            if (cm.getMeso() >= cost && cm.canHold(TICKET_TO_ELLINIA)) {
                cm.gainItem(TICKET_TO_ELLINIA, 1);
                cm.gainMeso(-cost);
                cm.dispose();
            } else {
                cm.sendOk("Are you sure you have #b" + cost + " mesos#k? If so, then I urge you to check your etc. inventory, and see if it's full or not.");
                cm.dispose();
            }
        }
    }
}
