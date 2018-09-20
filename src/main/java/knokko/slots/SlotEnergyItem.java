package knokko.slots;

import knokko.items.IEnergyItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotEnergyItem extends Slot {

	public SlotEnergyItem(IInventory inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
	}
	
	@Override
	public boolean isItemValid(ItemStack stack){
		return stack != null && stack.getItem() instanceof IEnergyItem;
	}
	
	@Override
	public int getItemStackLimit(ItemStack stack){
		return 1;
	}
}
