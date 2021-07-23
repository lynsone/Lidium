CREATE TABLE IF NOT EXISTS `notes` (
  `id` int(11) NOT NULL,
  `to` varchar(13) NOT NULL DEFAULT '',
  `from` varchar(13) NOT NULL DEFAULT '',
  `message` text NOT NULL,
  `timestamp` bigint(20) UNSIGNED NOT NULL,
  `gift` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

ALTER TABLE `notes`
  ADD PRIMARY KEY (`id`),
  ADD KEY `to` (`to`);
