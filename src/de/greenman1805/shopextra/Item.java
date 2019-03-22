package de.greenman1805.shopextra;


import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Item {
	public ItemStack item;
	public int price;
	public int xp;
	
	
	
	public Item(Material type, int price, int xp) {
		item = new ItemStack(type, 1);
		this.price = price;
		this.xp = xp;
	}
	

	


}
