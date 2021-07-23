CREATE TABLE IF NOT EXISTS `pokemon` (
  `id` int(11) NOT NULL,
  `monsterid` int(11) NOT NULL DEFAULT 0,
  `characterid` int(11) NOT NULL DEFAULT 0,
  `level` smallint(6) NOT NULL DEFAULT 1,
  `exp` int(11) NOT NULL DEFAULT 0,
  `name` varchar(255) NOT NULL DEFAULT '',
  `nature` tinyint(4) NOT NULL DEFAULT 0,
  `active` tinyint(1) NOT NULL DEFAULT 0,
  `accountid` int(11) NOT NULL DEFAULT 0,
  `itemid` int(11) NOT NULL DEFAULT 0,
  `gender` tinyint(4) NOT NULL DEFAULT -1,
  `hpiv` tinyint(4) NOT NULL DEFAULT -1,
  `atkiv` tinyint(4) NOT NULL DEFAULT -1,
  `defiv` tinyint(4) NOT NULL DEFAULT -1,
  `spatkiv` tinyint(4) NOT NULL DEFAULT -1,
  `spdefiv` tinyint(4) NOT NULL DEFAULT -1,
  `speediv` tinyint(4) NOT NULL DEFAULT -1,
  `evaiv` tinyint(4) NOT NULL DEFAULT -1,
  `acciv` tinyint(4) NOT NULL DEFAULT -1,
  `ability` tinyint(4) NOT NULL DEFAULT -1
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

ALTER TABLE `pokemon`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id` (`id`),
  ADD KEY `characterid` (`characterid`);
