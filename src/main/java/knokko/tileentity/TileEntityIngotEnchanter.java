package knokko.tileentity;

import knokko.blocks.BlockIngotEnchanter;
import knokko.container.ContainerIngotEnchanter;
import knokko.items.ItemBattery;
import knokko.main.HyperCombat;
import knokko.recipes.RecipesIngotEnchanter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityIngotEnchanter extends TileEntityLockable implements IUpdatePlayerListBox, ISidedInventory, IEnergyStorage {
	
	public enum slotEnum 
    {
        INPUT_SLOT, OUTPUT_SLOT, SPECIAL_SLOT, BATTERY_SLOT;
        
        @Override
        public String toString(){
        	return name().toLowerCase().replace('_', ' ');
        }
    }
	
    private int[] slots = new int[]{slotEnum.OUTPUT_SLOT.ordinal(), slotEnum.INPUT_SLOT.ordinal(), slotEnum.SPECIAL_SLOT.ordinal(), slotEnum.SPECIAL_SLOT.ordinal(), slotEnum.SPECIAL_SLOT.ordinal(), slotEnum.SPECIAL_SLOT.ordinal()};
    private ItemStack[] enchanterItems = new ItemStack[4];
    
    private int timeCanEnchant;
    private int currentItemEnchantTime;
    private int ticksEnchantingItemSoFar;
    private int ticksPerItem;
    private int energy;
    private int enoughEnergy = maxEnergy();
    private boolean enabled;
    private String enchanterCustomName;

	@Override
    public boolean shouldRefresh(World parWorld, BlockPos parPos, IBlockState parOldState, IBlockState parNewState){
        return false;
    }

    @Override
    public int getSizeInventory(){
        return enchanterItems.length;
    }

    @Override
    public ItemStack getStackInSlot(int index){
        return enchanterItems[index];
    }

    @Override
    public ItemStack decrStackSize(int index, int count){
        if (enchanterItems[index] != null){
            ItemStack itemstack;
            if (enchanterItems[index].stackSize <= count){
                itemstack = enchanterItems[index];
                enchanterItems[index] = null;
                return itemstack;
            }
            else {
                itemstack = enchanterItems[index].splitStack(count);
                if (enchanterItems[index].stackSize == 0)
                    enchanterItems[index] = null;
                return itemstack;
            }
        }
        else
            return null;
    }

    /**
     * When some containers are closed they call this on each slot, then 
     * drop whatever it returns as an EntityItem -
     * like when you close a workbench GUI.
     */
    @Override
    public ItemStack getStackInSlotOnClosing(int index){
        if (enchanterItems[index] != null){
            ItemStack itemstack = enchanterItems[index];
            enchanterItems[index] = null;
            return itemstack;
        }
        else
            return null;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack){
        boolean isSameItemStackAlreadyInSlot = stack != null && stack.isItemEqual(enchanterItems[index]) && ItemStack.areItemStackTagsEqual(stack, enchanterItems[index]);
        enchanterItems[index] = stack;
        if (stack != null && stack.stackSize > getInventoryStackLimit())
            stack.stackSize = getInventoryStackLimit();
        if ((index == slotEnum.INPUT_SLOT.ordinal() || index == slotEnum.SPECIAL_SLOT.ordinal()) && !isSameItemStackAlreadyInSlot)
        {
            ticksPerItem = timeToEnchantOneItem(stack);
            ticksEnchantingItemSoFar = 0;
            markDirty();
        }
    }

    @Override
    public String getName(){
        return hasCustomName() ? enchanterCustomName : "container.ingotenchanter";
    }

    @Override
    public boolean hasCustomName(){
        return enchanterCustomName != null && enchanterCustomName.length() > 0;
    }

    public void setCustomInventoryName(String parCustomName){
        enchanterCustomName = parCustomName;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        NBTTagList nbttaglist = compound.getTagList("Items", 10);
        enchanterItems = new ItemStack[getSizeInventory()];
        for (int i = 0; i < nbttaglist.tagCount(); ++i){
            NBTTagCompound nbtTagCompound = nbttaglist.getCompoundTagAt(i);
            byte b0 = nbtTagCompound.getByte("Slot");
            if (b0 >= 0 && b0 < enchanterItems.length)
                enchanterItems[b0] = ItemStack.loadItemStackFromNBT(nbtTagCompound);
        }
        timeCanEnchant = compound.getShort("GrindTime");
        ticksEnchantingItemSoFar = compound.getShort("EnchantTime");
        ticksPerItem = compound.getShort("EnchantTimeTotal");
        energy = compound.getShort("Energy");
        enoughEnergy = compound.getShort("EnoughEnergy");
        int t = 0;
        while(t < slots.length){
        	if(compound.hasKey("Slot" + t))
        		slots[t] = compound.getByte("Slot" + t);
        	++t;
        }
        if (compound.hasKey("CustomName", 8))
            enchanterCustomName = compound.getString("CustomName");
    }

    @Override
    public void writeToNBT(NBTTagCompound compound){
        super.writeToNBT(compound);
        compound.setShort("GrindTime", (short)timeCanEnchant);
        compound.setShort("EnchantTime", (short)ticksEnchantingItemSoFar);
        compound.setShort("EnchantTimeTotal", (short)ticksPerItem);
        compound.setShort("Energy", (short)energy);
        compound.setShort("EnoughEnergy", (short) enoughEnergy);
        int t = 0;
        while(t < slots.length){
        	compound.setByte("Slot" + t, (byte) slots[t]);
        	++t;
        }
        NBTTagList nbttaglist = new NBTTagList();
        for (int i = 0; i < enchanterItems.length; ++i){
            if (enchanterItems[i] != null){
                NBTTagCompound nbtTagCompound = new NBTTagCompound();
                nbtTagCompound.setByte("Slot", (byte)i);
                enchanterItems[i].writeToNBT(nbtTagCompound);
                nbttaglist.appendTag(nbtTagCompound);
            }
        }
        compound.setTag("Items", nbttaglist);
        if (hasCustomName())
            compound.setString("CustomName", enchanterCustomName);
    }

    @Override
    public int getInventoryStackLimit(){
        return 64;
    }

    public boolean grindingSomething(){
        return true;
    }

    @SideOnly(Side.CLIENT)
    public static boolean func_174903_a(IInventory parIInventory){
        return true;
    }

    @Override
    public void update(){
    	int t = 0;
		while(!worldObj.isRemote && t < EnumFacing.values().length && energy < enoughEnergy){
			energy += drain(EnumFacing.values()[t], enoughEnergy - energy);
			++t;
		}
        boolean hasBeenGrinding = grindingSomething();
        boolean changedGrindingState = false;
        if (grindingSomething())
            --timeCanEnchant;
        if (!worldObj.isRemote){
            if (enchanterItems[slotEnum.INPUT_SLOT.ordinal()] != null && enchanterItems[slotEnum.SPECIAL_SLOT.ordinal()] != null){                
                if (!grindingSomething() && canEnchant()){
                     timeCanEnchant = 150;
                     if (grindingSomething())
                         changedGrindingState = true;
                }
                if (grindingSomething() && canEnchant()){
                    ++ticksEnchantingItemSoFar;
                    if (ticksEnchantingItemSoFar == ticksPerItem){
                        ticksEnchantingItemSoFar = 0;
                        ticksPerItem = timeToEnchantOneItem(enchanterItems[0]);
                        enchantItem();
                        changedGrindingState = true;
                    }
                }
                else
                    ticksEnchantingItemSoFar = 0;
            }
            if (hasBeenGrinding != grindingSomething())
                changedGrindingState = true;
        }
        
        if(energy < maxEnergy() && enchanterItems[3] != null && enchanterItems[3].getItem() instanceof ItemBattery){
        	ItemBattery battery = (ItemBattery) enchanterItems[3].getItem();
        	int batEnergy = enchanterItems[3].getMaxDamage() - enchanterItems[3].getItemDamage();
        	int drainSpeed = battery.drainSpeed();
        	int requested = enoughEnergy - energy;
        	int drained = Math.min(Math.min(drainSpeed, batEnergy), requested);
        	energy += drained;
        	enchanterItems[3].setItemDamage(drained + enchanterItems[3].getItemDamage());
        }
        if (changedGrindingState)
            markDirty();
    }

    public int timeToEnchantOneItem(ItemStack parItemStack){
        return 100;
    }

    private boolean canEnchant(){
        if (enchanterItems[slotEnum.INPUT_SLOT.ordinal()] == null || enchanterItems[slotEnum.SPECIAL_SLOT.ordinal()] == null)
            return false;
        else {
        	if(energy < 1000 && !worldObj.isRemote){
            	int t = 0;
    			while(t < EnumFacing.values().length && energy < 1000){
    				energy += drain(EnumFacing.values()[t], 1000 - energy);
    				++t;
    			}
    			if(energy < 1000)
    				return false;
            }
            ItemStack itemStackToOutput = RecipesIngotEnchanter.instance().getEnchantingResult(enchanterItems[slotEnum.INPUT_SLOT.ordinal()], enchanterItems[slotEnum.SPECIAL_SLOT.ordinal()]);
            if (itemStackToOutput == null)
            	return false; 
            if (enchanterItems[slotEnum.OUTPUT_SLOT.ordinal()] == null)
            	return true; 
            if (!enchanterItems[slotEnum.OUTPUT_SLOT.ordinal()].isItemEqual(itemStackToOutput))
            	return false; 
            int result = enchanterItems[slotEnum.OUTPUT_SLOT.ordinal()].stackSize + itemStackToOutput.stackSize;
            return result <= getInventoryStackLimit() && result <= enchanterItems[slotEnum.OUTPUT_SLOT.ordinal()].getMaxStackSize();
        }
    }

    protected int drain(EnumFacing facing, int amount){
		TileEntity entity = worldObj.getTileEntity(pos.offset(facing));
		if(entity instanceof IEnergyStorage)
			return ((IEnergyStorage)entity).drainEnergy(facing, amount);
		return 0;
	}

	private void enchantItem(){
        if (canEnchant()){
            ItemStack itemstack = RecipesIngotEnchanter.instance().getEnchantingResult(enchanterItems[slotEnum.INPUT_SLOT.ordinal()], enchanterItems[slotEnum.SPECIAL_SLOT.ordinal()]);
            if (enchanterItems[slotEnum.OUTPUT_SLOT.ordinal()] == null)
                enchanterItems[slotEnum.OUTPUT_SLOT.ordinal()] = itemstack.copy();
            else if (enchanterItems[slotEnum.OUTPUT_SLOT.ordinal()].getItem() == itemstack.getItem())
                enchanterItems[slotEnum.OUTPUT_SLOT.ordinal()].stackSize += itemstack.stackSize; 
            --enchanterItems[slotEnum.INPUT_SLOT.ordinal()].stackSize;
            --enchanterItems[slotEnum.SPECIAL_SLOT.ordinal()].stackSize;
            if (enchanterItems[slotEnum.INPUT_SLOT.ordinal()].stackSize <= 0)
                enchanterItems[slotEnum.INPUT_SLOT.ordinal()] = null;
            if (enchanterItems[slotEnum.SPECIAL_SLOT.ordinal()].stackSize <= 0)
                enchanterItems[slotEnum.SPECIAL_SLOT.ordinal()] = null;
            energy -= 1000;
        }
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer playerIn){
        return worldObj.getTileEntity(pos) != this ? false : playerIn.getDistanceSq(pos.getX()+0.5D, pos.getY()+0.5D, pos.getZ()+0.5D) <= 64.0D;
    }

    @Override
    public void openInventory(EntityPlayer playerIn) {}

    @Override
    public void closeInventory(EntityPlayer playerIn) {}

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack){
        return index == slotEnum.INPUT_SLOT.ordinal() || index == slotEnum.SPECIAL_SLOT.ordinal() || (index == slotEnum.BATTERY_SLOT.ordinal() && stack != null && stack.getItem() instanceof ItemBattery); 
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side){
        return new int[]{slots[side.ordinal()]};
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction){
        return isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int parSlotIndex, ItemStack parStack, EnumFacing parFacing){
        return true;
    }

    @Override
    public String getGuiID(){
        return "knokkohypercombat:ingotenchanter";
    }

    @Override
    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn){
        return new ContainerIngotEnchanter(playerInventory, this);
    }

    @Override
    public int getField(int id){
        switch (id){
            case 0:
                return timeCanEnchant;
            case 1:
                return currentItemEnchantTime;
            case 2:
                return ticksEnchantingItemSoFar;
            case 3:
                return ticksPerItem;
            case 4:
            	return energy;
            case 5:
            	return enoughEnergy;
            case 6:
            	return slots[0];
            case 7:
            	return slots[1];
            case 8:
            	return slots[2];
            case 9:
            	return slots[3];
            case 10:
            	return slots[4];
            case 11:
            	return slots[5];
            default:
                return 0;
        }
    }

    @Override
    public void setField(int id, int value){
        switch (id){
            case 0:
                timeCanEnchant = value;
                break;
            case 1:
                currentItemEnchantTime = value;
                break;
            case 2:
                ticksEnchantingItemSoFar = value;
                break;
            case 3:
                ticksPerItem = value;
                break;
            case 4:
            	if(value <= maxEnergy() && value >= 0)
            		energy = value;
                break;
            case 5:
            	if(value <= maxEnergy() && value >= 0)
            		enoughEnergy = value;
            	break;
            case 6:
            	slot(0, value);
            	break;
            case 7:
            	slot(1, value);
            	break;
            case 8:
            	slot(2, value);
            	break;
            case 9:
            	slot(3, value);
            	break;
            case 10:
            	slot(4, value);
            	break;
            case 11:
            	slot(5, value);
            	break;
        default:
            break;
        }
    }

    @Override
    public int getFieldCount(){
        return 12;
    }
    
    private void slot(int index, int value){
    	slots[index] = value;
    	if(slots[index] > 2)
    		slots[index] = 0;
    }

    @Override
    public void clear(){
        for (int i = 0; i < enchanterItems.length; ++i){
            enchanterItems[i] = null;
        }
    }

	@Override
	public int getEnergy(EnumFacing side) {
		return 0;
	}

	@Override
	public int addEnergy(EnumFacing facing, int in) {
		int out = 0;
		energy += in;
		if(energy > maxEnergy()){
			out = energy - maxEnergy();
			energy = maxEnergy();
		}
		return out;
	}

	@Override
	public int drainEnergy(EnumFacing facing, int requestedAmount) {
		return 0;
	}
	
	public int maxEnergy(){
		return 10000;
	}
}
