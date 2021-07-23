CREATE TABLE IF NOT EXISTS `readable_cheatlog` (
`accountname` varchar(13)
,`accountid` int(11)
,`name` varchar(13)
,`characterid` int(11)
,`offense` tinytext
,`count` int(11)
,`lastoffensetime` timestamp
,`param` tinytext
);