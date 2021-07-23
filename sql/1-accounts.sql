CREATE TABLE IF NOT EXISTS `accounts` (
  `id` int(11) NOT NULL,
  `name` varchar(13) NOT NULL,
  `password` varchar(128) NOT NULL,
  `salt` varchar(32) DEFAULT NULL,
  `2ndpassword` varchar(134) DEFAULT NULL,
  `salt2` varchar(32) DEFAULT NULL,
  `loggedin` tinyint(3) UNSIGNED NOT NULL DEFAULT 0,
  `lastlogin` timestamp NULL DEFAULT NULL,
  `createdat` timestamp NOT NULL DEFAULT current_timestamp(),
  `birthday` datetime NOT NULL DEFAULT current_timestamp(),
  `banned` tinyint(1) NOT NULL DEFAULT 0,
  `banreason` text DEFAULT NULL,
  `gm` tinyint(1) NOT NULL DEFAULT 0,
  `email` tinytext DEFAULT NULL,
  `macs` tinytext DEFAULT NULL,
  `tempban` timestamp NULL DEFAULT NULL,
  `greason` tinyint(3) UNSIGNED DEFAULT NULL,
  `NxPrepaid` int(11) NOT NULL DEFAULT 0,
  `NxCredit` int(11) NOT NULL DEFAULT 0,
  `mPoints` int(11) NOT NULL DEFAULT 0,
  `gender` tinyint(3) UNSIGNED NOT NULL DEFAULT 0,
  `SessionIP` varchar(64) DEFAULT NULL,
  `points` int(11) NOT NULL DEFAULT 0,
  `vpoints` int(11) NOT NULL DEFAULT 0,
  `monthvotes` int(11) NOT NULL DEFAULT 0,
  `totalvotes` int(11) NOT NULL DEFAULT 0,
  `lastvote` int(11) NOT NULL DEFAULT 0,
  `lastvote2` int(11) NOT NULL DEFAULT 0,
  `lastlogon` timestamp NULL DEFAULT NULL,
  `lastvoteip` varchar(64) DEFAULT NULL,
  `PicEnabled`tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Account data.
--

INSERT IGNORE INTO `accounts` (`id`, `name`, `password`, `salt`, `2ndpassword`, `salt2`, `loggedin`, `lastlogin`, `createdat`, `birthday`, `banned`, `banreason`, `gm`, `email`, `macs`, `tempban`, `greason`, `NxPrepaid`, `NxCredit`, `mPoints`, `gender`, `SessionIP`, `points`, `vpoints`, `monthvotes`, `totalvotes`, `lastvote`, `lastvote2`, `lastlogon`, `lastvoteip`) VALUES
(2, 'admin', 'admin', NULL, 'UC8DYXd1c087d93a3eb8d03154fa2560e945128be960087a25be8d34983ef46b5902884a0cbae6bfd7300e7833153e76e14c4c2b31d70e4415be08c851c4690257487c', '53e5fac23b052d0afbdd2db4fc7be324', 0, '2021-06-13 21:06:06', '2021-06-13 07:52:41', '2021-06-13 02:52:41', 0, NULL, 0, NULL, '00-50-56-C0-00-08, 1C-BF-C0-8A-6C-EB, 0A-00-27-00-00-18, 00-50-56-C0-00-01', NULL, NULL, 1, 120613, 2, 0, '/127.0.0.1', 0, 0, 0, 0, 0, 0, '2021-06-13 21:04:45', NULL);

ALTER TABLE `accounts`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name` (`name`);
