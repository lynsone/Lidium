//Job advance Way of the Hero / Paladin / Dark Knight
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
    
    if (status == 0 && qm.haveItem(4031343, 1) && qm.haveItem(4031344, 1)){
        if (qm.getJob() == 111){
            qm.gainItem(4031343, -1);
            qm.gainItem(4031344, -1)
            qm.changeJob(112);
        }else if (qm.getJob() == 121){  
            qm.gainItem(4031343, -1);
            qm.gainItem(4031344, -1)
            qm.changeJob(122);           
        }else if (qm.getJob() == 131){
            qm.gainItem(4031343, -1);
            qm.gainItem(4031344, -1);
            qm.changeJob(132);
        }
        qm.sendNext("I will take these tokens of heroism from you, and grant you your 4th job skills."); 
        qm.forceCompleteQuest(); 
        qm.dispose();
    }else{
        qm.forceStartQuest(); 
        qm.dispose();
    }         
}