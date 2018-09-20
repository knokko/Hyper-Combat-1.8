package knokko.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;

public class ItemBattery extends Item implements IEnergyItem{
	
	public final int drainSpeed;

	public ItemBattery(int maxStorage, int maxDrainSpeed) {
		setMaxDamage(maxStorage);
		setCreativeTab(CreativeTabs.tabTools);
		drainSpeed = maxDrainSpeed;
	}
	
	@Override
	public ItemBattery setUnlocalizedName(String name){
		super.setUnlocalizedName(name);
		return this;
	}

	@Override
	public int drainSpeed() {
		return drainSpeed;
	}
}
