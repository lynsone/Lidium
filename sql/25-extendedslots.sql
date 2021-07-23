CREATE TABLE IF NOT EXISTS `extendedslots` (
  `id` int(11) NOT NULL,
  `characterid` int(11) NOT NULL DEFAULT 0,
  `itemId` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

ALTER TABLE `extendedslots`
  ADD PRIMARY KEY (`id`);