CREATE TABLE IF NOT EXISTS `questinfo` (
  `questinfoid` int(10) UNSIGNED NOT NULL,
  `characterid` int(11) NOT NULL DEFAULT 0,
  `quest` int(11) NOT NULL DEFAULT 0,
  `customData` varchar(555) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

--
-- questinfo data.
--

INSERT IGNORE INTO `questinfo` (`questinfoid`, `characterid`, `quest`, `customData`) VALUES
(4, 18, 27000, 'enter=10000000000000000000');

ALTER TABLE `questinfo`
  ADD PRIMARY KEY (`questinfoid`),
  ADD KEY `characterid` (`characterid`);
