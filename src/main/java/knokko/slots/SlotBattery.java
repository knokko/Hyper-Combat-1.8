package knokko.slots;

import knokko.items.ItemBattery;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SlotBattery extends Slot {

	public SlotBattery(IInventory inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
	}
	
	@Override
	public boolean isItemValid(ItemStack stack){
		return stack != null && stack.getItem() instanceof ItemBattery;
	}
	
	@Override
	public int getItemStackLimit(ItemStack stack){
		return 1;
	}
}
