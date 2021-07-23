CREATE TABLE IF NOT EXISTS `savedlocations` (
  `id` int(11) NOT NULL,
  `characterid` int(11) NOT NULL,
  `locationtype` int(11) NOT NULL DEFAULT 0,
  `map` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

ALTER TABLE `savedlocations`
  ADD PRIMARY KEY (`id`),
  ADD KEY `savedlocations_ibfk_1` (`characterid`);
