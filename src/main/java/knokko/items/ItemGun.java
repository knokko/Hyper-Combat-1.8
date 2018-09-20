package knokko.items;

import knokko.entities.EntityBattleShip;
import knokko.entities.EntityLaser;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemGun extends Item implements IEnergyItem {
	
	public final int power;
	public final int cost;
	public final int drainSpeed;
	
	public ItemGun(int power, int maxEnergy, int cost, int drain) {
		setCreativeTab(CreativeTabs.tabCombat);
		setMaxDamage(maxEnergy);
		this.power = power;
		this.cost = cost;
		this.drainSpeed = drain;
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player){
		if(getMaxDamage() - stack.getItemDamage() >= cost){
			if(!world.isRemote)
				world.spawnEntityInWorld(getProjectile(world, player));
			stack.setItemDamage(stack.getItemDamage() + cost);
		}
		return super.onItemRightClick(stack, world, player);
	}
	
	@Override
	public ItemGun setUnlocalizedName(String name){
		super.setUnlocalizedName(name);
		return this;
	}

	@Override
	public int drainSpeed() {
		return drainSpeed;
	}
	
	public Entity getProjectile(World world, EntityPlayer player){
		return new EntityLaser(world, player, power);
	}
}
