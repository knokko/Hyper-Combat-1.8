package knokko.blocks;

import knokko.items.HyperItems;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

public final class HyperBlocks {
	
	public static final BlockIngotEnchanter ingotEnchanter = new BlockIngotEnchanter();
	public static final BlockCable copperCable = new BlockCable("coppercable", 20);
	public static final BlockSolarPanel solarPanel = new BlockSolarPanel("solarpanel", 20, 10000);
	public static final BlockEnergyStorage energyStorage = new BlockEnergyStorage("energystorage", 10000);
	public static final BlockGenerator generator = new BlockGenerator();
	
	public static final HyperBlock siliconOre = new HyperBlock(Material.rock, "siliconore", "pickaxe", CreativeTabs.tabBlock, 4, 5, 1).setDropItem(HyperItems.silicon);
	public static final HyperBlock copperOre = new HyperBlock(Material.rock, "copperore", "pickaxe", CreativeTabs.tabBlock, 4, 5, 1);
	public static final HyperBlock copperBlock = new HyperBlock(Material.rock, "copperblock", "pickaxe", CreativeTabs.tabBlock, 10, 10, 1);
	
	public static final void register(){
		register(ingotEnchanter);
		register(generator);
		register(copperCable);
		register(solarPanel);
		register(energyStorage);
		register(siliconOre, "oreSilicon");
		register(copperOre, "oreCopper");
		register(copperBlock, "blockCopper");
	}
	
	private static final void register(Block block){
		GameRegistry.registerBlock(block, block.getUnlocalizedName().substring(5));
	}
	
	private static final void register(Block block, String oreID){
		register(block);
		OreDictionary.registerOre(oreID, block);
	}
}
