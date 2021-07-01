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
    
    if (status == 0 && qm.haveItem(4031860, 1) && qm.haveItem(4031861, 1)){
        if (qm.getJob() == 511){
            qm.gainItem(4031860, -1);
            qm.gainItem(4031861, -1);
            qm.changeJob(512);
        }else if (qm.getJob() == 521){  
            qm.gainItem(4031860, -1);
            qm.gainItem(4031861, -1)
            qm.changeJob(522);  
        }    
        qm.sendNext("I will take these tokens of heroism from you, and grant you your 4th job skills."); 
        qm.forceCompleteQuest(); 
        qm.dispose();
    }else{
        qm.forceStartQuest(); 
        qm.dispose();
    }         
}