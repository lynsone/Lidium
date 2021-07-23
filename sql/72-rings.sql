CREATE TABLE IF NOT EXISTS `rings` (
  `ringid` int(11) NOT NULL,
  `partnerRingId` int(11) NOT NULL DEFAULT 0,
  `partnerChrId` int(11) NOT NULL DEFAULT 0,
  `itemid` int(11) NOT NULL DEFAULT 0,
  `partnername` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

ALTER TABLE `rings`
  ADD PRIMARY KEY (`ringid`),
  ADD KEY `ringid` (`ringid`),
  ADD KEY `partnerChrId` (`partnerChrId`),
  ADD KEY `partnerRingId` (`partnerRingId`);
