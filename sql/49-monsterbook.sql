CREATE TABLE IF NOT EXISTS `monsterbook` (
  `id` int(11) NOT NULL,
  `charid` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `cardid` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `level` tinyint(3) UNSIGNED DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

ALTER TABLE `monsterbook`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id` (`id`),
  ADD KEY `charid` (`charid`);
