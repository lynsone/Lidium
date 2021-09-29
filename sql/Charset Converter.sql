SELECT
	CONCAT( 'ALTER TABLE `', tbl.`TABLE_SCHEMA`, '`.`', tbl.`TABLE_NAME`, '` CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;' ) 
FROM
	`information_schema`.`TABLES` tbl 
WHERE
	tbl.`TABLE_SCHEMA` = 'v111'