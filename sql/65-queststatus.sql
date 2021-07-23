CREATE TABLE IF NOT EXISTS `queststatus` (
  `queststatusid` int(10) UNSIGNED NOT NULL,
  `characterid` int(11) NOT NULL DEFAULT 0,
  `quest` int(11) NOT NULL DEFAULT 0,
  `status` tinyint(4) NOT NULL DEFAULT 0,
  `time` int(11) NOT NULL DEFAULT 0,
  `forfeited` int(11) NOT NULL DEFAULT 0,
  `customData` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

--
-- queststatus data.
--

INSERT IGNORE INTO `queststatus` (`queststatusid`, `characterid`, `quest`, `status`, `time`, `forfeited`, `customData`) VALUES
(1475, 18, 123000, 0, 1623539207, 0, '42,82,71,73,29,83,79,81'),
(1476, 18, 1031, 2, 1623539399, 0, NULL),
(1477, 18, 1021, 2, 1623540194, 1, NULL),
(1478, 18, 1032, 2, 1623540230, 0, NULL),
(1479, 18, 1033, 2, 1623540239, 0, NULL),
(1480, 18, 1034, 2, 1623540249, 0, NULL),
(1481, 18, 1035, 2, 1623540279, 0, NULL),
(1482, 18, 1036, 2, 1623540299, 0, NULL),
(1483, 18, 1037, 2, 1623552649, 0, NULL),
(1484, 18, 28433, 2, 1623573821, 0, NULL),
(1485, 18, 11385, 2, 1623574250, 0, NULL),
(1486, 18, 2081, 2, 1623574538, 0, NULL),
(1487, 18, 8163, 1, 1623574541, 0, NULL),
(1488, 18, 29003, 2, 1623574561, 0, NULL),
(1489, 18, 29900, 1, 1623574735, 0, NULL),
(1490, 18, 29005, 1, 1623616575, 0, NULL),
(1491, 18, 27010, 1, 1623616575, 0, '2');

ALTER TABLE `queststatus`
  ADD PRIMARY KEY (`queststatusid`),
  ADD KEY `characterid` (`characterid`),
  ADD KEY `queststatusid` (`queststatusid`);
