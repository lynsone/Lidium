CREATE TABLE IF NOT EXISTS `cheatlog` (
  `id` int(11) NOT NULL,
  `characterid` int(11) NOT NULL DEFAULT 0,
  `offense` tinytext NOT NULL,
  `count` int(11) NOT NULL DEFAULT 0,
  `lastoffensetime` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `param` tinytext NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

ALTER TABLE `cheatlog`
  ADD PRIMARY KEY (`id`),
  ADD KEY `cid` (`characterid`) USING BTREE;