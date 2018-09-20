package knokko.entities;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityLaser extends Entity implements IProjectile{
	
	public Entity thrower;
	public float power;
	
	public EntityLaser(World world){
		super(world);
	}
	
	public EntityLaser(World world, Entity entity, float power) {
		super(world);
		this.power = power;
		thrower = entity;
		setPosition(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
		setSize(1, 0.25F);
		rotationYaw = entity.rotationYaw;
		rotationPitch = entity.rotationPitch;
		double speed = 8.5;
		motionX = -Math.sin(Math.toRadians(rotationYaw)) * Math.cos(Math.toRadians(rotationPitch)) * speed;
        motionZ = Math.cos(Math.toRadians(rotationYaw)) * Math.cos(Math.toRadians(rotationPitch)) * speed;
        motionY = -Math.sin(Math.toRadians(rotationPitch)) * speed;
	}
	
	public EntityLaser(World world, Entity entity, float power, double x, double y, double z){
		this(world, entity, power);
		setPosition(x, y, z);
	}

	@Override
	public void entityInit() {}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		power = nbt.getFloat("power");
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setFloat("power", power);
	}
	
	@Override
	public void onUpdate(){
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		super.onUpdate();
		Vec3 vec1 = new Vec3(posX, posY, posZ);
		Vec3 vec2 = new Vec3(posX + motionX, posY + motionY, posZ + motionZ);
		MovingObjectPosition mop = worldObj.rayTraceBlocks(vec1, vec2);
		if (mop != null)
            vec2 = new Vec3(mop.hitVec.xCoord, mop.hitVec.yCoord, mop.hitVec.zCoord);
        if (!worldObj.isRemote){
            Entity entity = null;
            List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().addCoord(motionX, motionY, motionZ).expand(1, 1, 1));
            double d0 = 0.0D;
            Entity entitylivingbase = thrower;
            for (int j = 0; j < list.size(); ++j){
                Entity entity1 = (Entity)list.get(j);
                if (entity1.canBeCollidedWith() && (entity1 != entitylivingbase || ticksExisted >= 5)){
                    float f = 0.3F;
                    AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand((double)f, (double)f, (double)f);
                    MovingObjectPosition movingobjectposition1 = axisalignedbb.calculateIntercept(vec1, vec2);
                    if (movingobjectposition1 != null){
                        double d1 = vec1.distanceTo(movingobjectposition1.hitVec);
                        if (d1 < d0 || d0 == 0.0D){
                            entity = entity1;
                            d0 = d1;
                        }
                    }
                }
            }
            if (entity != null)
                mop = new MovingObjectPosition(entity);
        }
        if (mop != null){
            if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && worldObj.getBlockState(mop.getBlockPos()).getBlock() == Blocks.portal)
                setInPortal();
            else if(mop.entityHit == null || (mop.entityHit != thrower && mop.entityHit != thrower.riddenByEntity && mop.entityHit != thrower.ridingEntity))
            	onImpact(mop);
            else
            	setPosition(posX + motionX, posY + motionY, posZ + motionZ);
        }
        else
        	setPosition(posX + motionX, posY + motionY, posZ + motionZ);
	}

	@Override
	public void setThrowableHeading(double x, double y, double z, float velocity, float inaccuracy) {
		double distance = Math.sqrt(x * x + y * y + z * z);
		motionX = x = (x * velocity) / distance;
		motionY = y = (y * velocity) / distance;
		motionZ = z = (z * velocity) / distance;
		prevRotationYaw = rotationYaw = (float) Math.toDegrees(Math.atan2(x, z));
		prevRotationPitch = rotationPitch = (float)(Math.toDegrees(Math.atan2(y, Math.hypot(x, z))));
	}
	
	public void onImpact(MovingObjectPosition mop){
		if(!worldObj.isRemote && mop.entityHit != null){
			mop.entityHit.hurtResistantTime = 0;
			mop.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, thrower), power);
		}
		setDead();
	}
}
