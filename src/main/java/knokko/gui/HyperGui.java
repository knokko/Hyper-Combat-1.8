package knokko.gui;

import knokko.container.ContainerBattleShip;
import knokko.container.ContainerEnergyStorage;
import knokko.container.ContainerGenerator;
import knokko.container.ContainerIngotEnchanter;
import knokko.container.ContainerSolarPanel;
import knokko.entities.EntityBattleShip;
import knokko.tileentity.TileEntityEnergyStorage;
import knokko.tileentity.TileEntityGenerator;
import knokko.tileentity.TileEntityIngotEnchanter;
import knokko.tileentity.TileEntitySolarPanel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public final class HyperGui implements IGuiHandler{
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity entity = world.getTileEntity(new BlockPos(x, y, z));
		if(entity != null){
			if(ID == 0 && entity instanceof TileEntityIngotEnchanter)
				return new ContainerIngotEnchanter(player.inventory, (IInventory) entity);
			if(ID == 1 && entity instanceof TileEntitySolarPanel)
				return new ContainerSolarPanel(player.inventory, (TileEntitySolarPanel) entity);
			if(ID == 3 && entity instanceof TileEntityEnergyStorage)
				return new ContainerEnergyStorage(player.inventory, (TileEntityEnergyStorage) entity);
			if(ID == 4 && entity instanceof TileEntityGenerator)
				return new ContainerGenerator(player.inventory, (TileEntityGenerator) entity);
		}
		if(ID == 2 && player.ridingEntity instanceof EntityBattleShip)
			return new ContainerBattleShip(player.inventory, (EntityBattleShip) player.ridingEntity);
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity entity = world.getTileEntity(new BlockPos(x, y, z));
		if(entity != null){
			if(ID == 0 && entity instanceof TileEntityIngotEnchanter)
				return new GuiIngotEnchanter(player.inventory, (TileEntityIngotEnchanter) entity);
			if(ID == 1 && entity instanceof TileEntitySolarPanel)
				return new GuiSolarPanel(player.inventory, (TileEntitySolarPanel) entity);
			if(ID == 3 && entity instanceof TileEntityEnergyStorage)
				return new GuiEnergyStorage(player.inventory, (TileEntityEnergyStorage) entity);
			if(ID == 4 && entity instanceof TileEntityGenerator)
				return new GuiGenerator(player.inventory, (TileEntityGenerator) entity);
		}
		if(ID == 2 && player.ridingEntity instanceof EntityBattleShip)
			return new GuiBattleShip(player.inventory, (EntityBattleShip)player.ridingEntity);
		return null;
	}
}
