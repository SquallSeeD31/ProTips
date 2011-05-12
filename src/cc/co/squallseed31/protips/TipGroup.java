package cc.co.squallseed31.protips;

import java.util.HashMap;
import java.util.Random;

public class TipGroup {
	private String group;
	private int interval;
	private int next;
	private HashMap<String, String> tips;
	private String color;
	private String world;
	private Random RN = new Random();
	
	public TipGroup(String group, int interval, int next,
			HashMap<String, String> tips, String color, String world) {
		this.group = group;
		this.interval = interval;
		this.next = next;
		this.tips = tips;
		this.color = color;
		this.world = world;
		
		Protips.debug("Tipgroup added: " + toString());
	}
	
	public TipGroup(String group, int interval, int next,
			HashMap<String, String> tips, String color) {
		this(group, interval, next, tips, color, Config.defaultWorld);
	}
	
	public TipGroup(String group, int interval, HashMap<String, String> tips, String color, String world) {
		this(group, interval, interval, tips, color, world);
	}
	
	public TipGroup(String group, int interval, HashMap<String, String> tips, String color) {
		this(group, interval, interval, tips, color);
	}
	
	public TipGroup(String group, int interval, HashMap<String, String> tips) {
		this(group, interval, interval, tips, Config.defaultColor);
	}
	
	public TipGroup(String group, int interval, String color) {
		this(group, interval, interval, null, color);
	}
	
	public TipGroup(String group, int interval) {
		this(group, interval, interval, null, Config.defaultColor);
	}

	public int doTick() {
		return --next;
	}
	
	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public int getInterval() {
		return interval;
	}

	public void resetInterval() {
		this.next = interval;
	}
	
	public void setInterval(int interval) {
		this.interval = interval;
	}

	public int getNext() {
		return next;
	}

	public void setNext(int next) {
		this.next = next;
	}

	public HashMap<String, String> getTips() {
		return tips;
	}

	public void setTips(HashMap<String, String> tips) {
		this.tips = tips;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
	
	public String getWorld() {
		return world;
	}
	
	public void setWorld(String world) {
		this.world = world;
	}
	
	public String getRandom() {
		if (tips == null || tips.isEmpty()) return null;
		Object[] values = tips.values().toArray();
		return (String) values[RN.nextInt(values.length)];
	}

	@Override
	public String toString() {
		return "TipGroup [group=" + group + ", world=" + world + ", interval="
				+ interval + ", next=" + next + ", tips=" + tips + ", color="
				+ color + "]";
	}
	
}
