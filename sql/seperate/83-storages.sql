CREATE TABLE IF NOT EXISTS `storages` (
  `storageid` int(10) UNSIGNED NOT NULL,
  `accountid` int(11) NOT NULL DEFAULT 0,
  `slots` int(11) NOT NULL DEFAULT 0,
  `meso` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

--
-- storages data.
--

INSERT IGNORE INTO `storages` (`storageid`, `accountid`, `slots`, `meso`) VALUES
(3, 2, 4, 0);

ALTER TABLE `storages`
  ADD PRIMARY KEY (`storageid`),
  ADD KEY `accountid` (`accountid`);
