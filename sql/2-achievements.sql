CREATE TABLE IF NOT EXISTS `achievements` (
  `achievementid` int(11) NOT NULL DEFAULT 0,
  `charid` int(11) NOT NULL DEFAULT 0,
  `accountid` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Achievement data.
--

INSERT IGNORE INTO `achievements` (`achievementid`, `charid`, `accountid`) VALUES
(1, 15, 1),
(2, 15, 1),
(31, 18, 3),
(32, 18, 3),
(33, 18, 3),
(34, 18, 3);

ALTER TABLE `achievements`
  ADD PRIMARY KEY (`achievementid`,`charid`),
  ADD KEY `achievementid` (`achievementid`),
  ADD KEY `accountid` (`accountid`),
  ADD KEY `charid` (`charid`);