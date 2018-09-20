package knokko.proxy;

import org.lwjgl.input.Mouse;

import knokko.blocks.HyperBlocks;
import knokko.entities.EntityBattleShip;
import knokko.entities.EntityLaser;
import knokko.entities.render.RenderBattleShip;
import knokko.entities.render.RenderLaser;
import knokko.items.HyperItems;
import knokko.main.HyperCombat;
import knokko.main.HyperKeyBindings;
import knokko.render.RenderCable;
import knokko.tileentity.TileEntityCable;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.MouseHelper;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class ClientProxy extends Proxy {
	
	@Override
	public Side getSide() {
		return Side.CLIENT;
	}
	
	@Override
	public void load(){
		HyperKeyBindings.load();
		register(HyperItems.testItem);
		register(HyperItems.battery);
		register(HyperItems.yellowBattery);
		register(HyperItems.lasergun);
		register(HyperItems.shipLaserGun);
		register(HyperItems.shipBlastLaserGun);
		register(HyperItems.hyperBoots);
		register(HyperItems.silicon);
		register(HyperItems.blueSilicon);
		register(HyperItems.blueIron);
		register(HyperItems.yellowCopper);
		register(HyperItems.stoneWheel);
		register(HyperItems.copperIngot);
		register(HyperItems.energyTank);
		register(HyperItems.energyConverter);
		register(HyperItems.heatConverter);
		register(HyperBlocks.ingotEnchanter);
		register(HyperBlocks.generator);
		register(HyperBlocks.copperCable);
		register(HyperBlocks.solarPanel);
		register(HyperBlocks.energyStorage);
		register(HyperBlocks.siliconOre);
		register(HyperBlocks.copperOre);
		register(HyperBlocks.copperBlock);
		RenderManager manager = Minecraft.getMinecraft().getRenderManager();
		RenderingRegistry.registerEntityRenderingHandler(EntityLaser.class, new RenderLaser(manager));
		RenderingRegistry.registerEntityRenderingHandler(EntityBattleShip.class, new RenderBattleShip(manager));
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCable.class, new RenderCable());
	}
	
	private static void register(Item item){
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(HyperCombat.modid + ":" + item.getUnlocalizedName().substring(5), "inventory"));
	}
	
	private static void register(Block block){
		register(Item.getItemFromBlock(block));
	}
	
	@Override
	boolean iskeyDown(String key){
		if(key.matches("z"))
			return HyperKeyBindings.z.isKeyDown();
		if(key.matches("jump"))
			return Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown();
		if(key.matches("scroll forward"))
			return Mouse.getDWheel() > 0;
		if(key.matches("scroll backward"))
			return Mouse.getDWheel() < 0;
		if(key.matches("sneak"))
			return Minecraft.getMinecraft().gameSettings.keyBindSneak.isKeyDown();
		if(key.matches("use item"))
			return Minecraft.getMinecraft().gameSettings.keyBindUseItem.isKeyDown();
		if(key.matches("attack"))
			return Minecraft.getMinecraft().gameSettings.keyBindAttack.isKeyDown();
		return false;
	}
}
