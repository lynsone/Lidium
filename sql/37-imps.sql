CREATE TABLE IF NOT EXISTS `imps` (
  `impid` int(10) UNSIGNED NOT NULL,
  `characterid` int(11) NOT NULL DEFAULT 0,
  `itemid` int(11) NOT NULL DEFAULT 0,
  `level` tinyint(3) UNSIGNED NOT NULL DEFAULT 1,
  `state` tinyint(3) UNSIGNED NOT NULL DEFAULT 1,
  `closeness` mediumint(8) UNSIGNED NOT NULL DEFAULT 0,
  `fullness` mediumint(8) UNSIGNED NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

ALTER TABLE `imps`
  ADD PRIMARY KEY (`impid`),
  ADD KEY `impid` (`impid`);
