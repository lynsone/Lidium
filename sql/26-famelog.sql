CREATE TABLE IF NOT EXISTS `famelog` (
  `famelogid` int(11) NOT NULL,
  `characterid` int(11) NOT NULL DEFAULT 0,
  `characterid_to` int(11) NOT NULL DEFAULT 0,
  `when` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

ALTER TABLE `famelog`
  ADD PRIMARY KEY (`famelogid`),
  ADD KEY `characterid` (`characterid`);