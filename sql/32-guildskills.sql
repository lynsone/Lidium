CREATE TABLE IF NOT EXISTS `guildskills` (
  `id` int(11) NOT NULL,
  `guildid` int(11) NOT NULL DEFAULT 0,
  `skillid` int(11) NOT NULL DEFAULT 0,
  `level` smallint(6) NOT NULL DEFAULT 1,
  `timestamp` bigint(20) NOT NULL DEFAULT 0,
  `purchaser` varchar(13) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

ALTER TABLE `guildskills`
  ADD PRIMARY KEY (`id`);
