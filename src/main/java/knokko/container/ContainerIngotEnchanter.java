package knokko.container;

import java.util.ArrayList;

import knokko.main.HyperCombat;
import knokko.recipes.RecipesIngotEnchanter;
import knokko.slots.SlotBattery;
import knokko.slots.SlotOutputIngotEnchanter;
import knokko.tileentity.TileEntityIngotEnchanter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerIngotEnchanter extends Container {
	
	private final IInventory ingotEnchanter;
    private final int sizeInventory;
    private int ticksEnchantingItemSoFar;
    private int ticksPerItem;
    private int timeCanEnchant;
    private int energy;
    private int enoughEnergy;
    private int[] slots = new int[6];
    
    public boolean config;

    public ContainerIngotEnchanter(InventoryPlayer parInventoryPlayer, IInventory parIInventory){
    	inventorySlots = new ArrayList(){
    		
    		public ContainerIngotEnchanter container = ContainerIngotEnchanter.this;
    		
    		@Override
    		public int size(){
    			return container.config ? 0 : super.size();
    		}
    	};
        ingotEnchanter = parIInventory;
        sizeInventory = ingotEnchanter.getSizeInventory();
        addSlotToContainer(new Slot(ingotEnchanter, TileEntityIngotEnchanter.slotEnum.INPUT_SLOT.ordinal(), 48, 15));
        addSlotToContainer(new Slot(ingotEnchanter, TileEntityIngotEnchanter.slotEnum.SPECIAL_SLOT.ordinal(), 112, 15));
        addSlotToContainer(new SlotOutputIngotEnchanter(parInventoryPlayer.player, ingotEnchanter, TileEntityIngotEnchanter.slotEnum.OUTPUT_SLOT.ordinal(), 80, 35));
        addSlotToContainer(new SlotBattery(ingotEnchanter, TileEntityIngotEnchanter.slotEnum.BATTERY_SLOT.ordinal(), 44, 54));
        int i;
        for (i = 0; i < 3; ++i){
            for (int j = 0; j < 9; ++j){
                addSlotToContainer(new Slot(parInventoryPlayer, j+i*9+9, 
                8+j*18, 84+i*18));
            }
        }
        for (i = 0; i < 9; ++i){
            addSlotToContainer(new Slot(parInventoryPlayer, i, 8 + i * 18, 
            142));
        }
    }

    @Override
    public void addCraftingToCrafters(ICrafting listener){
        super.addCraftingToCrafters(listener);
        listener.func_175173_a(this, ingotEnchanter);
    }

    /**
     * Looks for changes made in the container, sends them to every listener.
     */
    @Override
    public void detectAndSendChanges(){
        super.detectAndSendChanges();
        for (int i = 0; i < crafters.size(); ++i){
            ICrafting icrafting = (ICrafting)crafters.get(i);
            if (ticksEnchantingItemSoFar != ingotEnchanter.getField(2))
                icrafting.sendProgressBarUpdate(this, 2, ingotEnchanter.getField(2));
            if (timeCanEnchant != ingotEnchanter.getField(0))
                icrafting.sendProgressBarUpdate(this, 0, ingotEnchanter.getField(0));
            if (ticksPerItem != ingotEnchanter.getField(3))
                icrafting.sendProgressBarUpdate(this, 3, ingotEnchanter.getField(3));
            if(energy != ingotEnchanter.getField(4))
            	icrafting.sendProgressBarUpdate(this, 4, ingotEnchanter.getField(4));
            if(enoughEnergy != ingotEnchanter.getField(5))
            	icrafting.sendProgressBarUpdate(this, 5, ingotEnchanter.getField(5));
            int t = 0;
            while(t < slots.length){
            	if(slots[t] != ingotEnchanter.getField(6 + t))
            		icrafting.sendProgressBarUpdate(this, 6 + t, ingotEnchanter.getField(6 + t));
            	++t;
            }
        }
        ticksEnchantingItemSoFar = ingotEnchanter.getField(2);
        timeCanEnchant = ingotEnchanter.getField(0); 
        ticksPerItem = ingotEnchanter.getField(3);
        energy = ingotEnchanter.getField(4);
        enoughEnergy = ingotEnchanter.getField(5);
        int t = 0;
        while(t < slots.length){
        	slots[t] = ingotEnchanter.getField(6 + t);
        	++t;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data){
        ingotEnchanter.setField(id, data);
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn){
        return ingotEnchanter.isUseableByPlayer(playerIn);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int slotIndex){
        ItemStack itemStack1 = null;
        Slot slot = (Slot)inventorySlots.get(slotIndex);
        if (slot != null && slot.getHasStack()){
            ItemStack itemStack2 = slot.getStack();
            itemStack1 = itemStack2.copy();
            if (slotIndex == TileEntityIngotEnchanter.slotEnum.OUTPUT_SLOT.ordinal()){
                if (!mergeItemStack(itemStack2, sizeInventory, sizeInventory+36, true))
                    return null;
                slot.onSlotChange(itemStack2, itemStack1);
            }
            else if (slotIndex != TileEntityIngotEnchanter.slotEnum.INPUT_SLOT.ordinal()){
                if (RecipesIngotEnchanter.instance().getEnchantingResult(itemStack2, ingotEnchanter.getStackInSlot(TileEntityIngotEnchanter.slotEnum.SPECIAL_SLOT.ordinal())) != null){
                    if (!mergeItemStack(itemStack2, 0, 1, false))
                        return null;
                }
                else if (slotIndex >= sizeInventory && slotIndex < sizeInventory+27){
                    if (!mergeItemStack(itemStack2, sizeInventory+27, sizeInventory+36, false))
                        return null;
                }
                else if (slotIndex >= sizeInventory+27 && slotIndex < sizeInventory+36 && !mergeItemStack(itemStack2, sizeInventory+1, sizeInventory+27, false))
                    return null;
            }
            else if (!mergeItemStack(itemStack2, sizeInventory, sizeInventory+36, false))
                return null;
            if (itemStack2.stackSize == 0)
                slot.putStack((ItemStack)null);
            else
                slot.onSlotChanged();
            if (itemStack2.stackSize == itemStack1.stackSize)
                return null;
            slot.onPickupFromSlot(playerIn, itemStack2);
        }
        return itemStack1;
    }
}
