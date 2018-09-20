package knokko.tileentity;

import net.minecraft.util.EnumFacing;

public interface IEnergyStorage {
	
	/**
	 * 
	 * @return the amount of energy this block can give to that side
	 */
	public int getEnergy(EnumFacing facing);
	
	/**
	 * 
	 * @param facing the side where the energy comes from
	 * @param in the amount of energy that goes in
	 * @return the amount of energy that returns
	 */
	public int addEnergy(EnumFacing facing, int in);
	
	/**
	 * 
	 * @param facing the side that tries to drain energy
	 * @param requestedAmount the amount of energy that is requested
	 * @return the amount of energy that is given
	 */
	public int drainEnergy(EnumFacing facing, int requestedAmount);
}
