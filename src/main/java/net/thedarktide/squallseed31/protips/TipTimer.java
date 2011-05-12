/*
 * Protips
 * Copyright (C) 2010 SquallSeeD31 <SquallSeeD31 at gmail dot com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

package net.thedarktide.squallseed31.protips;

import java.util.HashMap;

import org.bukkit.entity.Player;


class TipTimer implements Runnable {
	Protips plugin;
	
	TipTimer(Protips instance) { 
		this.plugin = instance;
	}
	
	public void broadcastProtip(TipGroup tg) {
		try {
			String buildLine = Config.lineTag + " " + tg.getColor();
			String randomTip = tg.getRandom();
			if (randomTip == null) return;
			buildLine += randomTip.replaceAll("&([0-9a-fA-F])", "\u00A7$1") + " ";
			buildLine = buildLine.replaceAll("&x", tg.getColor());
			buildLine = buildLine.replaceAll("''", "'");
			if (Config.logProtips)
				Protips.log.info(buildLine.replaceAll("(?i)\u00A7[0-F]", ""));
			for (Player p : plugin.getServer().getOnlinePlayers()) {
				if (tg.getWorld().equalsIgnoreCase(p.getWorld().getName())) {
					Protips.debug("Due Group: " + tg.getGroup() + " Default Group: " + Config.defaultName + " Player " + p.getName() + " in? " + Protips.Permissions.inGroup(p.getWorld().getName(), p.getName(), tg.getGroup()));
					if (tg.getGroup().equalsIgnoreCase(Config.defaultName) || Protips.Permissions.inGroup(p.getWorld().getName(), p.getName(), tg.getGroup()))
						p.sendMessage(buildLine);
				}
			}
			tg.resetInterval();
			Config.allGroups.put(tg.getGroup(), tg);
		} catch (Exception e) {
			Protips.log.severe("[" + Config.name + "] Exception occurred in timer; disabling timer for safety: " + e.getMessage());
			plugin.getServer().getScheduler().cancelTask(Config.taskId);
		}

	}
	
	@Override
	public void run() {
		HashMap<String, TipGroup> dueGroups = new HashMap<String, TipGroup>();
		int next=1;
		for (TipGroup tg : Config.allGroups.values()) {
			next = tg.doTick();
			if (next <= 0)
				dueGroups.put(tg.getGroup(), tg);
		}
		if (!dueGroups.isEmpty()) {
			for (TipGroup tg : dueGroups.values()) {
				Protips.debug("Due group: " + tg.getGroup());
				if (dueGroups.containsKey(Config.defaultName) && Config.globalOverride) {
					if (tg.getGroup().equalsIgnoreCase(Config.defaultName)) {
						broadcastProtip(tg);
					} else {
						tg.resetInterval();
						Config.allGroups.put(tg.getGroup(), tg);
					}
				} else {
					broadcastProtip(tg);
				}
			}
		}
		dueGroups.clear();
	}
}