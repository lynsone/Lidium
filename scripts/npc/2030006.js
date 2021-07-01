//Holy Stone - Holy Ground at the Snowfield (3rd job)
var status = -1;

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 0) {
	cm.dispose();
	return;
    } else {
	status++;
    }
    
    if(status == 0){
        if (cm.getQuestStatus(1431) == 1 || cm.getQuestStatus(1432) == 1 || cm.getQuestStatus(1433) == 1 || cm.getQuestStatus(1435) == 1 || cm.getQuestStatus(1436) == 1 || 
        cm.getQuestStatus(1437) == 1 || cm.getQuestStatus(1439) == 1 || cm.getQuestStatus(1440) == 1 || cm.getQuestStatus(1442) == 1 || cm.getQuestStatus(1443) == 1 ||
        cm.getQuestStatus(1445) == 1 || cm.getQuestStatus(1446) == 1 && !cm.haveItem(4031059)) {
        var em = cm.getEventManager("3rdjob");
        if (em == null || cm.haveItem(4031059) || em.getInstance("3rdjob") != null) {
            cm.sendOk("#b(A mysterious energy surrounds this stone)#k");
            cm.dispose();
	} else {
	    em.newInstance(cm.getName()).registerPlayer(cm.getChar());
            cm.dispose();
	}       
        }else{
            cm.sendOk("#b(A mysterious energy surrounds this stone)#k");
            cm.dispose();            
        }      
    }
} 