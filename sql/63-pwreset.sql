CREATE TABLE IF NOT EXISTS `pwreset` (
  `id` int(10) UNSIGNED NOT NULL,
  `name` varchar(14) NOT NULL,
  `email` varchar(100) NOT NULL,
  `confirmkey` varchar(100) NOT NULL,
  `status` tinyint(3) UNSIGNED NOT NULL DEFAULT 0,
  `timestamp` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

ALTER TABLE `pwreset`
  ADD PRIMARY KEY (`id`);
