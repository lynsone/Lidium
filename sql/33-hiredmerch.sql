CREATE TABLE IF NOT EXISTS `hiredmerch` (
  `PackageId` int(10) UNSIGNED NOT NULL,
  `characterid` int(10) UNSIGNED DEFAULT 0,
  `accountid` int(10) UNSIGNED DEFAULT NULL,
  `Mesos` int(10) UNSIGNED DEFAULT 0,
  `time` bigint(20) UNSIGNED DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

ALTER TABLE `hiredmerch`
  ADD PRIMARY KEY (`PackageId`);
