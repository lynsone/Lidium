CREATE TABLE IF NOT EXISTS `pets` (
  `petid` int(10) UNSIGNED NOT NULL,
  `name` varchar(13) DEFAULT NULL,
  `level` int(10) UNSIGNED NOT NULL,
  `closeness` int(10) UNSIGNED NOT NULL,
  `fullness` int(10) UNSIGNED NOT NULL,
  `seconds` int(11) NOT NULL DEFAULT 0,
  `flags` smallint(6) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

INSERT IGNORE INTO `pets` (`petid`, `name`, `level`, `closeness`, `fullness`, `seconds`, `flags`) VALUES
(6, 'Persian Cat', 1, 0, 100, 0, 0),
(8, 'Persian Cat', 1, 0, 100, 0, 0),
(10, 'Kino', 1, 0, 6, 0, 0),
(20, 'Black Pig', 1, 0, 100, 0, 0);

ALTER TABLE `pets`
  ADD PRIMARY KEY (`petid`),
  ADD KEY `petid` (`petid`);
