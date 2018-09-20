package knokko.slots;

import knokko.items.ItemShipGun;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotShipGun extends Slot {

	public SlotShipGun(IInventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
	}
	
	@Override
	public boolean isItemValid(ItemStack stack){
		return stack != null && stack.getItem() instanceof ItemShipGun;
	}
	
	@Override
	public int getItemStackLimit(ItemStack stack){
		return 1;
	}
}
