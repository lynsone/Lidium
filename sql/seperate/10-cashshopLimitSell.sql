CREATE TABLE IF NOT EXISTS `cashshop_limit_sell` (
  `serial` int(11) NOT NULL,
  `amount` int(11) NOT NULL DEFAULT 0
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

ALTER TABLE `cashshop_limit_sell`
  ADD PRIMARY KEY (`serial`);