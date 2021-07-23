CREATE TABLE IF NOT EXISTS `trocklocations` (
  `trockid` int(11) NOT NULL,
  `characterid` int(11) DEFAULT NULL,
  `mapid` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

ALTER TABLE `trocklocations`
  ADD PRIMARY KEY (`trockid`),
  ADD KEY `characterid` (`characterid`);
