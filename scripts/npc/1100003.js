var menu = new Array("Victoria Island");
var method;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
        return;
    } else {
        if (mode == 0 && status == 0) {
            cm.dispose();
            return;
        } else if (mode == 0) {
            cm.sendNext("OK. If you ever change your mind, please let me know.");
            cm.dispose();
            return;
        }
        status++;
        if (status == 0) {
            var display = "";
            for (var i = 0; i < menu.length; i++) {
                display += "\r\n#L" + i + "##b Victoria Island (1000 mesos)#k";
            }
            cm.sendSimple("Hmm... The winds are favorable. Are you thinking of leaving ereve and going somwhere else? This ferry sails to Victoria Island, have you taking care of everything you needed to in Ereve? If you happen to be headed toward #bVictoria Island#k I can take you there. What do you day? Are you going to go to Victoria Island?\r\n" + display);

        } else if (status == 1) {
            if (cm.getMeso() < 1000) {
                cm.sendNext("Hmm... Are you sure you have #b1000#k Mesos? Check your Inventory and make sure you have enough. You must pay the fee or I can't let you get on...");
                cm.dispose();
            } else {
                cm.gainMeso(-1000);
                cm.warp(104020110);
                cm.dispose();
            }
        }
    }
}