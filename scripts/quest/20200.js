/*
 * Cygnus 2nd Job advancement
 */

var status = -1;

function start(mode, type, selection) {
    qm.sendNext("Not coded.");
   // qm.forceStartQuest();
    qm.dispose();
}

function end(mode, type, selection) {
    qm.dispose();
}