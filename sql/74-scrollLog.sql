CREATE TABLE IF NOT EXISTS `scroll_log` (
  `id` int(11) NOT NULL,
  `accId` int(11) NOT NULL DEFAULT 0,
  `chrId` int(11) NOT NULL DEFAULT 0,
  `scrollId` int(11) NOT NULL DEFAULT 0,
  `itemId` int(11) NOT NULL DEFAULT 0,
  `oldSlots` tinyint(4) NOT NULL DEFAULT 0,
  `newSlots` tinyint(4) NOT NULL DEFAULT 0,
  `hammer` tinyint(4) NOT NULL DEFAULT 0,
  `result` varchar(13) NOT NULL DEFAULT '',
  `whiteScroll` tinyint(1) NOT NULL DEFAULT 0,
  `legendarySpirit` tinyint(1) NOT NULL DEFAULT 0,
  `vegaId` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

ALTER TABLE `scroll_log`
  ADD PRIMARY KEY (`id`);
