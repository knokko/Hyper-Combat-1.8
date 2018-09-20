package knokko.tileentity;

import java.util.Arrays;

import knokko.container.ContainerGenerator;
import knokko.items.IEnergyItem;
import knokko.items.ItemBattery;
import knokko.tileentity.TileEntityIngotEnchanter.slotEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class TileEntityGenerator extends TileEntityLockable implements IUpdatePlayerListBox, ISidedInventory, IEnergyStorage {
	
	private static final int FUEL_SLOT = 0;
	private static final int ITEM_SLOT = 1;
	
	private ItemStack[] inventory = new ItemStack[2];
	
	private String customName;
	
	public boolean[] giveEnergyToSide = new boolean[]{true, true, true, true, true, true};
	
	public int ticksPerItem;
	public int burnProgress;
	public int energy;

	@Override
	public int getSizeInventory() {
		return 2;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return inventory[index];
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
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
	public ItemStack getStackInSlotOnClosing(int index) {
		if (inventory[index] != null){
            ItemStack itemstack = inventory[index];
            inventory[index] = null;
            return itemstack;
        }
        else
            return null;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		boolean isSameItemStackAlreadyInSlot = stack != null && stack.isItemEqual(inventory[index]) && ItemStack.areItemStackTagsEqual(stack, inventory[index]);
        inventory[index] = stack;
        if (stack != null && stack.stackSize > getInventoryStackLimit())
            stack.stackSize = getInventoryStackLimit();
	}

	private int timeToBurnOneItem(ItemStack stack) {
		return TileEntityFurnace.getItemBurnTime(stack);
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
    public boolean isUseableByPlayer(EntityPlayer player){
        return worldObj.getTileEntity(pos) != this ? false : player.getDistanceSq(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64;
    }

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return stack != null && ((index == 0 && TileEntityFurnace.isItemFuel(stack)) || (index == 1 && stack.getItem() instanceof IEnergyItem));
	}

	@Override
	public int getField(int id) {
		if(id == 0)
			return energy;
		if(id == 1)
			return burnProgress;
		if(id == 2)
			return ticksPerItem;
		if(id >= 3 && id <= 8)
			return giveEnergyToSide[id - 3] ? 1 : 0;
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		if(id == 0)
			energy = value;
		if(id == 1)
			burnProgress = value;
		if(id == 2)
			ticksPerItem = value;
		if(id >= 3 && id <= 8)
			giveEnergyToSide[id - 3] = value > 0;
	}

	@Override
	public int getFieldCount() {
		return 9;
	}

	@Override
	public void clear() {
		inventory = new ItemStack[getSizeInventory()];
	}

	@Override
	public String getName() {
		return hasCustomName() ? customName : "container.generator";
	}

	@Override
	public boolean hasCustomName() {
		return customName != null && customName.length() > 0;
	}

	@Override
	public Container createContainer(InventoryPlayer inventory, EntityPlayer player) {
		return new ContainerGenerator(inventory, this);
	}

	@Override
	public String getGuiID() {
		return "knokkohypercombat:generator";
	}

	@Override
	public int getEnergy(EnumFacing facing) {
		return giveEnergyToSide[facing.ordinal()] ? energy : 0;
	}

	@Override
	public int addEnergy(EnumFacing facing, int in) {
		energy += in;
		if(energy > maxEnergy()){
			int out = energy - maxEnergy();
			energy = maxEnergy();
			return out;
		}
		return 0;
	}

	@Override
	public int drainEnergy(EnumFacing facing, int requestedAmount) {
		if(giveEnergyToSide[facing.ordinal()]){
			if(energy > requestedAmount){
				energy -= requestedAmount;
				return requestedAmount;
			}
			int out = energy;
			energy = 0;
			return out;
		}
		return 0;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return new int[]{0};
	}

	@Override
	public boolean canInsertItem(int index, ItemStack stack, EnumFacing direction) {
		return isItemValidForSlot(index, stack);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return true;
	}

	@Override
	public void update() {
		boolean changedBurningState = false;
		if(!worldObj.isRemote){
            if(burnProgress == 0 && inventory[FUEL_SLOT] != null && canBurn()){                
                 ticksPerItem = timeToBurnOneItem(inventory[FUEL_SLOT]);
                 burnItem();
                 changedBurningState = true;
                 burnProgress = 1;
                 energy++;
            }
            else if(burnProgress < ticksPerItem && burnProgress > 0 && energy < maxEnergy()){
            	++burnProgress;
            	++energy;
            }
            else if(burnProgress >= ticksPerItem){
            	burnProgress = 0;
            	changedBurningState = true;
            }
        }
		if(energy < maxEnergy() && inventory[ITEM_SLOT] != null && inventory[ITEM_SLOT].getItem() instanceof IEnergyItem){
        	IEnergyItem battery = (IEnergyItem) inventory[ITEM_SLOT].getItem();;
        	int drainSpeed = battery.drainSpeed();
        	int requested = inventory[ITEM_SLOT].getItemDamage();
        	int drained = Math.min(Math.min(drainSpeed, energy), requested);
        	energy -= drained;
        	inventory[ITEM_SLOT].setItemDamage(inventory[ITEM_SLOT].getItemDamage() - drained);
        }
        if(changedBurningState)
            markDirty();
	}
	
	@Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
        return false;
    }
	
	@Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        NBTTagList nbttaglist = compound.getTagList("Items", 10);
        inventory = new ItemStack[getSizeInventory()];
        for (int i = 0; i < nbttaglist.tagCount(); ++i){
            NBTTagCompound nbtTagCompound = nbttaglist.getCompoundTagAt(i);
            byte b0 = nbtTagCompound.getByte("Slot");
            if (b0 >= 0 && b0 < inventory.length)
                inventory[b0] = ItemStack.loadItemStackFromNBT(nbtTagCompound);
        }
        burnProgress = compound.getShort("BurnProgress");
        ticksPerItem = compound.getShort("TotalBurnTime");
        energy = compound.getShort("Energy");
        giveEnergyToSide = Arrays.copyOf(TileEntitySolarPanel.toBinair(compound.getByte("giveEnergyToSide")), 6);
        if(compound.hasKey("CustomName", 8))
            customName = compound.getString("CustomName");
    }

    @Override
    public void writeToNBT(NBTTagCompound compound){
        super.writeToNBT(compound);
        compound.setShort("BurnProgress", (short)burnProgress);
        compound.setShort("TotalBurnTime", (short)ticksPerItem);
        compound.setShort("Energy", (short)energy);
        compound.setByte("giveEnergyToSide", TileEntitySolarPanel.fromBinair(Arrays.copyOf(giveEnergyToSide, 8)));
        NBTTagList nbttaglist = new NBTTagList();
        for (int i = 0; i < inventory.length; ++i){
            if (inventory[i] != null){
                NBTTagCompound nbtTagCompound = new NBTTagCompound();
                nbtTagCompound.setByte("Slot", (byte)i);
                inventory[i].writeToNBT(nbtTagCompound);
                nbttaglist.appendTag(nbtTagCompound);
            }
        }
        compound.setTag("Items", nbttaglist);
        if (hasCustomName())
            compound.setString("CustomName", customName);
    }
	
	public int maxEnergy(){
		return 10000;
	}
	
	private boolean canBurn(){
		return !worldObj.isRemote && energy < maxEnergy() && inventory[FUEL_SLOT] != null && TileEntityFurnace.isItemFuel(inventory[FUEL_SLOT]);
	}
	
	private void burnItem(){
		inventory[FUEL_SLOT].stackSize--;
		if(inventory[FUEL_SLOT].stackSize <= 0)
			inventory[FUEL_SLOT] = null;
	}
}
