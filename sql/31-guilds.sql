CREATE TABLE IF NOT EXISTS `guilds` (
  `guildid` int(10) UNSIGNED NOT NULL,
  `leader` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `GP` int(11) NOT NULL DEFAULT 0,
  `logo` int(10) UNSIGNED DEFAULT NULL,
  `logoColor` smallint(5) UNSIGNED NOT NULL DEFAULT 0,
  `name` varchar(45) NOT NULL,
  `rank1title` varchar(45) NOT NULL DEFAULT 'Master',
  `rank2title` varchar(45) NOT NULL DEFAULT 'Jr. Master',
  `rank3title` varchar(45) NOT NULL DEFAULT 'Member',
  `rank4title` varchar(45) NOT NULL DEFAULT 'Member',
  `rank5title` varchar(45) NOT NULL DEFAULT 'Member',
  `capacity` int(10) UNSIGNED NOT NULL DEFAULT 10,
  `logoBG` int(10) UNSIGNED DEFAULT NULL,
  `logoBGColor` smallint(5) UNSIGNED NOT NULL DEFAULT 0,
  `notice` varchar(101) DEFAULT NULL,
  `signature` int(11) NOT NULL DEFAULT 0,
  `alliance` int(10) UNSIGNED NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

ALTER TABLE `guilds`
  ADD PRIMARY KEY (`guildid`),
  ADD UNIQUE KEY `name` (`name`),
  ADD KEY `guildid` (`guildid`),
  ADD KEY `leader` (`leader`) USING BTREE;
