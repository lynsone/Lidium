CREATE TABLE IF NOT EXISTS `nxcode` (
  `code` varchar(15) NOT NULL,
  `valid` int(11) NOT NULL DEFAULT 1,
  `user` varchar(13) DEFAULT NULL,
  `type` int(11) NOT NULL DEFAULT 0,
  `item` int(11) NOT NULL DEFAULT 10000
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

ALTER TABLE `nxcode`
  ADD PRIMARY KEY (`code`);
