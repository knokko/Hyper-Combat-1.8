package knokko.entities;

import static java.lang.Math.*;
import knokko.blocks.BlockSolarPanel;
import knokko.items.ItemBattery;
import knokko.items.ItemShipGun;
import knokko.main.HyperCombat;
import knokko.packet.ShootMessage;
import knokko.proxy.Proxy;
import knokko.tileentity.IEnergyStorage;
import knokko.tileentity.TileEntityIngotEnchanter.slotEnum;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityBattleShip extends Entity implements IInventory, IEnergyStorage{
	
	private ItemStack[] inventory = new ItemStack[68];
	
	private int energy;
	
	private byte cooldownLeft;
	private byte cooldownRight;

	public EntityBattleShip(World world){
		super(world);
		setSize(14, 4);
	}
	
	public EntityBattleShip(World world, double x, double y, double z){
		this(world);
		setPosition(x, y, z);
	}
	
	@Override
	public void onUpdate(){
		super.onUpdate();
		if (riddenByEntity != null){
			if(riddenByEntity instanceof EntityLivingBase){
				EntityLivingBase rider = (EntityLivingBase)riddenByEntity;
				if(rider.moveForward > 0 && energy >= 1000){
            		double speedMultiplier = 0.05;//TODO moves glitchy on servers
					motionX += -Math.sin(Math.toRadians(riddenByEntity.rotationYaw)) * Math.cos(Math.toRadians(riddenByEntity.rotationPitch)) * speedMultiplier;
					motionY += Math.sin(-Math.toRadians(riddenByEntity.rotationPitch)) * speedMultiplier;
            		motionZ += Math.cos(Math.toRadians(riddenByEntity.rotationYaw)) * Math.cos(Math.toRadians(riddenByEntity.rotationPitch)) * speedMultiplier;
            		energy -= 1000;
				}
				else if(rider.moveForward < 0){
            		double brakeMultiplier = 0.9;
            		motionX *= brakeMultiplier;
            		motionY *= brakeMultiplier;
            		motionZ *= brakeMultiplier;
            	}
			}
            rotationYaw = riddenByEntity.rotationYaw;
			rotationPitch = riddenByEntity.rotationPitch;
        }
		//System.out.println("motionX = " + motionX + " and isRemote = " + worldObj.isRemote);
		moveEntity(motionX, motionY, motionZ);
		motionX *= 0.96;
		if(motionX < 0.001 && motionX > -0.001)
			motionX = 0;
		motionY *= 0.96;
		if(motionY < 0.001 && motionY > -0.001)
			motionY = 0;
		motionZ *= 0.96;
		if(motionZ < 0.001 && motionZ > -0.001)
			motionZ = 0;
		if(riddenByEntity instanceof EntityPlayer && Proxy.isKeyDown("use item"))
			HyperCombat.network.sendToServer(new ShootMessage((EntityPlayer) riddenByEntity, false));
		if(riddenByEntity instanceof EntityPlayer && Proxy.isKeyDown("attack"))
			HyperCombat.network.sendToServer(new ShootMessage((EntityPlayer) riddenByEntity, true));
		if(cooldownLeft > 0)
			--cooldownLeft;
		if(cooldownRight > 0)
			--cooldownRight;
		int t = 60;
		while(t < 63){
			Item item = inventory[t] != null ? inventory[t].getItem() : null;
			if(item != null){
				Block block = Block.getBlockFromItem(item);
				if(block instanceof BlockSolarPanel){
					BlockSolarPanel panel = (BlockSolarPanel) block;
					if(worldObj.canBlockSeeSky(getPosition()) && worldObj.isDaytime())
						energy += panel.getPower();
					if(energy > maxEnergy())
						energy = maxEnergy();
				}
			}
			++t;
		}
		while(t < 66 && energy < maxEnergy()){
			Item item = inventory[t] != null ? inventory[t].getItem() : null;
			if(item instanceof ItemBattery){
				int requested = maxEnergy() - energy;
				int possible = ((ItemBattery)inventory[t].getItem()).drainSpeed;
				int left = inventory[t].getMaxDamage() - inventory[t].getItemDamage();
				int e = Math.min(Math.min(requested, possible), left);
				energy += e;
				inventory[t].setItemDamage(e + inventory[t].getItemDamage());
			}
			++t;
		}
	}
	
	@Override
	public void updateRiderPosition(){
		if(riddenByEntity != null){
			if(riddenByEntity instanceof EntityLivingBase)
				rotationYaw = ((EntityLivingBase)riddenByEntity).rotationYawHead;
			else
				rotationYaw = riddenByEntity.rotationYaw;
			rotationPitch = riddenByEntity.rotationPitch;
			riddenByEntity.setPosition(posX - sin(toRadians(rotationYaw)) * cos(toRadians(rotationPitch)) * 5, posY - riddenByEntity.getEyeHeight() - sin(toRadians(rotationPitch)) * 5, posZ + cos(toRadians(rotationYaw)) * cos(toRadians(rotationPitch)) * 5);
		}
	}
	
	@Override
	public boolean canBePushed(){
		return false;
	}
	
	@Override
	public float getEyeHeight(){
        return riddenByEntity != null ? 2 + riddenByEntity.getEyeHeight() : 2;
    }
	
	@Override
	public double getMountedYOffset(){
        return 0;
    }
	
	@Override
	public boolean interactFirst(EntityPlayer player){
		if(isUseableByPlayer(player)){
			if(player.isSneaking()){
			}
			else {
				player.mountEntity(this);
			}
			return true;
		}
		return false;
	}
	
	@Override
	public boolean func_174825_a(EntityPlayer player, Vec3 v3){
		return interactFirst(player);
	}
	
	@Override
	public AxisAlignedBB getCollisionBox(Entity entity){
        return null;
    }
	
	@Override
    public AxisAlignedBB getBoundingBox(){
        return getEntityBoundingBox();
    }
	
	@Override
	public boolean canBeCollidedWith(){
        return !this.isDead;
    }
	
	@Override
	public boolean canRiderInteract(){
        return false;
    }

	@Override
	protected void entityInit() {}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		NBTTagList nbttaglist = nbt.getTagList("Items", 10);
        inventory = new ItemStack[getSizeInventory()];
        for (int i = 0; i < nbttaglist.tagCount(); ++i){
            NBTTagCompound nbtTagCompound = nbttaglist.getCompoundTagAt(i);
            byte b0 = nbtTagCompound.getByte("Slot");
            if (b0 >= 0 && b0 < inventory.length)
                inventory[b0] = ItemStack.loadItemStackFromNBT(nbtTagCompound);
        }
        energy = nbt.getInteger("Energy");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		NBTTagList nbttaglist = new NBTTagList();
        for (int i = 0; i < inventory.length; ++i){
            if (inventory[i] != null){
                NBTTagCompound nbtTagCompound = new NBTTagCompound();
                nbtTagCompound.setByte("Slot", (byte)i);
                inventory[i].writeToNBT(nbtTagCompound);
                nbttaglist.appendTag(nbtTagCompound);
            }
        }
        nbt.setTag("Items", nbttaglist);
        nbt.setInteger("Energy", energy);
	}

	@Override
	public int getSizeInventory() {
		return inventory.length;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		if(index < getSizeInventory())
			return inventory[index];
		return null;
	}

	@Override
    public ItemStack decrStackSize(int index, int count){
        if(index < getSizeInventory() && inventory[index] != null){
            ItemStack itemstack;
            if (inventory[index].stackSize <= count){
                itemstack = inventory[index];
                inventory[index] = null;
                return itemstack;
            }
            else {
                itemstack = inventory[index].splitStack(count);
                if (inventory[index].stackSize == 0)
                    inventory[index] = null;
                return itemstack;
            }
        }
        else
            return null;
    }

	 @Override
	    public ItemStack getStackInSlotOnClosing(int index){
	        if (index < getSizeInventory() && inventory[index] != null){
	            ItemStack itemstack = inventory[index];
	            inventory[index] = null;
	            return itemstack;
	        }
	        else
	            return null;
	    }

	 @Override
	    public void setInventorySlotContents(int index, ItemStack stack){
	     inventory[index] = stack;
	     if(stack != null && stack.stackSize > getInventoryStackLimit())
	        stack.stackSize = getInventoryStackLimit();
	    }

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty() {}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return player.getDistanceToEntity(this) <= 7;
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return true;
	}

	@Override
	public int getField(int id) {
		if(id == 0)
			return energy;
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		if(id == 0)
			energy = value > maxEnergy() ? maxEnergy() : value;
	}

	@Override
	public int getFieldCount() {
		return 1;
	}

	@Override
	public void clear() {
		inventory = new ItemStack[getSizeInventory()];
	}
	
	public void shoot(boolean left){
		if(left && inventory[66] != null && inventory[66].getItem() instanceof ItemShipGun){
			ItemShipGun leftGun = (ItemShipGun) inventory[66].getItem();
			if(energy >= leftGun.energyCost && cooldownLeft <= 0){
				double x2 = riddenByEntity.posX - Math.sin(Math.toRadians(rotationYaw - 90)) * 3.5;
				double y2 = riddenByEntity.posY + riddenByEntity.getEyeHeight() - Math.sin(Math.toRadians(rotationPitch)) * 3.5;
				double z2 = riddenByEntity.posZ + Math.cos(Math.toRadians(rotationYaw - 90)) * 3.5;
				worldObj.spawnEntityInWorld(leftGun.getProjectile(this, x2, y2, z2));
				energy -= leftGun.energyCost;
				cooldownLeft = (byte) leftGun.cooldown;
			}
		}
		if(!left && inventory[67] != null && inventory[67].getItem() instanceof ItemShipGun){
			ItemShipGun rightGun = (ItemShipGun) inventory[67].getItem();
			if(energy >= rightGun.energyCost && cooldownRight <= 0){
				double x1 = riddenByEntity.posX - Math.sin(Math.toRadians(rotationYaw + 90)) * 3.5;
				double y1 = riddenByEntity.posY + riddenByEntity.getEyeHeight() - Math.sin(Math.toRadians(rotationPitch)) * 3.5;
				double z1 = riddenByEntity.posZ + Math.cos(Math.toRadians(rotationYaw + 90)) * 3.5;
				worldObj.spawnEntityInWorld(rightGun.getProjectile(this, x1, y1, z1));
				energy -= rightGun.energyCost;
				cooldownRight = (byte) rightGun.cooldown;
			}
		}
	}

	@Override
	public int getEnergy(EnumFacing facing) {
		return energy;
	}

	@Override
	public int addEnergy(EnumFacing facing, int in) {
		if(energy + in <= maxEnergy())
			energy += in;
		else {
			int e = energy + in - maxEnergy();
			energy = maxEnergy();
			return e;
		}
		return 0;
	}

	@Override
	public int drainEnergy(EnumFacing facing, int requestedAmount) {
		if(requestedAmount <= energy){
			energy -= requestedAmount;
			return requestedAmount;
		}
		int e = energy;
		energy = 0;
		return e;
	}
	
	public int maxEnergy(){
		return 10000000;
	}
}
