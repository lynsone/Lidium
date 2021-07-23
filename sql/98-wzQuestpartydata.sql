CREATE TABLE IF NOT EXISTS `wz_questpartydata` (
  `id` int(11) NOT NULL,
  `questid` int(11) NOT NULL DEFAULT 0,
  `rank` varchar(1) NOT NULL DEFAULT '',
  `mode` varchar(13) NOT NULL DEFAULT '',
  `property` varchar(255) NOT NULL DEFAULT '',
  `value` int(11) NOT NULL DEFAULT 0
) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

--
-- wz quest party data data.
--

INSERT IGNORE INTO `wz_questpartydata` (`id`, `questid`, `rank`, `mode`, `property`, `value`) VALUES
(1, 1212, 'S', 'more', 'cmp', 50),
(2, 1212, 'S', 'equal', 'have', 1),
(3, 1212, 'A', 'more', 'cmp', 40),
(4, 1212, 'A', 'equal', 'have', 1),
(5, 1212, 'B', 'more', 'cmp', 20),
(6, 1212, 'C', 'more', 'cmp', 10),
(7, 1212, 'D', 'more', 'cmp', 1),
(8, 1212, 'F', 'more', 'cmp', 0),
(9, 1300, 'S', 'more', 'try', 100),
(10, 1300, 'S', 'more', 'VR', 40),
(11, 1300, 'S', 'equal', 'have', 1),
(12, 1300, 'A', 'more', 'try', 70),
(13, 1300, 'A', 'more', 'VR', 30),
(14, 1300, 'A', 'equal', 'have', 1),
(15, 1300, 'B', 'more', 'try', 50),
(16, 1300, 'B', 'more', 'VR', 25),
(17, 1300, 'C', 'more', 'try', 10),
(18, 1300, 'C', 'more', 'VR', 20),
(19, 1300, 'D', 'more', 'try', 1),
(20, 1300, 'D', 'more', 'VR', 10),
(21, 1300, 'F', 'more', 'try', 1),
(22, 1300, 'F', 'more', 'VR', 0),
(23, 1301, 'S', 'more', 'vic', 100),
(24, 1301, 'S', 'equal', 'have', 1),
(25, 1301, 'A', 'more', 'vic', 80),
(26, 1301, 'A', 'equal', 'have', 1),
(27, 1301, 'B', 'more', 'vic', 50),
(28, 1301, 'C', 'more', 'vic', 20),
(29, 1301, 'D', 'more', 'vic', 1),
(30, 1301, 'F', 'more', 'try', 1),
(31, 1301, 'F', 'more', 'VR', 0),
(32, 1302, 'S', 'more', 'vic', 100),
(33, 1302, 'S', 'equal', 'have', 1),
(34, 1302, 'A', 'more', 'vic', 80),
(35, 1302, 'A', 'equal', 'have', 1),
(36, 1302, 'B', 'more', 'vic', 50),
(37, 1302, 'C', 'more', 'vic', 20),
(38, 1302, 'D', 'more', 'vic', 1),
(39, 1302, 'F', 'more', 'try', 1),
(40, 1302, 'F', 'more', 'VR', 0);

ALTER TABLE `wz_questpartydata`
  ADD PRIMARY KEY (`id`),
  ADD KEY `quests_ibfk_7` (`questid`);
