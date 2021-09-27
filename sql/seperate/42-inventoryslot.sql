CREATE TABLE IF NOT EXISTS `inventoryslot` (
  `id` int(10) UNSIGNED NOT NULL,
  `characterid` int(10) UNSIGNED DEFAULT NULL,
  `equip` tinyint(3) UNSIGNED DEFAULT NULL,
  `use` tinyint(3) UNSIGNED DEFAULT NULL,
  `setup` tinyint(3) UNSIGNED DEFAULT NULL,
  `etc` tinyint(3) UNSIGNED DEFAULT NULL,
  `cash` tinyint(3) UNSIGNED DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

INSERT IGNORE INTO `inventoryslot` (`id`, `characterid`, `equip`, `use`, `setup`, `etc`, `cash`) VALUES
(15, 10, 32, 32, 32, 32, 60),
(18, 2, 32, 32, 32, 32, 60),
(23, 11, 32, 32, 32, 32, 60),
(55, 14, 32, 32, 32, 32, 60),
(57, 12, 32, 32, 32, 32, 60),
(58, 13, 32, 32, 32, 32, 60),
(70, 15, 32, 32, 32, 32, 60),
(226, 18, 36, 32, 32, 36, 60);

ALTER TABLE `inventoryslot`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `characterid` (`characterid`),
  ADD KEY `id` (`id`);
