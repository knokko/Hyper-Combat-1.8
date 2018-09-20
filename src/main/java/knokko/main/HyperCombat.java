package knokko.main;

import knokko.blocks.HyperBlocks;
import knokko.entities.EntityHandler;
import knokko.items.HyperItems;
import knokko.packet.GuiMessage;
import knokko.packet.OpenBattleShipMessage;
import knokko.packet.ShootMessage;
import knokko.proxy.Proxy;
import knokko.recipes.HyperRecipes;
import knokko.tileentity.HyperTileEntities;
import knokko.worldgen.HyperGenerator;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = HyperCombat.modid, name = HyperCombat.name, version = HyperCombat.version)
public class HyperCombat {
	
	public static final String modid = "knokkohypercombat";
	public static final String name = "Hyper Combat";
	public static final String version = "0.1.0";
	
	public static SimpleNetworkWrapper network;
	
	@SidedProxy(clientSide = "knokko.proxy.ClientProxy", serverSide = "knokko.proxy.ServerProxy")
	public static Proxy proxy;
	
	@Instance(modid)
	public static HyperCombat instance;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		network = NetworkRegistry.INSTANCE.newSimpleChannel("Knokko Hyper Combat");
		network.registerMessage(GuiMessage.Handler.class, GuiMessage.class, 0, Side.SERVER);
		network.registerMessage(ShootMessage.Handler.class, ShootMessage.class, 1, Side.SERVER);
		network.registerMessage(OpenBattleShipMessage.Handler.class, OpenBattleShipMessage.class, 2, Side.SERVER);
		HyperItems.register();
		HyperBlocks.register();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event){
		proxy.load();
		HyperEventHandler.load();
		EntityHandler.registerEntities();
		HyperTileEntities.register();
		HyperGenerator.register();
		HyperRecipes.register();
	}
}
