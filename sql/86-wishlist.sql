CREATE TABLE IF NOT EXISTS `wishlist` (
  `characterid` int(11) NOT NULL,
  `sn` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

ALTER TABLE `wishlist`
  ADD KEY `characterid` (`characterid`);
