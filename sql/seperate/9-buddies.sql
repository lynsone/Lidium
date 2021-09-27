CREATE TABLE IF NOT EXISTS `buddies` (
  `id` int(11) NOT NULL,
  `characterid` int(11) NOT NULL,
  `buddyid` int(11) NOT NULL,
  `pending` tinyint(4) NOT NULL DEFAULT 0,
  `groupname` varchar(16) NOT NULL DEFAULT 'ETC'
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

ALTER TABLE `buddies`
  ADD PRIMARY KEY (`id`),
  ADD KEY `buddies_ibfk_1` (`characterid`),
  ADD KEY `buddyid` (`buddyid`),
  ADD KEY `id` (`id`);