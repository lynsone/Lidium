CREATE TABLE IF NOT EXISTS `tournamentlog` (
  `logid` int(11) NOT NULL,
  `winnerid` int(11) NOT NULL DEFAULT 0,
  `numContestants` int(11) NOT NULL DEFAULT 0,
  `when` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

ALTER TABLE `tournamentlog`
  ADD PRIMARY KEY (`logid`);
