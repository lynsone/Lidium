CREATE TABLE IF NOT EXISTS `auth_server_channel_ip` (
  `channelconfigid` int(10) UNSIGNED NOT NULL,
  `channelid` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `name` tinytext NOT NULL,
  `value` tinytext NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

ALTER TABLE `auth_server_channel_ip`
  ADD PRIMARY KEY (`channelconfigid`),
  ADD KEY `channelid` (`channelid`);