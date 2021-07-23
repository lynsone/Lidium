CREATE TABLE IF NOT EXISTS `skills` (
  `id` int(11) NOT NULL,
  `skillid` int(11) NOT NULL DEFAULT 0,
  `characterid` int(11) NOT NULL DEFAULT 0,
  `skilllevel` int(11) NOT NULL DEFAULT 0,
  `masterlevel` tinyint(4) NOT NULL DEFAULT 0,
  `expiration` bigint(20) NOT NULL DEFAULT -1,
  `victimid` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

--
-- Volcado de datos para la tabla `skills`
--

INSERT IGNORE INTO `skills` (`id`, `skillid`, `characterid`, `skilllevel`, `masterlevel`, `expiration`, `victimid`) VALUES
(4, 12, 18, 0, 0, -1, 0),
(5, 73, 18, 0, 0, -1, 0),
(6, 1000, 18, 1, 0, -1, 0),
(7, 2001004, 18, 1, 0, -1, 0),
(8, 2001005, 18, 20, 0, -1, 0),
(9, 2000006, 18, 4, 0, -1, 0);

ALTER TABLE `skills`
  ADD PRIMARY KEY (`id`),
  ADD KEY `skills_ibfk_1` (`characterid`);
