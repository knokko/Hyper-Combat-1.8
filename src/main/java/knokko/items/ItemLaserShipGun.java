package knokko.items;

import knokko.entities.EntityBattleShip;
import knokko.entities.EntityLaser;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class ItemLaserShipGun extends ItemShipGun {

	public ItemLaserShipGun(int energyCost, int cooldown, int power) {
		super(energyCost, cooldown, power);
	}

	@Override
	public Entity getProjectile(World world, EntityBattleShip launcher, Entity controller, double spawnX, double spawnY, double spawnZ) {
		return new EntityLaser(world, controller, power, spawnX, spawnY, spawnZ);
	}

}
