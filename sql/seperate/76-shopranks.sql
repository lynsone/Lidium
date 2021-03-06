CREATE TABLE IF NOT EXISTS `shopranks` (
  `id` int(11) NOT NULL,
  `shopid` int(11) NOT NULL DEFAULT 0,
  `rank` int(11) NOT NULL DEFAULT 0,
  `name` varchar(255) NOT NULL DEFAULT '',
  `itemid` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

--
-- Volcado de datos para la tabla `shopranks`
--

INSERT IGNORE INTO `shopranks` (`id`, `shopid`, `rank`, `name`, `itemid`) VALUES
(1, 400, 0, 'Grand Champion', 1142321),
(2, 400, 1, 'High Champion', 1142320),
(3, 400, 2, 'Champion', 1142319),
(4, 400, 3, 'Grand Commander', 1142318),
(5, 400, 4, 'High Commander', 1142317),
(6, 400, 5, 'Commander', 1142316),
(7, 400, 6, 'Grand Captain', 1142315),
(8, 400, 7, 'High Captain', 1142314),
(9, 400, 8, 'Captain', 1142313),
(10, 400, 9, 'Grand Officer', 1142312),
(11, 400, 10, 'High Officer', 1142311),
(12, 400, 11, 'Officer', 1142310);

ALTER TABLE `shopranks`
  ADD PRIMARY KEY (`id`);
