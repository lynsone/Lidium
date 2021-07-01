//Job advance Way of the NightLord / Shadower
var status = -1;

function end(mode, type, selection) {       
    status++;   
    
    if (mode != 1){            
        if(type == 1 && mode == 0)
            status -= 2;
        else{
            qm.dispose();
            return;
        }
    }
    
    if (status == 0 && qm.haveItem(4031517, 1) && qm.haveItem(4031518, 1)){
        if (qm.getJob() == 411){
            qm.gainItem(4031517, -1);
            qm.gainItem(4031518, -1);
            qm.changeJob(412);
        }else if (qm.getJob() == 421){  
            qm.gainItem(4031517, -1);
            qm.gainItem(4031518, -1)
            qm.changeJob(422);  
        }    
        qm.sendNext("I will take these tokens of heroism from you, and grant you your 4th job skills."); 
        qm.forceCompleteQuest(); 
        qm.dispose();
    }else{
        qm.forceStartQuest(); 
        qm.dispose();
    }         
}