package knokko.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class HyperBlock extends Block {
	
	protected Item dropItem;

	public HyperBlock(Material material, String name, String tool, CreativeTabs tab, int hardness, int resistance, int toolLevel) {
		super(material);
		setUnlocalizedName(name);
		setHardness(hardness);
		setResistance(resistance);
		setCreativeTab(tab);
		setHarvestLevel(tool, toolLevel);
	}
	
	public HyperBlock setDropItem(Item item){
		dropItem = item;
		return this;
	}
	
	public Item getItemDropped(IBlockState state, Random random, int fortune){
		return dropItem;
	}
}
