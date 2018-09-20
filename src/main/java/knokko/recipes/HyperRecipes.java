package knokko.recipes;

import knokko.blocks.HyperBlocks;
import knokko.items.HyperItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class HyperRecipes {

	public static void register(){
		GameRegistry.addSmelting(HyperBlocks.copperOre, new ItemStack(HyperItems.copperIngot), 0.8F);
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(HyperBlocks.copperCable, 6), "   ", "ccc", "   ", 'c', "ingotCopper"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(HyperItems.battery, 6, HyperItems.battery.getMaxDamage()), "gcg", "gcg", "gcg", 'g', "dyeGreen", 'c', "ingotCopper"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(HyperItems.yellowBattery, 6, HyperItems.yellowBattery.getMaxDamage()), "ycy", "ycy", "ycy", 'y', "dyeYellow", 'c', HyperItems.yellowCopper));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(HyperItems.energyTank), "wbw", "bbb", "wbw", 'b', HyperItems.battery, 'w', Blocks.wool));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(HyperItems.energyConverter), "bbb", "brb", "bbb", 'b', HyperItems.blueIron, 'r', "dustRedstone"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(HyperItems.heatConverter), "  s", " c ", "w  ", 's', HyperItems.stoneWheel, 'c', "blockCopper", 'w', Items.water_bucket));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(HyperItems.stoneWheel), " s ", "sss", " s ", 's', "stone"));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(HyperBlocks.copperBlock), "ccc", "ccc", "ccc", 'c', "ingotCopper"));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(HyperBlocks.energyStorage), "cec", "cic", "c c", 'c', HyperBlocks.copperCable, 'i', "blockIron", 'e', HyperItems.energyTank));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(HyperBlocks.solarPanel), "sss", "ses", "sss", 's', HyperItems.blueSilicon, 'e', HyperBlocks.energyStorage));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(HyperBlocks.ingotEnchanter), "ppp", "pep", "rfr", 'p', "dyePurple", 'e', HyperBlocks.energyStorage, 'r', "dustRedstone", 'f', Blocks.furnace));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(HyperBlocks.generator), "rfr", "rer", "rcr", 'r', "dyeRed", 'f', Blocks.furnace, 'e', HyperBlocks.energyStorage, 'c', HyperItems.heatConverter));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(HyperItems.lasergun, 1, HyperItems.lasergun.getMaxDamage()), "ltc", "iii", "i  ", 'l', Blocks.lever, 't', HyperItems.energyTank, 'c', HyperItems.energyConverter, 'i', "ingotIron"));
	}
}
