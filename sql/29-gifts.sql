CREATE TABLE IF NOT EXISTS `gifts` (
  `giftid` int(10) UNSIGNED NOT NULL,
  `recipient` int(11) NOT NULL DEFAULT 0,
  `from` varchar(13) NOT NULL DEFAULT '',
  `message` varchar(255) NOT NULL DEFAULT '',
  `sn` int(11) NOT NULL DEFAULT 0,
  `uniqueid` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

ALTER TABLE `gifts`
  ADD PRIMARY KEY (`giftid`),
  ADD KEY `recipient` (`recipient`);