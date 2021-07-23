ALTER TABLE `accounts`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `alliances`
--
ALTER TABLE `alliances`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `androids`
--
ALTER TABLE `androids`
  MODIFY `uniqueid` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `auth_server_channel_ip`
--
ALTER TABLE `auth_server_channel_ip`
  MODIFY `channelconfigid` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- AUTO_INCREMENT de la tabla `battlelog`
--
ALTER TABLE `battlelog`
  MODIFY `battlelogid` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `bbs_replies`
--
ALTER TABLE `bbs_replies`
  MODIFY `replyid` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `bbs_threads`
--
ALTER TABLE `bbs_threads`
  MODIFY `threadid` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `buddies`
--
ALTER TABLE `buddies`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `characters`
--
ALTER TABLE `characters`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT de la tabla `character_slots`
--
ALTER TABLE `character_slots`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `cheatlog`
--
ALTER TABLE `cheatlog`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `csequipment`
--
ALTER TABLE `csequipment`
  MODIFY `inventoryequipmentid` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de la tabla `csitems`
--
ALTER TABLE `csitems`
  MODIFY `inventoryitemid` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- AUTO_INCREMENT de la tabla `donorlog`
--
ALTER TABLE `donorlog`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21408;

--
-- AUTO_INCREMENT de la tabla `drop_data`
--
ALTER TABLE `drop_data`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=45187;

--
-- AUTO_INCREMENT de la tabla `drop_data_global`
--
ALTER TABLE `drop_data_global`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=32;


--
-- AUTO_INCREMENT de la tabla `dueyequipment`
--
ALTER TABLE `drop_data_level`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1000;

--
-- AUTO_INCREMENT de la tabla `dueyequipment`
--
ALTER TABLE `dueyequipment`
  MODIFY `inventoryequipmentid` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `dueyitems`
--
ALTER TABLE `dueyitems`
  MODIFY `inventoryitemid` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `dueypackages`
--
ALTER TABLE `dueypackages`
  MODIFY `PackageId` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `extendedslots`
--
ALTER TABLE `extendedslots`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `famelog`
--
ALTER TABLE `famelog`
  MODIFY `famelogid` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `familiars`
--
ALTER TABLE `familiars`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `families`
--
ALTER TABLE `families`
  MODIFY `familyid` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `gifts`
--
ALTER TABLE `gifts`
  MODIFY `giftid` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `gmlog`
