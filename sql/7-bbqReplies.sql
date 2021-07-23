CREATE TABLE IF NOT EXISTS `bbs_replies` (
  `replyid` int(10) UNSIGNED NOT NULL,
  `threadid` int(10) UNSIGNED NOT NULL,
  `postercid` int(10) UNSIGNED NOT NULL,
  `timestamp` bigint(20) UNSIGNED NOT NULL,
  `content` varchar(26) NOT NULL DEFAULT '',
  `guildid` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

ALTER TABLE `bbs_replies`
  ADD PRIMARY KEY (`replyid`);