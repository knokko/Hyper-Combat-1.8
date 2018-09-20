package knokko.container;

import knokko.entities.EntityBattleShip;
import knokko.recipes.RecipesIngotEnchanter;
import knokko.slots.SlotBattery;
import knokko.slots.SlotShipGun;
import knokko.slots.SlotSolarPanel;
import knokko.tileentity.TileEntityIngotEnchanter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerBattleShip extends Container {
	
	private final EntityBattleShip ship;
	
	private final int sizeInventory;
	private int energy;

	public ContainerBattleShip(InventoryPlayer player, EntityBattleShip battleShip) {
		ship = battleShip;
		sizeInventory = ship.getSizeInventory() - 6;
		int i;
		for(i = 0; i < 6; ++i){
        	for(int j = 0; j < 9; ++j){
        		addSlotToContainer(new Slot(ship, j + i * 9, j * 18 + 6, i * 18 + 6));
        	}
        }
        addSlotToContainer(new SlotSolarPanel(ship, 60, 173, 24));
        addSlotToContainer(new SlotSolarPanel(ship, 61, 191, 24));
        addSlotToContainer(new SlotSolarPanel(ship, 62, 209, 24));
        addSlotToContainer(new SlotBattery(ship, 63, 173, 60));
        addSlotToContainer(new SlotBattery(ship, 64, 191, 60));
        addSlotToContainer(new SlotBattery(ship, 65, 209, 60));
        addSlotToContainer(new SlotShipGun(ship, 66, 173, 96));
        addSlotToContainer(new SlotShipGun(ship, 67, 209, 96));
        for (i = 0; i < 3; ++i){
            for (int j = 0; j < 9; ++j){
                addSlotToContainer(new Slot(player, j + i * 9 + 9, 6 + j * 18, 120 + i * 18));
            }
        }
        for (i = 0; i < 9; ++i){
            addSlotToContainer(new Slot(player, i, 6 + i * 18, 178));
        }
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return ship.riddenByEntity == player;
	}
	
	@Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int slotIndex){
        ItemStack itemStack1 = null;
        Slot slot = (Slot)inventorySlots.get(slotIndex);
        if (slot != null && slot.getHasStack()){
            ItemStack itemStack2 = slot.getStack();
            itemStack1 = itemStack2.copy();
            boolean flag = false;
            if (slotIndex >= sizeInventory && !mergeItemStack(itemStack2, 0, sizeInventory, false))
                flag = true;
            else if (!mergeItemStack(itemStack2, sizeInventory, sizeInventory + 36, false))
                flag = true;
            if (itemStack2.stackSize == 0)
                slot.putStack((ItemStack)null);
            else
                slot.onSlotChanged();
            if(flag)
            	return null;
            if (itemStack2.stackSize == itemStack1.stackSize)
                return null;
            slot.onPickupFromSlot(playerIn, itemStack2);
        }
        return itemStack1;
    }
	
	@Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data){
        ship.setField(id, data);
    }
	
	@Override
    public void addCraftingToCrafters(ICrafting listener){
        super.addCraftingToCrafters(listener);
        listener.func_175173_a(this, ship);
    }
	
	public void detectAndSendChanges(){
        super.detectAndSendChanges();
        for (int i = 0; i < crafters.size(); ++i){
            ICrafting icrafting = (ICrafting)crafters.get(i);
            if (energy != ship.getField(0))
                icrafting.sendProgressBarUpdate(this, 0, ship.getField(0));
        }
        energy = ship.getField(0);
    }
}
