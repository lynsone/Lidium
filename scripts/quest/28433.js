var status = -1;

function start(mode, type, selection) {
	qm.sendOk("Hey there, Mapler~ Remember to make lots of friends. They can help you in a pinch!");
	qm.forceCompleteQuest();
	qm.dispose();
}
function end(mode, type, selection) {
	qm.dispose();
}
