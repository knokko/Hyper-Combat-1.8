package knokko.container;

import knokko.items.ItemBattery;
import knokko.recipes.RecipesIngotEnchanter;
import knokko.slots.SlotBattery;
import knokko.slots.SlotEnergyItem;
import knokko.tileentity.TileEntityIngotEnchanter;
import knokko.tileentity.TileEntitySolarPanel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerSolarPanel extends Container {
	
	private final TileEntitySolarPanel tileEntity;
	
	private int energy;
	private byte timer = 5;
	private boolean[] giveEnergyToSide = new boolean[6];

	public ContainerSolarPanel(InventoryPlayer player, TileEntitySolarPanel panel) {
		tileEntity = panel;
		addSlotToContainer(new SlotEnergyItem(tileEntity, 0, 61, 35));
		int i;
        for (i = 0; i < 3; ++i){
            for (int j = 0; j < 9; ++j){
                addSlotToContainer(new Slot(player, j + i * 9 + 9, 11 + j * 18, 156 + i * 18));
            }
        }
        for (i = 0; i < 9; ++i){
            addSlotToContainer(new Slot(player, i, 11 + i * 18, 214));
        }
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return tileEntity.isUseableByPlayer(player);
	}
	
	@Override
	public void detectAndSendChanges(){
		super.detectAndSendChanges();
        for (int i = 0; i < crafters.size(); ++i){
            ICrafting icrafting = (ICrafting)crafters.get(i);
            if(energy != tileEntity.energy())
            	icrafting.sendProgressBarUpdate(this, 0, tileEntity.energy());
            int t = 0;
            while(t < 6){
            	if(giveEnergyToSide[t] != tileEntity.giveEnergyToSide[t]){
            		icrafting.sendProgressBarUpdate(this, t + 3, tileEntity.giveEnergyToSide[t] ? 1 : 0);
            	}
            	++t;
            }
            if(timer <= 0){
            	icrafting.sendProgressBarUpdate(this, 0, tileEntity.energy());
            	t = 0;
            	while(t < 6){
            		icrafting.sendProgressBarUpdate(this, t + 3, tileEntity.giveEnergyToSide[t] ? 1 : 0);
            		++t;
            	}
            }
        }
        energy = tileEntity.energy();
        int t = 0;
        while(t < 6){
        	giveEnergyToSide[t] = tileEntity.giveEnergyToSide[t];
        	++t;
        }
        if(timer > 0)
        	--timer;
	}
	
	@Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data){
        if(id == 0)
        	tileEntity.setEnergy(data);
        if(id >= 3 && id <= 8)
        	tileEntity.setField(id, data);
    }
	
	@Override
    public void addCraftingToCrafters(ICrafting listener){
        super.addCraftingToCrafters(listener);
        listener.func_175173_a(this, tileEntity);
    }
	
	@Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int slotIndex){
        ItemStack itemStack1 = null;
        Slot slot = (Slot)inventorySlots.get(slotIndex);
        if (slot != null && slot.getHasStack()){
            ItemStack itemStack2 = slot.getStack();
            itemStack1 = itemStack2.copy();
            int sizeInventory = tileEntity.getSizeInventory();
			if (slotIndex != 0){
                if (slotIndex >= sizeInventory && slotIndex < sizeInventory + 27){
                    if (!mergeItemStack(itemStack2, sizeInventory + 27, sizeInventory + 36, false))
                        return null;
                }
                else if (slotIndex >= sizeInventory + 27 && slotIndex < sizeInventory + 36 && !mergeItemStack(itemStack2, sizeInventory + 1, sizeInventory + 27, false))
                    return null;
            }
            else
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
