CREATE TABLE IF NOT EXISTS `character_slots` (
  `id` int(11) NOT NULL,
  `accid` int(11) NOT NULL DEFAULT 0,
  `worldid` int(11) NOT NULL DEFAULT 0,
  `charslots` int(11) NOT NULL DEFAULT 6
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- character slots data.
--

INSERT IGNORE INTO `character_slots` (`id`, `accid`, `worldid`, `charslots`) VALUES
(1, 1, 0, 6),
(2, 3, 0, 6),
(3, 2, 0, 6);

ALTER TABLE `character_slots`
  ADD PRIMARY KEY (`id`),
  ADD KEY `accid` (`accid`),
  ADD KEY `id` (`id`);