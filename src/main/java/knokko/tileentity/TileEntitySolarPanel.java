package knokko.tileentity;

import java.util.Arrays;

import knokko.blocks.BlockSolarPanel;
import knokko.items.IEnergyItem;
import knokko.items.ItemBattery;
import knokko.packet.GuiMessage;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntitySolarPanel extends TileEntity implements IEnergyStorage, IUpdatePlayerListBox, IInventory {
	
	public static final byte[] BYTES = new byte[]{64,32,16,8,4,2,1};
	
	private ItemStack[] inventory = new ItemStack[1];
	
	private String customName;
	
	protected int power;
	protected int energy;
	protected int maxEnergy;
	
	public boolean[] giveEnergyToSide = new boolean[]{true, true, true, true, true, true};

	@Override
	public int getEnergy(EnumFacing facing) {
		return energy;
	}

	@Override
	public int addEnergy(EnumFacing facing, int in) {
		getMaxEnergy();
		energy += in;
		int out = 0;
		if(energy > maxEnergy){
			out = energy - maxEnergy;
			energy = maxEnergy;
		}
		return out;
	}

	@Override
	public int drainEnergy(EnumFacing facing, int requestedAmount) {
		if(giveEnergyToSide[facing.getOpposite().ordinal()])
			return energy > requestedAmount ? requestedAmount : energy;
		else
			return 0;
	}
	
	@Override
	public void update(){
		getPower();
		getMaxEnergy();
		if(worldObj.canBlockSeeSky(getPos()) && worldObj.isDaytime())
			energy += power;
		if(energy > 0 && inventory[0] != null && inventory[0].getItem() instanceof IEnergyItem){
			IEnergyItem battery = (IEnergyItem) inventory[0].getItem();
			int drain = battery.drainSpeed();
			int maxDrain = inventory[0].getItemDamage();
			int actual = Math.min(Math.min(drain, maxDrain), energy);
			energy -= actual;
			inventory[0].setItemDamage(maxDrain - actual);
		}
		if(energy > maxEnergy)
			energy = maxEnergy;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("energy", energy);
		nbt.setByte("giveEnergyToSide", fromBinair(Arrays.copyOf(giveEnergyToSide, 8)));
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
        if(hasCustomName())
            nbt.setString("CustomName", customName);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		NBTTagList nbttaglist = nbt.getTagList("Items", 10);
        inventory = new ItemStack[getSizeInventory()];
        for (int i = 0; i < nbttaglist.tagCount(); ++i){
            NBTTagCompound nbtTagCompound = nbttaglist.getCompoundTagAt(i);
            byte b0 = nbtTagCompound.getByte("Slot");
            if (b0 >= 0 && b0 < inventory.length)
                inventory[b0] = ItemStack.loadItemStackFromNBT(nbtTagCompound);
        }
		energy = nbt.getInteger("energy");
		giveEnergyToSide = Arrays.copyOf(toBinair(nbt.getByte("giveEnergyToSide")), 6);
		if(nbt.hasKey("CustomName", 8))
            customName = nbt.getString("CustomName");
	}
	
	protected void getPower(){
		if(power == 0 && worldObj != null){
			Block block = worldObj.getBlockState(pos).getBlock();
			if(block instanceof BlockSolarPanel)
				power = ((BlockSolarPanel) block).getPower();
		}
	}
	
	protected void getMaxEnergy(){
		if(maxEnergy == 0 && worldObj != null){
			Block block = worldObj.getBlockState(pos).getBlock();
			if(block instanceof BlockSolarPanel)
				maxEnergy = ((BlockSolarPanel) block).getMaxEnergy();
		}
	}

	public boolean isUseableByPlayer(EntityPlayer player) {
		return worldObj.getTileEntity(pos) != this ? false : player.getDistanceSq(pos.getX()+0.5D, pos.getY()+0.5D, pos.getZ()+0.5D) <= 64.0D;
	}
	
	public int power(){
		return power;
	}
	
	public int energy(){
		return energy;
	}
	
	public int maxEnergy(){
		return maxEnergy;
	}
	
	public void setField(int id, int value){
		if(id >= 3 && id <= 8)
			giveEnergyToSide[id - 3] = value > 0;
	}
	
	public int getField(int id){
		if(id == 0)
			return power();
		if(id == 1)
			return energy;
		if(id == 2)
			return maxEnergy();
		if(id >= 3 && id <= 8)
			return giveEnergyToSide[id - 3] ? 1 : 0;
		return 0;
	}
	
	public void setEnergy(int value){
		if(worldObj.isRemote)
			energy = value;
	}
	
	public static boolean[] toBinair(byte b){
		boolean[] bools = new boolean[8];
		if(b >= 0)
			bools[7] = true;
		else {
			b++;
			b *= -1;
		}
		byte t = 0;
		while(t < 7){
			if(b >= BYTES[t]){
				b -= BYTES[t];
				bools[t] = true;
			}
			++t;
		}
		return bools;
	}
	
	public static byte fromBinair(boolean[] bools){
		byte b = 0;
		int t = 0;
		while(t < 7){
			if(bools[t])
				b += BYTES[t];
			++t;
		}
		if(!bools[7]){
			b *= -1;
			b--;
		}
		return b;
	}

	@Override
	public String getName() {
		return hasCustomName() ? customName : "container.solarpanel";
	}

	@Override
	public boolean hasCustomName() {
		return customName != null && customName.length() > 0;
	}

	@Override
	public IChatComponent getDisplayName(){
        return (IChatComponent)(this.hasCustomName() ? new ChatComponentText(this.getName()) : new ChatComponentTranslation(this.getName(), new Object[0]));
    }

	@Override
	public int getSizeInventory() {
		return inventory.length;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return index < getSizeInventory() ? inventory[index] : null;
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
		return 1;
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return stack != null && stack.getItem() instanceof ItemBattery;
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		inventory = new ItemStack[inventory.length];
	}
}
