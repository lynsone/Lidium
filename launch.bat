@echo off
@title Lidium Server Console
set CLASSPATH=.;out\artifacts\Lithium_master_jar\Lithium-master.jar;lib\*
java -server -Dnet.sf.odinms.wzpath=wz\ server.Start
pause