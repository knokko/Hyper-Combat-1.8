package knokko.items;

import knokko.entities.EntityBattleShip;
import knokko.entities.EntityBlastLaser;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class ItemBlastLaserShipGun extends ItemShipGun {

	public ItemBlastLaserShipGun(int energyCost, int cooldown, float power) {
		super(energyCost, cooldown, power);
	}

	@Override
	public Entity getProjectile(World world, EntityBattleShip launcher, Entity controller, double spawnX, double spawnY, double spawnZ) {
		return new EntityBlastLaser(world, controller, power, spawnX, spawnY, spawnZ);
	}

}
