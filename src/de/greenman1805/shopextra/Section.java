package de.greenman1805.shopextra;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

public class Section {
	public static List<Section> sections = new ArrayList<Section>();
	public String name;
	public List<Item> items = new ArrayList<Item>();
	
	public Section(String name) {
		this.name = name;
		sections.add(this);
	}
	
	
	
	public static Section getSection(String name) {
		for (Section s : sections) {
			if (s.name.equalsIgnoreCase(name)) {
				return s;
			}
		}
		return null;
	}
	
	
	
	public Item getItem(ItemStack itemstack) {
		for (Item i : items) {
			if (i.item.getType().equals(itemstack.getType()) && i.item.getData().equals(itemstack.getData()) ) {
				return i;
			}
		}
		return null;
	}
	
	

}
