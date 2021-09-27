CREATE TABLE IF NOT EXISTS `familiars` (
  `id` int(10) UNSIGNED NOT NULL,
  `characterid` int(11) NOT NULL DEFAULT 0,
  `familiar` int(11) NOT NULL DEFAULT 0,
  `name` varchar(40) NOT NULL DEFAULT '',
  `fatigue` int(11) NOT NULL DEFAULT 0,
  `expiry` bigint(20) NOT NULL DEFAULT 0,
  `vitality` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

ALTER TABLE `familiars`
  ADD PRIMARY KEY (`id`);