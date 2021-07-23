CREATE TABLE IF NOT EXISTS `families` (
  `familyid` int(11) NOT NULL,
  `leaderid` int(11) NOT NULL DEFAULT 0,
  `notice` varchar(255) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

ALTER TABLE `families`
  ADD PRIMARY KEY (`familyid`),
  ADD KEY `familyid` (`familyid`),
  ADD KEY `leaderid` (`leaderid`);