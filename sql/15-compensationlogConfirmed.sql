CREATE TABLE IF NOT EXISTS `compensationlog_confirmed` (
  `chrname` varchar(25) NOT NULL DEFAULT '',
  `donor` tinyint(1) NOT NULL DEFAULT 0,
  `value` int(11) NOT NULL DEFAULT 0,
  `taken` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

ALTER TABLE `compensationlog_confirmed`
  ADD PRIMARY KEY (`chrname`);