CREATE TABLE IF NOT EXISTS `reports` (
  `reportid` int(11) NOT NULL,
  `characterid` int(11) NOT NULL DEFAULT 0,
  `type` tinyint(4) NOT NULL DEFAULT 0,
  `count` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

ALTER TABLE `reports`
  ADD PRIMARY KEY (`reportid`,`characterid`),
  ADD KEY `characterid` (`characterid`);
