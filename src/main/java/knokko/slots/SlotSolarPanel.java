package knokko.slots;

import knokko.blocks.BlockSolarPanel;
import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotSolarPanel extends Slot {

	public SlotSolarPanel(IInventory inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
	}
	
	@Override
	public boolean isItemValid(ItemStack stack){
		return Block.getBlockFromItem(stack.getItem()) instanceof BlockSolarPanel;
	}
	
	@Override
	public int getItemStackLimit(ItemStack stack){
		return 1;
	}
}
