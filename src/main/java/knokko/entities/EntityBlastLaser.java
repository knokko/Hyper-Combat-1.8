package knokko.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityBlastLaser extends EntityLaser {

	public EntityBlastLaser(World world) {
		super(world);
	}

	public EntityBlastLaser(World world, Entity entity, float power) {
		super(world, entity, power);
	}
	
	public EntityBlastLaser(World world, Entity shooter, float power, double x, double y, double z) {
		super(world, shooter, power, x, y, z);
	}

	@Override
	public void onImpact(MovingObjectPosition mop){
		Vec3 v = mop.hitVec;
		worldObj.createExplosion(this, v.xCoord, v.yCoord, v.zCoord, power, worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing"));
		setDead();
	}
}
