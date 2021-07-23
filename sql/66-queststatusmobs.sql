CREATE TABLE IF NOT EXISTS `queststatusmobs` (
  `queststatusmobid` int(10) UNSIGNED NOT NULL,
  `queststatusid` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `mob` int(11) NOT NULL DEFAULT 0,
  `count` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

ALTER TABLE `queststatusmobs`
  ADD PRIMARY KEY (`queststatusmobid`),
  ADD KEY `queststatusid` (`queststatusid`);
