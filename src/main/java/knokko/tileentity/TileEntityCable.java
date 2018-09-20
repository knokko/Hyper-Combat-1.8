package knokko.tileentity;

import knokko.blocks.BlockCable;
import knokko.main.HyperCombat;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class TileEntityCable extends TileEntity implements IEnergyStorage{
	
	private int capacity;
	private boolean isDraining;

	@Override
	public int getEnergy(EnumFacing facing) {
		return 0;
	}

	@Override
	public int addEnergy(EnumFacing facing, int in) {
		return in;
	}

	@Override
	public int drainEnergy(EnumFacing facing, int requestedAmount) {
		int energy = 0;
		if(!isDraining && worldObj != null){
			if(capacity == 0){
				Block block = worldObj.getBlockState(pos).getBlock();
				if(block instanceof BlockCable)
					capacity = ((BlockCable) block).getCapacity();
			}
			if(requestedAmount > capacity)
				requestedAmount = capacity;
			isDraining = true;
			int t = 0;
			while(t < EnumFacing.values().length){
				if(energy >= requestedAmount){
					isDraining = false;
					return energy;
				}
				if(facing != EnumFacing.values()[t].getOpposite()){
					int extra = drain(EnumFacing.values()[t], requestedAmount - energy);
					energy += extra;
				}
				++t;
			}
		}
		isDraining = false;
		return energy;
	}
	
	public boolean connect(TileEntity other){
		return other instanceof IEnergyStorage;
	}
	
	protected int drain(EnumFacing facing, int amount){
		TileEntity entity = worldObj.getTileEntity(pos.offset(facing));
		if(entity instanceof IEnergyStorage){
			return ((IEnergyStorage)entity).drainEnergy(facing, amount);
		}
		return 0;
	}
}
