CREATE TABLE IF NOT EXISTS `mts_cart` (
  `id` int(10) UNSIGNED NOT NULL,
  `characterid` int(11) NOT NULL DEFAULT 0,
  `itemid` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

ALTER TABLE `mts_cart`
  ADD PRIMARY KEY (`id`),
  ADD KEY `characterid` (`characterid`),
  ADD KEY `id` (`id`);
