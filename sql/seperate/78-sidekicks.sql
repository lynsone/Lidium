CREATE TABLE IF NOT EXISTS `sidekicks` (
  `id` int(10) UNSIGNED NOT NULL,
  `firstid` int(11) NOT NULL DEFAULT 0,
  `secondid` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

ALTER TABLE `sidekicks`
  ADD PRIMARY KEY (`id`);
