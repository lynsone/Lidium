CREATE TABLE IF NOT EXISTS `wz_questactquestdata` (
  `id` int(11) NOT NULL,
  `quest` int(11) NOT NULL DEFAULT 0,
  `state` tinyint(1) NOT NULL DEFAULT 2,
  `uniqueid` int(11) NOT NULL DEFAULT 0
) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

--
-- wz quest act quest data data.
--

INSERT IGNORE INTO `wz_questactquestdata` (`id`, `quest`, `state`, `uniqueid`) VALUES
(1, 2100, 2, 174),
(2, 2144, 2, 213),
(3, 2198, 2, 253),
(4, 2198, 2, 254),
(5, 2199, 2, 255),
(6, 2200, 2, 255),
(7, 2201, 2, 256),
(8, 2202, 2, 257),
(9, 2205, 2, 261),
(10, 3080, 2, 691),
(11, 3081, 2, 693),
(12, 3334, 2, 847),
(13, 3521, 2, 973),
(14, 3521, 2, 981),
(15, 3641, 2, 1029),
(16, 3926, 2, 1146),
(17, 4307, 2, 1211),
(18, 6033, 2, 1391),
(19, 20300, 2, 2653);

ALTER TABLE `wz_questactquestdata`
  ADD PRIMARY KEY (`id`);
