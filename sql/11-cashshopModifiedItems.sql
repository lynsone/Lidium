CREATE TABLE IF NOT EXISTS `cashshop_modified_items` (
  `serial` int(11) NOT NULL,
  `discount_price` int(11) NOT NULL DEFAULT -1,
  `mark` tinyint(1) NOT NULL DEFAULT -1,
  `showup` tinyint(1) NOT NULL DEFAULT 0,
  `itemid` int(11) NOT NULL DEFAULT 0,
  `priority` tinyint(4) NOT NULL DEFAULT 0,
  `package` tinyint(1) NOT NULL DEFAULT 0,
  `period` tinyint(4) NOT NULL DEFAULT 0,
  `gender` tinyint(1) NOT NULL DEFAULT 0,
  `count` tinyint(4) NOT NULL DEFAULT 0,
  `meso` int(11) NOT NULL DEFAULT 0,
  `unk_1` tinyint(1) NOT NULL DEFAULT 0,
  `unk_2` tinyint(1) NOT NULL DEFAULT 0,
  `unk_3` tinyint(1) NOT NULL DEFAULT 0,
  `extra_flags` int(11) NOT NULL DEFAULT 0
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

ALTER TABLE `cashshop_modified_items`
  ADD PRIMARY KEY (`serial`);