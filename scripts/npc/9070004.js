/* 
	NPC: 	Maximus (From Town)
	NPC ID:	9070004
	Author: ??
	Author: Manu
*/
var status = -1;
var BATTLE_SQUARE = 960000000;

function action(mode, type, selection) {
    cm.dispose();
}
function action_(mode, type, selection) {
	if (mode == 1) {
            status++;
	} else {
            if (status == 0) {
                cm.dispose();
            }
        status--;
	}
	if (status == 0) {
		if (cm.getPlayer().getMapId() != BATTLE_SQUARE) {
                    cm.sendSimple("\r\n#L5#Go to Battle Square#l");
		}
		else {
                    cm.sendSimple("\r\n#L5#Go back to town#l");
		}
	} else if (status == 1) {
		var currentMapId = cm.getPlayer().getMapId();
		if (currentMapId != BATTLE_SQUARE) {
                    //Go to Battle Square
                    cm.saveLocation("MULUNG_TC");
                    cm.warp(BATTLE_SQUARE);
                } else {
                    var savedMapId = cm.getSavedLocation("MULUNG_TC");
                    savedMapId = (savedMapId > 0 ? savedMapId : 100000000);
                    cm.clearSavedLocation("MULUNG_TC");
                    cm.warp(savedMapId);
		}
		cm.dispose();
	}
}
