CREATE TABLE IF NOT EXISTS `drop_data_global` (
  `id` bigint(20) NOT NULL,
  `continent` int(11) NOT NULL,
  `dropType` tinyint(1) NOT NULL DEFAULT 0,
  `itemid` int(11) NOT NULL DEFAULT 0,
  `minimum_quantity` int(11) NOT NULL DEFAULT 1,
  `maximum_quantity` int(11) NOT NULL DEFAULT 1,
  `questid` int(11) NOT NULL DEFAULT 0,
  `chance` int(11) NOT NULL DEFAULT 0,
  `comments` varchar(45) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

ALTER TABLE `drop_data_global`
  ADD PRIMARY KEY (`id`),
  ADD KEY `mobid` (`continent`) USING BTREE;