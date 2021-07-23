CREATE TABLE IF NOT EXISTS `macfilters` (
  `macfilterid` int(10) UNSIGNED NOT NULL,
  `filter` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

ALTER TABLE `macfilters`
  ADD PRIMARY KEY (`macfilterid`);
