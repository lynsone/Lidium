CREATE TABLE IF NOT EXISTS `battlelog` (
  `battlelogid` int(11) NOT NULL,
  `accid` int(11) NOT NULL DEFAULT 0,
  `accid_to` int(11) NOT NULL DEFAULT 0,
  `when` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

ALTER TABLE `battlelog`
  ADD PRIMARY KEY (`battlelogid`),
  ADD KEY `accid` (`accid`);