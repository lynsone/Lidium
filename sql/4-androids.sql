CREATE TABLE IF NOT EXISTS `androids` (
  `uniqueid` int(10) UNSIGNED NOT NULL,
  `name` varchar(13) NOT NULL DEFAULT 'Android',
  `hair` int(11) NOT NULL DEFAULT 0,
  `face` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

ALTER TABLE `androids`
  ADD PRIMARY KEY (`uniqueid`),
  ADD KEY `uniqueid` (`uniqueid`);