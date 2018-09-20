package knokko.blocks;

import knokko.main.HyperCombat;
import knokko.tileentity.TileEntitySolarPanel;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockSolarPanel extends BlockContainer {
	
	private int power;
	private int maxEnergy;

	public BlockSolarPanel(String name, int power, int maxEnergy) {
		super(Material.rock);
		this.power = power;
		this.maxEnergy = maxEnergy;
		setHardness(4);
		setResistance(10);
		setUnlocalizedName(name);
		setCreativeTab(CreativeTabs.tabDecorations);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntitySolarPanel();
	}
	
	public int getPower(){
		return power;
	}
	
	public int getMaxEnergy(){
		return maxEnergy;
	}
	
	public int getRenderType(){
        return 3;
    }
	
	@Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ){
        if (!world.isRemote)
            player.openGui(HyperCombat.instance, 1, world, pos.getX(), pos.getY(), pos.getZ()); 
        return true;
    }
}
