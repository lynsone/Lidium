CREATE TABLE IF NOT EXISTS `drop_data_level` (
  `id` bigint(20) NOT NULL,
  `moblevel` int(11) NOT NULL,
  `dropType` tinyint(1) NOT NULL DEFAULT 0,
  `itemid` int(11) NOT NULL DEFAULT 0,
  `minimum_quantity` int(11) NOT NULL DEFAULT 1,
  `maximum_quantity` int(11) NOT NULL DEFAULT 1,
  `questid` int(11) NOT NULL DEFAULT 0,
  `chance` int(11) NOT NULL DEFAULT 0,
  `comments` varchar(45) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

INSERT IGNORE INTO `drop_data_level` (`id`, `moblevel`, `dropType`, `itemid`, `minimum_quantity`, `maximum_quantity`, `questid`, `chance`, `comments`) VALUES
(1, 1, 0, 4001513, 1, 1, 0, 900000, 0),
(2, 2, 0, 4001513, 1, 1, 0, 900000, 0),
(3, 3, 0, 4001513, 1, 1, 0, 900000, 0),
(4, 4, 0, 4001513, 1, 1, 0, 900000, 0),
(5, 5, 0, 4001513, 1, 1, 0, 900000, 0),
(6, 6, 0, 4001513, 1, 1, 0, 900000, 0),
(7, 7, 0, 4001513, 1, 1, 0, 900000, 0),
(8, 8, 0, 4001513, 1, 1, 0, 900000, 0),
(9, 9, 0, 4001513, 1, 1, 0, 900000, 0),
(10, 10, 0, 4001513, 1, 1, 0, 900000, 0),
(11, 11, 0, 4001513, 1, 1, 0, 900000, 0),
(12, 12, 0, 4001513, 1, 1, 0, 900000, 0),
(13, 13, 0, 4001513, 1, 1, 0, 900000, 0);