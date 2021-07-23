CREATE TABLE IF NOT EXISTS `speedruns` (
  `id` int(10) UNSIGNED NOT NULL,
  `type` varchar(13) NOT NULL,
  `leader` varchar(13) NOT NULL,
  `timestring` varchar(1024) NOT NULL,
  `time` bigint(20) NOT NULL DEFAULT 0,
  `members` varchar(1024) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

ALTER TABLE `speedruns`
  ADD PRIMARY KEY (`id`);
