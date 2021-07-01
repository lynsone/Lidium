//Job advance Way of the Bowmaster / Marksman
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
    
    if (status == 0 && qm.haveItem(4031514, 1) && qm.haveItem(4031515, 1)){
        if (qm.getJob() == 311){
            qm.gainItem(4031514, -1);
            qm.gainItem(4031515, -1);
            qm.changeJob(312);
        }else if (qm.getJob() == 321){  
            qm.gainItem(4031514, -1);
            qm.gainItem(4031515, -1)
            qm.changeJob(322);  
        }    
        qm.sendNext("I will take these tokens of heroism from you, and grant you your 4th job skills."); 
        qm.forceCompleteQuest(); 
        qm.dispose();
    }else{
        qm.forceStartQuest(); 
        qm.dispose();
    }         
}