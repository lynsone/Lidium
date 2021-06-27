//Monster Park Shuttle Warps player in and out of Monster Park

var status = -1;

function start() {
    cm.sendYesNo(cm.getMapId() != 951000000 ? "Ah, our favorite customer! Would you like to go to Spiegelmann's Monster Park?" : "Hey there! Need a lift back to town? That's what the Monster Park Shuttle is for!");
}

function action(mode, tupe, selection) {
    if (mode == 1) {
        if(cm.getMapId() != 951000000) {
            cm.saveReturnLocation("MONSTERPARK");
            (cm.getMapId() != 951000000 ? cm.warp(951000000, 0) : cm.sendNext("Okay, the shuttle will take you back to town."));
            cm.dispose();
        }else{
            var map = cm.getSavedLocation("MONSTERPARK");
            (cm.getMapId() != 951000000 ? cm.dispose() : cm.warp(map, 0));
            cm.dispose();
        }
    }else{
        cm.sendNext(cm.getMapId() != 951000000 ? "The shuttle is always ready for you, so come back anytime." : "Use the shuttle if you want to leave the Monster Park. A comfy ride every time, guaranteed!");
        cm.dispose();
    }
}