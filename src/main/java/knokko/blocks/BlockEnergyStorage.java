package knokko.blocks;

import knokko.main.HyperCombat;
import knokko.tileentity.TileEntityEnergyStorage;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockEnergyStorage extends BlockContainer {
	
	private int maxEnergy;

	public BlockEnergyStorage(String name, int maxEnergy) {
		super(Material.rock);
		this.maxEnergy = maxEnergy;
		setUnlocalizedName(name);
		setHardness(4);
		setResistance(10);
		setCreativeTab(CreativeTabs.tabDecorations);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityEnergyStorage();
	}
	
	@Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ){
        if (!world.isRemote)
            player.openGui(HyperCombat.instance, 3, world, pos.getX(), pos.getY(), pos.getZ()); 
        return true;
    }
	
	@Override
	public int getRenderType(){
        return 3;
    }
	
	public int getMaxEnergy(){
		return maxEnergy;
	}
}
