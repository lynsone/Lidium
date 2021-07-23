CREATE TABLE IF NOT EXISTS `macbans` (
  `macbanid` int(10) UNSIGNED NOT NULL,
  `mac` varchar(30) NOT NULL
) ENGINE=MEMORY DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

ALTER TABLE `macbans`
  ADD PRIMARY KEY (`macbanid`),
  ADD UNIQUE KEY `mac_2` (`mac`);
