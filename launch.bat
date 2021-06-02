@echo off
@title Lidium Server Console
set CLASSPATH=.;out\artifacts\Lithium_master_jar\Lithium-master.jar;dist\mina-core.jar;dist\slf4j-api.jar;dist\slf4j-jdk14.jar;dist\mysql-connector-java-bin.jar
java -server -Dnet.sf.odinms.wzpath=wz\ server.Start
pause