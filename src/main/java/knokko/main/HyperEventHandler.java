package knokko.main;

import knokko.entities.EntityBattleShip;
import knokko.gui.GuiBattleShip;
import knokko.items.ItemGun;
import knokko.packet.OpenBattleShipMessage;
import knokko.proxy.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class HyperEventHandler {
	
	public static NBTTagCompound zooms = new NBTTagCompound();
	
	public static void load(){
		HyperEventHandler handler = new HyperEventHandler();
		FMLCommonHandler.instance().bus().register(handler);
		MinecraftForge.EVENT_BUS.register(handler);
	}
	
	public static boolean canZoom(EntityPlayer player){
		ItemStack s = player.getCurrentEquippedItem();
		if(s != null && s.getItem() instanceof ItemGun)
			return true;
		if(player.ridingEntity instanceof EntityBattleShip)
			return true;
		return false;
	}
	
	public static boolean shouldUpdateZoom(EntityPlayer player){
		if(zooms.getFloat(player.getName()) != 1){
			return true;
		}
		if(zooms.getBoolean("reset:" + player.getName())){
			zooms.setBoolean("reset:" + player.getName(), false);
			return true;
		}
		return false;
	}
	
	@SubscribeEvent
	public void updateFOV(FOVUpdateEvent event){
		if(!canZoom(event.entity)){
			zooms.setFloat(event.entity.getName(), 0);
			zooms.setBoolean("reset:" + event.entity.getName(), true);
		}
		if(shouldUpdateZoom(event.entity)){
			float zoom = zooms.getFloat(event.entity.getName());
			if(zoom != 0)
				event.newfov = zoom;
			else {
				event.newfov = event.fov;
				zooms.setFloat(event.entity.getName(), event.fov);
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void mouseUpdate(MouseEvent event){
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		if(player != null){
		String name = player.getName();
			if(canZoom(player)){
				if(Proxy.isKeyDown("z")){
					int scroll = event.dwheel;
					float zoom = zooms.getFloat(name);
					if(scroll < 0){
						zoom *= 1.2;
						event.setCanceled(true);
					}
					else if(scroll > 0){
						zoom /= 1.2;
						event.setCanceled(true);
					}
					if(zoom < 0.09346389)
						zoom = 0.09346389F;
					if(zoom > 1.7280004F)
						zoom = 1.7280004F;
					zooms.setFloat(name, zoom);
				}
				else {
					zooms.setFloat(name, 0);
					zooms.setBoolean("reset:" + name, true);
				}
			}
			else {
				zooms.setFloat(name, 0);
				zooms.setBoolean("reset:" + name, true);
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void openGui(GuiOpenEvent event){
		if(event.gui instanceof GuiInventory){
			EntityPlayer player = Minecraft.getMinecraft().thePlayer;
			if(player.ridingEntity instanceof EntityBattleShip){
				event.setCanceled(true);
				//event.gui = new GuiBattleShip(player.inventory, (EntityBattleShip) player.ridingEntity);
				HyperCombat.network.sendToServer(new OpenBattleShipMessage(player));
				//player.openGui(HyperCombat.modid, 2, player.worldObj, (int)player.posX, (int)player.posY, (int)player.posZ);
			}
		}
	}
}
