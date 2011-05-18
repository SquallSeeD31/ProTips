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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.thedarktide.squallseed31.protips.Protips;
import net.thedarktide.squallseed31.protips.TipGroup;

import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.util.config.Configuration;

public class Config {
	private static Configuration config;
	
	protected static PluginDescriptionFile pdfFile;
	protected static String name;
	protected static String version;
	protected static File dataFolder;
	
	protected static HashMap<String, String> allTips = new HashMap<String, String>();
	protected static HashMap<String, TipGroup> allGroups = new HashMap<String, TipGroup>();
	
	protected static String defaultName = "Global";
	protected static String defaultColor = ChatColor.AQUA.toString();
	protected static int defaultInterval = 300;
	protected static String infoColor = ChatColor.GOLD.toString();
	protected static String messageColor = ChatColor.GRAY.toString();
	protected static boolean globalOverride = false;
	protected static String lineTag = defaultColor + "[Protip]";
	protected static String defaultWorld = "world";
	protected static String consoleSender = "SERVER";
	protected static boolean logProtips = true;
	protected static int taskId;
	
	public static void load(Protips plugin) {
		config = plugin.getConfiguration();
		
		pdfFile = plugin.getDescription();
		name = pdfFile.getName();
		version = pdfFile.getVersion();
		dataFolder = plugin.getDataFolder();
		
		defaultName = config.getString("options.global.name", defaultName);
		defaultColor = getConfigColor("messages.global.color", defaultColor);
		infoColor = getConfigColor("options.color.info", infoColor);
		messageColor = getConfigColor("options.color.message", messageColor);
		defaultInterval = config.getInt("messages.global.interval", defaultInterval);
		defaultWorld = config.getString("options.defaultWorld", plugin.getServer().getWorlds().get(0).getName());
		consoleSender = config.getString("options.consoleSender", consoleSender);
		globalOverride = config.getBoolean("options.global.override", globalOverride);
		logProtips = config.getBoolean("options.log", logProtips);
		lineTag = config.getString("options.tag", lineTag).replaceAll("&([0-9a-fA-F])", "\u00A7$1");
		
		loadAllTips();
		loadAllGroups();
	}
	
	public static void save() {
		config.setProperty("options.global.name", defaultName);
		config.setProperty("options.consoleSender", consoleSender);
		config.setProperty("options.global.override", globalOverride);
		config.setProperty("options.log", logProtips);
		config.setProperty("options.tag", lineTag.replace("\u00A7", "&"));
		config.setProperty("options.defaultWorld", defaultWorld);
		config.setProperty("options.color.info", getColorName(infoColor));
		config.setProperty("options.color.message", getColorName(messageColor));
		saveAllTips();
		saveAllGroups();
		config.save();
	}
	
	  public static String getConfigColor(String property, String def) {
		  String propColor = config.getString(property, def);
		  ChatColor returnColor = null;
		  try {
			returnColor = ChatColor.valueOf(propColor);
		  } catch (Exception e) {
			Protips.log.info("[" + name + "] Improper color definition in config.yml, using default.");
			returnColor = ChatColor.valueOf(def);
		  }
		  return returnColor.toString();
	  }
	  
	  public static String getColorName(String colorCode) {
		  try {
			  colorCode = colorCode.replace("\u00A7", "0x");
			  Byte b = Byte.decode(colorCode);
			  return ChatColor.getByCode(Integer.valueOf(b.intValue())).name();
		  } catch (NumberFormatException e) {
			  Protips.log.severe("[" + name + "] Unexpected error parsing color code: " + colorCode + ", using default of WHITE");
			  return "WHITE";
		  }
	  }
	  
	public static TipGroup loadGroup(String path, String group) {
		TipGroup tg = null;
		HashMap<String, String> tips = new HashMap<String, String>();
		int interval = config.getInt(path + ".interval", defaultInterval);
		String color = getConfigColor(path + ".color", getColorName(defaultColor));
		String world = config.getString(path + ".world", defaultWorld);
		List<String> tipList = config.getStringList(path + ".tips", null);
		if (tipList != null && !tipList.isEmpty()) {
			for (String tip : tipList) {
				String message = allTips.get(tip);
				if (message != null)
					tips.put(tip, message);
			}
		}
		tg = new TipGroup(group, interval, tips, color, world);
		return tg;
	}
	
	public static void loadAllTips() {
		allTips.clear();
		List<String> tipList = config.getKeys("messages.tips");
		if (tipList != null && !tipList.isEmpty()) {
			for (String tip : tipList) {
				String message = config.getString("messages.tips." + tip, null).replaceAll("&([0-9a-fA-F])", "\u00A7$1");
				if (message != null)
					allTips.put(tip, message);
			}
		}
	}
	
	public static void loadAllGroups() {
		allGroups.clear();
		allGroups.put(defaultName, loadGroup("messages.global", defaultName));
		List<String> groupList = config.getKeys("messages.groups");
		if (groupList != null && !groupList.isEmpty()) {
			for (String group : groupList) {
				TipGroup tg = loadGroup("messages.groups." + group, group);
				if (tg != null)
					allGroups.put(group, tg);
			}
		}
	}
	
	public static void saveTip(String label, String tip) {
		config.setProperty("messages.tips." + label, tip.replace("\u00A7", "&"));
	}
	
	public static void saveAllTips() {
		for (Map.Entry<String, String> tip : allTips.entrySet())
			config.setProperty("messages.tips." + tip.getKey(), tip.getValue().replace("\u00A7", "&"));
	}
	
	public static void saveGroup(String path, String group) {
		Protips.debug("Saving group: " + group + " at path: " + path);
		TipGroup tg = allGroups.get(group);
		if (tg != null) {
			config.setProperty(path + ".interval", tg.getInterval());
			config.setProperty(path + ".color", getColorName(tg.getColor()));
			config.setProperty(path + ".world", tg.getWorld());
			List<String> tipList = new ArrayList<String>();
			for (String label : tg.getTips().keySet())
				tipList.add(label);
			config.setProperty(path + ".tips", tipList);
		} else {
			Protips.log.severe("[" + name + "] Unable to save group: " + group);
		}
	}
	
	public static void saveAllGroups() {
		if (!allGroups.isEmpty()) {
			for (String group : allGroups.keySet()) {
				if (group.equalsIgnoreCase(defaultName))
					saveGroup("messages.global", group);
				else
					saveGroup("messages.groups." + group, group);
			}
		}
	}
}
