CREATE TABLE IF NOT EXISTS `dueypackages` (
  `PackageId` int(10) UNSIGNED NOT NULL,
  `RecieverId` int(11) NOT NULL,
  `SenderName` varchar(13) NOT NULL,
  `Mesos` int(10) UNSIGNED DEFAULT 0,
  `TimeStamp` bigint(20) UNSIGNED DEFAULT NULL,
  `Checked` tinyint(3) UNSIGNED DEFAULT 1,
  `Type` tinyint(3) UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

ALTER TABLE `dueypackages`
  ADD PRIMARY KEY (`PackageId`);