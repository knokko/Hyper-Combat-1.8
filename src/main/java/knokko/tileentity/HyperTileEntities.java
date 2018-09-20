package knokko.tileentity;

import knokko.gui.HyperGui;
import knokko.main.HyperCombat;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class HyperTileEntities {

	public static final void register(){
		GameRegistry.registerTileEntity(TileEntityIngotEnchanter.class, "tileEntityIngotEnchanter");
		GameRegistry.registerTileEntity(TileEntityCable.class, "tileEntityCable");
		GameRegistry.registerTileEntity(TileEntitySolarPanel.class, "tileEntitySolarPanel");
		GameRegistry.registerTileEntity(TileEntityEnergyStorage.class, "tileEntityEnergyStorage");
		GameRegistry.registerTileEntity(TileEntityGenerator.class, "tileEntityGenerator");
		NetworkRegistry.INSTANCE.registerGuiHandler(HyperCombat.instance, new HyperGui());
	}
}
