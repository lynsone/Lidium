CREATE TABLE IF NOT EXISTS `inventorylog` (
  `inventorylogid` int(10) UNSIGNED NOT NULL,
  `inventoryitemid` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `msg` tinytext NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

ALTER TABLE `inventorylog`
  ADD PRIMARY KEY (`inventorylogid`),
  ADD KEY `inventoryitemid` (`inventoryitemid`);
