CREATE TABLE IF NOT EXISTS `wz_oxdata` (
  `questionset` smallint(6) NOT NULL DEFAULT 0,
  `questionid` smallint(6) NOT NULL DEFAULT 0,
  `question` varchar(200) NOT NULL DEFAULT '',
  `display` varchar(200) NOT NULL DEFAULT '',
  `answer` enum('o','x') NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

ALTER TABLE `wz_oxdata`
  ADD PRIMARY KEY (`questionset`,`questionid`);
