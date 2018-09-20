package knokko.blocks;

import knokko.tileentity.TileEntityCable;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockCable extends BlockContainer {
	
	private int capacity;

	public BlockCable(String name, int capacity) {
		super(Material.glass);
		setUnlocalizedName(name);
		setHardness(0.5F);
		setResistance(5);
		setCreativeTab(CreativeTabs.tabDecorations);
		this.capacity = capacity;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityCable();
	}
    
    @Override
    public boolean isOpaqueCube() {
        return false;
    }
    
    public int getCapacity(){
    	return capacity;
    }
}
