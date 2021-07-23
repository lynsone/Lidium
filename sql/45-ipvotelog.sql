CREATE TABLE IF NOT EXISTS `ipvotelog` (
  `vid` int(10) UNSIGNED NOT NULL,
  `accid` varchar(45) NOT NULL DEFAULT '0',
  `ipaddress` varchar(30) NOT NULL DEFAULT '127.0.0.1',
  `votetime` varchar(100) NOT NULL DEFAULT '0',
  `votetype` tinyint(3) UNSIGNED NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

ALTER TABLE `ipvotelog`
  ADD PRIMARY KEY (`vid`);
