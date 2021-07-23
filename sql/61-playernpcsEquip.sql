CREATE TABLE IF NOT EXISTS `playernpcs_equip` (
  `id` int(11) NOT NULL,
  `npcid` int(11) NOT NULL,
  `equipid` int(11) NOT NULL,
  `equippos` int(11) NOT NULL,
  `charid` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

ALTER TABLE `playernpcs_equip`
  ADD PRIMARY KEY (`id`),
  ADD KEY `playernpcs_equip_ibfk_1` (`charid`),
  ADD KEY `playernpcs_equip_ibfk_2` (`npcid`);
