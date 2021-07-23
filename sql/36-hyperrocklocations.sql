CREATE TABLE IF NOT EXISTS `hyperrocklocations` (
  `trockid` int(11) NOT NULL,
  `characterid` int(11) DEFAULT NULL,
  `mapid` int(11) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

ALTER TABLE `hyperrocklocations`
  ADD PRIMARY KEY (`trockid`);
