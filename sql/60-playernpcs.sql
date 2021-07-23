CREATE TABLE IF NOT EXISTS `playernpcs` (
  `id` int(11) NOT NULL,
  `name` varchar(13) NOT NULL,
  `hair` int(11) NOT NULL,
  `face` int(11) NOT NULL,
  `skin` int(11) NOT NULL,
  `x` int(11) NOT NULL DEFAULT 0,
  `y` int(11) NOT NULL DEFAULT 0,
  `map` int(11) NOT NULL,
  `charid` int(11) NOT NULL,
  `scriptid` int(11) NOT NULL,
  `foothold` int(11) NOT NULL,
  `dir` tinyint(1) NOT NULL DEFAULT 0,
  `gender` tinyint(1) NOT NULL DEFAULT 0,
  `pets` varchar(25) DEFAULT '0,0,0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

ALTER TABLE `playernpcs`
  ADD PRIMARY KEY (`id`),
  ADD KEY `scriptid` (`scriptid`),
  ADD KEY `playernpcs_ibfk_1` (`charid`);
