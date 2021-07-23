CREATE TABLE IF NOT EXISTS `skills_cooldowns` (
  `id` int(11) NOT NULL,
  `charid` int(11) NOT NULL,
  `SkillID` int(11) NOT NULL,
  `length` bigint(20) NOT NULL,
  `StartTime` bigint(20) UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

ALTER TABLE `skills_cooldowns`
  ADD PRIMARY KEY (`id`),
  ADD KEY `charid` (`charid`);
