CREATE TABLE IF NOT EXISTS `bbs_threads` (
  `threadid` int(10) UNSIGNED NOT NULL,
  `postercid` int(10) UNSIGNED NOT NULL,
  `name` varchar(26) NOT NULL DEFAULT '',
  `timestamp` bigint(20) UNSIGNED NOT NULL,
  `icon` smallint(5) UNSIGNED NOT NULL,
  `startpost` text NOT NULL,
  `guildid` int(10) UNSIGNED NOT NULL,
  `localthreadid` int(10) UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

ALTER TABLE `bbs_threads`
  ADD PRIMARY KEY (`threadid`);