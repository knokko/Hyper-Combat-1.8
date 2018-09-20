package knokko.blocks;

import java.util.Random;

import knokko.main.HyperCombat;
import knokko.tileentity.TileEntityIngotEnchanter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockIngotEnchanter extends BlockContainer {
	
		public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	    private static boolean hasTileEntity;

	    public BlockIngotEnchanter()
	    {
	        super(Material.rock);
	        setUnlocalizedName("ingotenchanter");
	        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	        setCreativeTab(CreativeTabs.tabDecorations);
	        stepSound = soundTypeSnow;
	        blockParticleGravity = 1.0F;
	        slipperiness = 0.6F;
	        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	        lightOpacity = 20;
	        setTickRandomly(false);
	        useNeighborBrightness = false;
	    }
	    @Override
	    public Item getItemDropped(IBlockState state, Random rand, int fortune){
	        return Item.getItemFromBlock(HyperBlocks.ingotEnchanter);
	    }

	    @Override
	    public void onBlockAdded(World world, BlockPos pos, IBlockState state){
	        if (!world.isRemote){
	            Block blockToNorth = world.getBlockState(pos.north()).getBlock();
	            Block blockToSouth = world.getBlockState(pos.south()).getBlock();
	            Block blockToWest = world.getBlockState(pos.west()).getBlock();
	            Block blockToEast = world.getBlockState(pos.east()).getBlock();
	            EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
	            if (enumfacing == EnumFacing.NORTH && blockToNorth.isFullBlock() && !blockToSouth.isFullBlock())
	                enumfacing = EnumFacing.SOUTH;
	            else if (enumfacing == EnumFacing.SOUTH && blockToSouth.isFullBlock() && !blockToNorth.isFullBlock())
	                enumfacing = EnumFacing.NORTH;
	            else if (enumfacing == EnumFacing.WEST && blockToWest.isFullBlock() && !blockToEast.isFullBlock())
	                enumfacing = EnumFacing.EAST;
	            else if (enumfacing == EnumFacing.EAST && blockToEast.isFullBlock() && !blockToWest.isFullBlock())
	                enumfacing = EnumFacing.WEST;
	            world.setBlockState(pos, state.withProperty(FACING, enumfacing), 2);
	        }
	    }

	    @Override
	    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ){
	        if (!world.isRemote)
	            player.openGui(HyperCombat.instance, 0, world, pos.getX(), pos.getY(), pos.getZ()); 
	        return true;
	    }

	    @Override
	    public TileEntity createNewTileEntity(World world, int meta){
	        return new TileEntityIngotEnchanter();
	    }

	    @Override
	    public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
	        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	    }

	    @Override
	    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack){
	        world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
	    }

	    @Override
	    public void breakBlock(
	          World worldIn, 
	          BlockPos pos, 
	          IBlockState state)
	    {
	        if (!hasTileEntity)
	        {
	            TileEntity tileentity = worldIn.getTileEntity(pos);

	            if (tileentity instanceof TileEntityIngotEnchanter)
	            {
	                InventoryHelper.dropInventoryItems(worldIn, pos, 
	                      (TileEntityIngotEnchanter)tileentity);
	                worldIn.updateComparatorOutputLevel(pos, this);
	            }
	        }

	        super.breakBlock(worldIn, pos, state);
	    }

	    @Override
	    @SideOnly(Side.CLIENT)
	    public Item getItem(World worldIn, BlockPos pos)
	    {
	        return Item.getItemFromBlock(HyperBlocks.ingotEnchanter);
	    }

	    @Override
	    public int getRenderType()
	    {
	        return 3;
	    }
	    @Override
	    @SideOnly(Side.CLIENT)
	    public IBlockState getStateForEntityRender(IBlockState state)
	    {
	        return getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
	    }

	    @Override
	    public IBlockState getStateFromMeta(int meta)
	    {
	        EnumFacing enumfacing = EnumFacing.getFront(meta);

	        if (enumfacing.getAxis() == EnumFacing.Axis.Y)
	        {
	            enumfacing = EnumFacing.NORTH;
	        }

	        return getDefaultState().withProperty(FACING, enumfacing);
	    }

	    @Override
	    public int getMetaFromState(IBlockState state)
	    {
	        return ((EnumFacing)state.getValue(FACING)).getIndex();
	    }

	    @Override
	    protected BlockState createBlockState()
	    {
	        return new BlockState(this, new IProperty[] {FACING});
	    }

	    @SideOnly(Side.CLIENT)
	    static final class SwitchEnumFacing
	    {
	        static final int[] enumFacingArray = new int[EnumFacing.values()
	              .length];

	        static
	        {
	            try
	            {
	                enumFacingArray[EnumFacing.WEST.ordinal()] = 1;
	            }
	            catch (NoSuchFieldError var4)
	            {
	                ;
	            }

	            try
	            {
	                enumFacingArray[EnumFacing.EAST.ordinal()] = 2;
	            }
	            catch (NoSuchFieldError var3)
	            {
	                ;
	            }

	            try
	            {
	                enumFacingArray[EnumFacing.NORTH.ordinal()] = 3;
	            }
	            catch (NoSuchFieldError var2)
	            {
	                ;
	            }

	            try
	            {
	                enumFacingArray[EnumFacing.SOUTH.ordinal()] = 4;
	            }
	            catch (NoSuchFieldError var1)
	            {
	                // You should improve the error handling here
	            }
	        }
	    }
}
