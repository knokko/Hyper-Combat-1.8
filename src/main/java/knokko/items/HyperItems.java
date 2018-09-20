package knokko.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

public final class HyperItems {
	
	public static final Item testItem = new Item().setUnlocalizedName("testitem").setCreativeTab(CreativeTabs.tabMisc);
	public static final Item silicon = new Item().setUnlocalizedName("silicon").setCreativeTab(CreativeTabs.tabMaterials);
	public static final Item copperIngot = new Item().setUnlocalizedName("copperingot").setCreativeTab(CreativeTabs.tabMaterials);
	public static final Item blueSilicon = new Item().setUnlocalizedName("bluesilicon").setCreativeTab(CreativeTabs.tabMaterials);
	public static final Item blueIron = new Item().setUnlocalizedName("blueiron").setCreativeTab(CreativeTabs.tabMaterials);
	public static final Item stoneWheel = new Item().setUnlocalizedName("stonewheel").setCreativeTab(CreativeTabs.tabMisc);
	public static final Item yellowCopper = new Item().setUnlocalizedName("yellowcopper").setCreativeTab(CreativeTabs.tabMaterials);
	public static final Item energyTank = new Item().setUnlocalizedName("energytank").setCreativeTab(CreativeTabs.tabMisc);
	public static final Item energyConverter = new Item().setUnlocalizedName("energyconverter").setCreativeTab(CreativeTabs.tabMisc);
	public static final Item heatConverter = new Item().setUnlocalizedName("heatconverter").setCreativeTab(CreativeTabs.tabMisc);
	
	public static final ItemBattery battery = new ItemBattery(2000, 10).setUnlocalizedName("battery");
	public static final ItemBattery yellowBattery = new ItemBattery(5000, 50).setUnlocalizedName("yellowbattery");
	
	public static final ItemGun lasergun = new ItemGun(10, 100000, 1000, 100).setUnlocalizedName("lasergun");
	
	public static final ItemShipGun shipLaserGun = new ItemLaserShipGun(1000, 1, 10).setUnlocalizedName("lasershipgun");
	public static final ItemShipGun shipBlastLaserGun = new ItemBlastLaserShipGun(5000, 4, 0.4f).setUnlocalizedName("blastlasershipgun");
	
	public static final ItemHyperBoots hyperBoots = new ItemHyperBoots();
	
	public static final void register(){
		register(testItem);
		register(lasergun);
		register(shipLaserGun);
		register(shipBlastLaserGun);
		register(battery);
		register(yellowBattery);
		register(hyperBoots);
		register(blueSilicon);
		register(blueIron);
		register(yellowCopper);
		register(stoneWheel);
		register(energyTank);
		register(energyConverter);
		register(heatConverter);
		register(silicon, "gemSilicon");
		register(copperIngot, "ingotCopper");
	}
	
	private static final void register(Item item){
		GameRegistry.registerItem(item, item.getUnlocalizedName().substring(5));
	}
	
	private static final void register(Item item, String oreID){
		register(item);
		OreDictionary.registerOre(oreID, item);
	}
}
