CREATE TABLE IF NOT EXISTS `internlog` (
  `gmlogid` int(11) NOT NULL,
  `cid` int(11) NOT NULL DEFAULT 0,
  `command` tinytext NOT NULL,
  `mapid` int(11) NOT NULL DEFAULT 0,
  `time` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

ALTER TABLE `internlog`
  ADD PRIMARY KEY (`gmlogid`);
