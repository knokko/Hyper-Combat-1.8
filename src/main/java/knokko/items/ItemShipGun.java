package knokko.items;

import knokko.entities.EntityBattleShip;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public abstract class ItemShipGun extends Item {
	
	public final int energyCost;
	public final int cooldown;
	public final float power;

	public ItemShipGun(int energyCost, int cooldown, float power) {
		setCreativeTab(CreativeTabs.tabCombat);
		this.energyCost = energyCost;
		this.cooldown = cooldown;
		this.power = power;
	}
	
	@Override
	public ItemShipGun setUnlocalizedName(String name){
		super.setUnlocalizedName(name);
		return this;
	}
	
	public Entity getProjectile(EntityBattleShip launcher, double spawnX, double spawnY, double spawnZ){
		return getProjectile(launcher.worldObj, launcher, launcher.riddenByEntity != null ? launcher.riddenByEntity : launcher, spawnX, spawnY, spawnZ);
	}
	
	public abstract Entity getProjectile(World world, EntityBattleShip launcher, Entity controller, double spawnX, double spawnY, double spawnZ);
}
