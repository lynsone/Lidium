--
-- Filtros para la tabla `battlelog`
--
ALTER TABLE `battlelog`
  ADD CONSTRAINT `battlelog_ibfk_1` FOREIGN KEY (`accid`) REFERENCES `accounts` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `buddies`
--
ALTER TABLE `buddies`
  ADD CONSTRAINT `buddies_ibfk_1` FOREIGN KEY (`characterid`) REFERENCES `characters` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `csequipment`
--
ALTER TABLE `csequipment`
  ADD CONSTRAINT `csequipment_ibfk_1` FOREIGN KEY (`inventoryitemid`) REFERENCES `csitems` (`inventoryitemid`) ON DELETE CASCADE;

--
-- Filtros para la tabla `dueyequipment`
--
ALTER TABLE `dueyequipment`
  ADD CONSTRAINT `dueyequipment_ibfk_1` FOREIGN KEY (`inventoryitemid`) REFERENCES `dueyitems` (`inventoryitemid`) ON DELETE CASCADE;

--
-- Filtros para la tabla `famelog`
--
ALTER TABLE `famelog`
  ADD CONSTRAINT `famelog_ibfk_1` FOREIGN KEY (`characterid`) REFERENCES `characters` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `hiredmerchequipment`
--
ALTER TABLE `hiredmerchequipment`
  ADD CONSTRAINT `hiredmerchequipment_ibfk_1` FOREIGN KEY (`inventoryitemid`) REFERENCES `hiredmerchitems` (`inventoryitemid`) ON DELETE CASCADE;

--
-- Filtros para la tabla `inventoryequipment`
--
ALTER TABLE `inventoryequipment`
  ADD CONSTRAINT `inventoryequipment_ibfk_1` FOREIGN KEY (`inventoryitemid`) REFERENCES `inventoryitems` (`inventoryitemid`) ON DELETE CASCADE;

--
-- Filtros para la tabla `keymap`
--
ALTER TABLE `keymap`
  ADD CONSTRAINT `keymap_ibfk_1` FOREIGN KEY (`characterid`) REFERENCES `characters` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `mtsequipment`
--
ALTER TABLE `mtsequipment`
  ADD CONSTRAINT `mtsequipment_ibfk_1` FOREIGN KEY (`inventoryitemid`) REFERENCES `mtsitems` (`inventoryitemid`) ON DELETE CASCADE;

--
-- Filtros para la tabla `mtstransferequipment`
--
ALTER TABLE `mtstransferequipment`
  ADD CONSTRAINT `mtstransferequipment_ibfk_1` FOREIGN KEY (`inventoryitemid`) REFERENCES `mtstransfer` (`inventoryitemid`) ON DELETE CASCADE;

--
-- Filtros para la tabla `playernpcs`
--
ALTER TABLE `playernpcs`
  ADD CONSTRAINT `playernpcs_ibfk_1` FOREIGN KEY (`charid`) REFERENCES `characters` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `playernpcs_equip`
--
ALTER TABLE `playernpcs_equip`
  ADD CONSTRAINT `playernpcs_equip_ibfk_1` FOREIGN KEY (`charid`) REFERENCES `characters` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `playernpcs_equip_ibfk_2` FOREIGN KEY (`npcid`) REFERENCES `playernpcs` (`scriptid`) ON DELETE CASCADE;

--
-- Filtros para la tabla `questinfo`
--
ALTER TABLE `questinfo`
  ADD CONSTRAINT `questsinfo_ibfk_1` FOREIGN KEY (`characterid`) REFERENCES `characters` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `queststatus`
--
ALTER TABLE `queststatus`
  ADD CONSTRAINT `queststatus_ibfk_1` FOREIGN KEY (`characterid`) REFERENCES `characters` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `queststatusmobs`
--
ALTER TABLE `queststatusmobs`
  ADD CONSTRAINT `queststatusmobs_ibfk_1` FOREIGN KEY (`queststatusid`) REFERENCES `queststatus` (`queststatusid`) ON DELETE CASCADE;

--
-- Filtros para la tabla `savedlocations`
--
ALTER TABLE `savedlocations`
  ADD CONSTRAINT `savedlocations_ibfk_1` FOREIGN KEY (`characterid`) REFERENCES `characters` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `skills`
--
ALTER TABLE `skills`
  ADD CONSTRAINT `skills_ibfk_1` FOREIGN KEY (`characterid`) REFERENCES `characters` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `storages`
--
ALTER TABLE `storages`
  ADD CONSTRAINT `storages_ibfk_1` FOREIGN KEY (`accountid`) REFERENCES `accounts` (`id`) ON DELETE CASCADE;
