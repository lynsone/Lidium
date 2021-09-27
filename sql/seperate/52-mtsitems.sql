CREATE TABLE IF NOT EXISTS `mtsitems` (
  `inventoryitemid` bigint(20) UNSIGNED NOT NULL,
  `characterid` int(11) DEFAULT NULL,
  `accountid` int(11) DEFAULT NULL,
  `packageId` int(11) DEFAULT NULL,
  `itemid` int(11) NOT NULL DEFAULT 0,
  `inventorytype` int(11) NOT NULL DEFAULT 0,
  `position` int(11) NOT NULL DEFAULT 0,
  `quantity` int(11) NOT NULL DEFAULT 0,
  `owner` tinytext DEFAULT NULL,
  `GM_Log` tinytext DEFAULT NULL,
  `uniqueid` int(11) NOT NULL DEFAULT -1,
  `flag` int(11) NOT NULL DEFAULT 0,
  `expiredate` bigint(20) NOT NULL DEFAULT -1,
  `type` tinyint(1) NOT NULL DEFAULT 0,
  `sender` varchar(13) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

ALTER TABLE `mtsitems`
  ADD PRIMARY KEY (`inventoryitemid`),
  ADD KEY `inventoryitems_ibfk_1` (`characterid`),
  ADD KEY `characterid` (`characterid`),
  ADD KEY `inventorytype` (`inventorytype`),
  ADD KEY `accountid` (`accountid`),
  ADD KEY `characterid_2` (`characterid`,`inventorytype`),
  ADD KEY `packageid` (`packageId`) USING BTREE;
