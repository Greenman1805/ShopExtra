package de.greenman1805.shopextra;


import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Item {
	public ItemStack item;
	public int price;
	public int xp;
	short data;
	
	
	
	public Item(Material type, int data, int price, int xp) {
		this.data = (short) data;
		item = new ItemStack(type, 1, this.data);
		this.price = price;
		this.xp = xp;
	}
	

	


}
