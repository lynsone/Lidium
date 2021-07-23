CREATE TABLE IF NOT EXISTS `iplog` (
  `id` bigint(20) NOT NULL,
  `accid` int(11) NOT NULL,
  `ip` varchar(45) NOT NULL,
  `time` varchar(45) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

ALTER TABLE `iplog`
  ADD PRIMARY KEY (`id`);