--
ALTER TABLE `gmlog`
  MODIFY `gmlogid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=67;

--
-- AUTO_INCREMENT de la tabla `guilds`
--
ALTER TABLE `guilds`
  MODIFY `guildid` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `guildskills`
--
ALTER TABLE `guildskills`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `hiredmerch`
--
ALTER TABLE `hiredmerch`
  MODIFY `PackageId` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `hiredmerchequipment`
--
ALTER TABLE `hiredmerchequipment`
  MODIFY `inventoryequipmentid` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `hiredmerchitems`
--
ALTER TABLE `hiredmerchitems`
  MODIFY `inventoryitemid` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `hyperrocklocations`
--
ALTER TABLE `hyperrocklocations`
  MODIFY `trockid` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `imps`
--
ALTER TABLE `imps`
  MODIFY `impid` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `internlog`
--
ALTER TABLE `internlog`
  MODIFY `gmlogid` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `inventoryequipment`
--
ALTER TABLE `inventoryequipment`
  MODIFY `inventoryequipmentid` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1912;

--
-- AUTO_INCREMENT de la tabla `inventoryitems`
--
ALTER TABLE `inventoryitems`
  MODIFY `inventoryitemid` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2851;

--
-- AUTO_INCREMENT de la tabla `inventorylog`
--
ALTER TABLE `inventorylog`
  MODIFY `inventorylogid` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `inventoryslot`
--
ALTER TABLE `inventoryslot`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=227;

--
-- AUTO_INCREMENT de la tabla `ipbans`
--
ALTER TABLE `ipbans`
  MODIFY `ipbanid` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `iplog`
--
ALTER TABLE `iplog`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `ipvotelog`
--
ALTER TABLE `ipvotelog`
  MODIFY `vid` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `keymap`
--
ALTER TABLE `keymap`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5328;

--
-- AUTO_INCREMENT de la tabla `macbans`
--
ALTER TABLE `macbans`
  MODIFY `macbanid` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `macfilters`
--
ALTER TABLE `macfilters`
  MODIFY `macfilterid` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `monsterbook`
--
ALTER TABLE `monsterbook`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `mountdata`
--
ALTER TABLE `mountdata`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT de la tabla `mtsequipment`
--
ALTER TABLE `mtsequipment`
  MODIFY `inventoryequipmentid` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `mtsitems`
--
ALTER TABLE `mtsitems`
  MODIFY `inventoryitemid` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `mtstransfer`
--
ALTER TABLE `mtstransfer`
  MODIFY `inventoryitemid` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `mtstransferequipment`
--
ALTER TABLE `mtstransferequipment`
  MODIFY `inventoryequipmentid` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `mts_cart`
--
ALTER TABLE `mts_cart`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `notes`
--
ALTER TABLE `notes`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `pets`
--
ALTER TABLE `pets`
  MODIFY `petid` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT de la tabla `playernpcs`
--
ALTER TABLE `playernpcs`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `playernpcs_equip`
--
ALTER TABLE `playernpcs_equip`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `pokemon`
--
ALTER TABLE `pokemon`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `pwreset`
--
ALTER TABLE `pwreset`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17511;

--
-- AUTO_INCREMENT de la tabla `questinfo`
--
ALTER TABLE `questinfo`
  MODIFY `questinfoid` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `queststatus`
--
ALTER TABLE `queststatus`
  MODIFY `queststatusid` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1492;

--
-- AUTO_INCREMENT de la tabla `queststatusmobs`
--
ALTER TABLE `queststatusmobs`
  MODIFY `queststatusmobid` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT de la tabla `reactordrops`
--
ALTER TABLE `reactordrops`
  MODIFY `reactordropid` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=841;

--
-- AUTO_INCREMENT de la tabla `regrocklocations`
--
ALTER TABLE `regrocklocations`
  MODIFY `trockid` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `reports`
--
ALTER TABLE `reports`
  MODIFY `reportid` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `rings`
--
ALTER TABLE `rings`
  MODIFY `ringid` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `savedlocations`
--
ALTER TABLE `savedlocations`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `scroll_log`
--
ALTER TABLE `scroll_log`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `shopitems`
--
ALTER TABLE `shopitems`
  MODIFY `shopitemid` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2573;

--
-- AUTO_INCREMENT de la tabla `shopranks`
--
ALTER TABLE `shopranks`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT de la tabla `shops`
--
ALTER TABLE `shops`
  MODIFY `shopid` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1112;

--
-- AUTO_INCREMENT de la tabla `sidekicks`
--
ALTER TABLE `sidekicks`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `skillmacros`
--
ALTER TABLE `skillmacros`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `skills`
--
ALTER TABLE `skills`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT de la tabla `skills_cooldowns`
--
ALTER TABLE `skills_cooldowns`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `speedruns`
--
ALTER TABLE `speedruns`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `storages`
--
ALTER TABLE `storages`
  MODIFY `storageid` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `tournamentlog`
--
ALTER TABLE `tournamentlog`
  MODIFY `logid` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `trocklocations`
--
ALTER TABLE `trocklocations`
  MODIFY `trockid` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `wz_itemadddata`
--
ALTER TABLE `wz_itemadddata`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=307;

--
-- AUTO_INCREMENT de la tabla `wz_itemequipdata`
--
ALTER TABLE `wz_itemequipdata`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=106021;

--
-- AUTO_INCREMENT de la tabla `wz_itemrewarddata`
--
ALTER TABLE `wz_itemrewarddata`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7287;

--
-- AUTO_INCREMENT de la tabla `wz_mobskilldata`
--
ALTER TABLE `wz_mobskilldata`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=754;

--
-- AUTO_INCREMENT de la tabla `wz_questactdata`
--
ALTER TABLE `wz_questactdata`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6961;

--
-- AUTO_INCREMENT de la tabla `wz_questactitemdata`
--
ALTER TABLE `wz_questactitemdata`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8585;

--
-- AUTO_INCREMENT de la tabla `wz_questactquestdata`
--
ALTER TABLE `wz_questactquestdata`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- AUTO_INCREMENT de la tabla `wz_questactskilldata`
--
ALTER TABLE `wz_questactskilldata`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=129;

--
-- AUTO_INCREMENT de la tabla `wz_questpartydata`
--
ALTER TABLE `wz_questpartydata`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=41;

--
-- AUTO_INCREMENT de la tabla `wz_questreqdata`
--
ALTER TABLE `wz_questreqdata`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=30302;
