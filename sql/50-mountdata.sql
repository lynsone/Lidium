CREATE TABLE IF NOT EXISTS `mountdata` (
  `id` int(10) UNSIGNED NOT NULL,
  `characterid` int(10) UNSIGNED DEFAULT NULL,
  `Level` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `Exp` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `Fatigue` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT IGNORE INTO `mountdata` (`id`, `characterid`, `Level`, `Exp`, `Fatigue`) VALUES
(1, 2, 1, 0, 0),
(6, 10, 1, 0, 0),
(7, 11, 1, 0, 0),
(8, 12, 1, 0, 0),
(9, 13, 1, 0, 0),
(10, 14, 1, 0, 0),
(11, 15, 1, 0, 0),
(14, 18, 1, 0, 0);

ALTER TABLE `mountdata`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `characterid` (`characterid`),
  ADD KEY `id` (`id`);
