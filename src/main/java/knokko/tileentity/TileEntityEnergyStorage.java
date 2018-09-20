package knokko.tileentity;

import java.util.Arrays;

import knokko.blocks.BlockCable;
import knokko.blocks.BlockEnergyStorage;
import knokko.blocks.BlockSolarPanel;
import knokko.items.IEnergyItem;
import knokko.items.ItemBattery;
import knokko.tileentity.TileEntityIngotEnchanter.slotEnum;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;

public class TileEntityEnergyStorage extends TileEntity implements IEnergyStorage, IUpdatePlayerListBox, ISidedInventory {
	
	public static final int INPUT_SLOT = 0;
	public static final int OUTPUT_SLOT = 1;
	
	private ItemStack[] inventory = new ItemStack[2];
	private int[] slots = new int[]{INPUT_SLOT, INPUT_SLOT, INPUT_SLOT, OUTPUT_SLOT, OUTPUT_SLOT, OUTPUT_SLOT};
	public boolean[] giveEnergyToSide = new boolean[]{true, true, true, true, true, true};
	
	private String customName;
	
	public int energy;
	public int maxEnergy;
	
	private boolean isDraining;
	
	@Override
	public void writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("energy", energy);
		nbt.setByte("giveEnergyToSide", TileEntitySolarPanel.fromBinair(Arrays.copyOf(giveEnergyToSide, 8)));
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
        if (hasCustomName())
            nbt.setString("CustomName", customName);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		energy = nbt.getInteger("energy");
		giveEnergyToSide = Arrays.copyOf(TileEntitySolarPanel.toBinair(nbt.getByte("giveEnergyToSide")), 6);
		NBTTagList nbttaglist = nbt.getTagList("Items", 10);
        inventory = new ItemStack[getSizeInventory()];
        for (int i = 0; i < nbttaglist.tagCount(); ++i){
            NBTTagCompound nbtTagCompound = nbttaglist.getCompoundTagAt(i);
            byte b0 = nbtTagCompound.getByte("Slot");
            if (b0 >= 0 && b0 < inventory.length)
                inventory[b0] = ItemStack.loadItemStackFromNBT(nbtTagCompound);
        }
        if(nbt.hasKey("CustomName", 8))
            customName = nbt.getString("CustomName");
	}

	@Override
	public String getName() {
		return hasCustomName() ? customName : "container.energystorage";
	}

	@Override
	public boolean hasCustomName() {
		return customName != null && customName.length() > 0;
	}

	@Override
	public IChatComponent getDisplayName() {
		return (IChatComponent)(this.hasCustomName() ? new ChatComponentText(this.getName()) : new ChatComponentTranslation(this.getName(), new Object[0]));
	}

	@Override
	public int getSizeInventory() {
		return inventory.length;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return index < inventory.length ? inventory[index] : null;
	}

	@Override
    public ItemStack decrStackSize(int index, int count){
        if (inventory[index] != null){
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
        if (inventory[index] != null){
            ItemStack itemstack = inventory[index];
            inventory[index] = null;
            return itemstack;
        }
        else
            return null;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack){
        boolean isSameItemStackAlreadyInSlot = stack != null && stack.isItemEqual(inventory[index]) && ItemStack.areItemStackTagsEqual(stack, inventory[index]);
        inventory[index] = stack;
        if (stack != null && stack.stackSize > getInventoryStackLimit())
            stack.stackSize = getInventoryStackLimit();
    }

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
    public boolean isUseableByPlayer(EntityPlayer playerIn){
        return worldObj.getTileEntity(pos) != this ? false : playerIn.getDistanceSq(pos.getX()+0.5D, pos.getY()+0.5D, pos.getZ()+0.5D) <= 64.0D;
    }

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return index < inventory.length && stack.getItem() instanceof IEnergyItem;
	}

	@Override
	public int getField(int id) {
		if(id == 0)
			return energy;
		if(id >= 1 && id <= 6)
			return slots[id - 1];
		if(id >= 7 && id <= 12)
			return giveEnergyToSide[id - 7] ? 1 : 0;
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		if(id == 0)
			energy = value > maxEnergy ? maxEnergy : value;
		if(id >= 1 && id <= 6)
			slots[id - 1] = value;
		if(id >= 7 && id <= 12)
			giveEnergyToSide[id - 7] = value > 0;
	}

	@Override
	public int getFieldCount() {
		return 13;
	}

	@Override
	public void clear() {
		inventory = new ItemStack[inventory.length];
	}

	@Override
	public void update() {
		getMaxEnergy();
		if(energy < maxEnergy && inventory[INPUT_SLOT] != null && inventory[INPUT_SLOT].getItem() instanceof ItemBattery){
			int available = inventory[INPUT_SLOT].getMaxDamage() - inventory[INPUT_SLOT].getItemDamage();
			int speed = ((ItemBattery) inventory[INPUT_SLOT].getItem()).drainSpeed();
			int requested = maxEnergy - energy;
			int actual = Math.min(Math.min(available, speed), requested);
			energy += actual;
			inventory[INPUT_SLOT].setItemDamage(inventory[INPUT_SLOT].getItemDamage() + actual);
		}
		if(energy > 0 && inventory[OUTPUT_SLOT] != null && inventory[OUTPUT_SLOT].getItem() instanceof IEnergyItem){
			int available = energy;
			int speed = ((IEnergyItem) inventory[OUTPUT_SLOT].getItem()).drainSpeed();
			int requested = inventory[OUTPUT_SLOT].getItemDamage();
			int actual = Math.min(Math.min(available, speed), requested);
			energy -= actual;
			inventory[OUTPUT_SLOT].setItemDamage(inventory[OUTPUT_SLOT].getItemDamage() - actual);
		}
	}

	@Override
	public int getEnergy(EnumFacing facing) {
		return energy;
	}

	@Override
	public int addEnergy(EnumFacing facing, int in) {
		energy += in;
		if(energy > maxEnergy){
			int out = maxEnergy - energy;
			energy = maxEnergy;
			return out;
		}
		return 0;
	}

	@Override
	public int drainEnergy(EnumFacing facing, int requestedAmount) {
		if(!giveEnergyToSide[facing.getOpposite().ordinal()])
			return 0;
		if(energy >= requestedAmount){
			energy -= requestedAmount;
			return requestedAmount;
		}
		int energy = this.energy;
		if(!isDraining && worldObj != null){
			isDraining = true;
			int t = 0;
			while(t < EnumFacing.values().length){
				if(energy >= requestedAmount){
					isDraining = false;
					this.energy = energy - requestedAmount;
					return requestedAmount;
				}
				if(facing != EnumFacing.values()[t].getOpposite()){
					int extra = drain(EnumFacing.values()[t], requestedAmount - energy);
					energy += extra;
				}
				++t;
			}
		}
		isDraining = false;
		this.energy = Math.max(0, energy - requestedAmount);
		return Math.min(energy, requestedAmount);
	}
	
	protected int drain(EnumFacing facing, int amount){
		TileEntity entity = worldObj.getTileEntity(pos.offset(facing));
		if(entity instanceof IEnergyStorage){
			return ((IEnergyStorage)entity).drainEnergy(facing, amount);
		}
		return 0;
	}
	
	protected void getMaxEnergy(){
		if(maxEnergy == 0 && worldObj != null){
			Block block = worldObj.getBlockState(pos).getBlock();
			if(block instanceof BlockEnergyStorage)
				maxEnergy = ((BlockEnergyStorage) block).getMaxEnergy();
		}
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return new int[]{slots[side.ordinal()]};
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return isItemValidForSlot(index, itemStackIn);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return true;
	}
}
