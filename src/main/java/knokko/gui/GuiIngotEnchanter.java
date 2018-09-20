package knokko.gui;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import knokko.container.ContainerIngotEnchanter;
import knokko.main.HyperCombat;
import knokko.packet.GuiMessage;
import knokko.tileentity.TileEntityIngotEnchanter;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class GuiIngotEnchanter extends GuiContainer {
	
		private static final ResourceLocation textureIngotEnchanter = new ResourceLocation(HyperCombat.modid + ":textures/gui/ingotenchanter.png");
		private static final ResourceLocation textureConfiguration = new ResourceLocation(HyperCombat.modid + ":textures/gui/ingotenchanter configuration.png");
	    private final InventoryPlayer inventoryPlayer;
	    private final TileEntityIngotEnchanter inventory;
	    
	    private boolean configuration;

	    public GuiIngotEnchanter(InventoryPlayer player, TileEntityIngotEnchanter enchanter){
	        super(new ContainerIngotEnchanter(player, enchanter));
	        inventoryPlayer = player;
	        inventory = enchanter;
	    }

	    @Override
	    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
	        fontRendererObj.drawString(configuration ? "ingot enchanter" : "configuration", configuration ? 91 : 102, configuration ? 148 : 62, 4210752);
	        if(!configuration){
	        	String s = inventory.getDisplayName().getUnformattedText();
	 	        fontRendererObj.drawString(s, xSize/2-fontRendererObj.getStringWidth(s)/2, 6, 4210752);
	 	        fontRendererObj.drawString(inventoryPlayer.getDisplayName().getUnformattedText(), 8, ySize - 96 + 2, 4210752);
	        }
	        else {
	        	fontRendererObj.drawString("drain energy until:", 12, 12, 4034732);
	        	fontRendererObj.drawString(inventory.getField(5) + "", 113, 12, 4034732);
	        	int t = 0;
	        	while(t < 6){
	        		fontRendererObj.drawString(EnumFacing.values()[t].name().toLowerCase(), 12, 27 + t * 15, 4034732);
	        		fontRendererObj.drawString(TileEntityIngotEnchanter.slotEnum.values()[inventory.getField(6 + t)].toString(), 60, 27 + t * 15, 4034732);
	        		++t;
	        	}
	        }
	    }

	    @Override
	    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
	        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	        mc.getTextureManager().bindTexture(configuration ? textureConfiguration : textureIngotEnchanter);
	        int marginHorizontal = (width - xSize) / 2;
	        int marginVertical = (height - ySize) / 2;
	        drawTexturedModalRect(marginHorizontal, marginVertical, 0, 0, xSize, ySize);
	        if(!configuration){
	        	int progressLevel = progressLevel(12);
		        int energyLevel = energyLevel(38);
	        	drawTexturedModalRect(marginHorizontal + 76, marginVertical + 17, 176, 14, progressLevel, 16);
	        	drawTexturedModalRect(marginHorizontal + 100 - progressLevel, marginVertical + 17, 200 - progressLevel, 14, progressLevel, 16);
	        	drawTexturedModalRect(marginHorizontal + 21, marginVertical + 69 - energyLevel, 176, 31, 18, energyLevel != 0 ? energyLevel + 1 : 0);
	        }
	    }
	    
	    @Override
	    protected void mouseClicked(int x, int y, int button) throws IOException{
	    	super.mouseClicked(x, y, button);
	    	int mh = (width - xSize) / 2;
	    	int mv = (height - ySize) / 2;
	    	BlockPos pos = inventory.getPos();
	    	if(x >= mh + (configuration ? 90 : 100) && x <= mh + 170 && y >= mv + (configuration ? 146 : 60) && y <= mv + (configuration ? 156 : 70)){
	    		configuration = !configuration;
	    		if(inventorySlots instanceof ContainerIngotEnchanter)
	    			((ContainerIngotEnchanter)inventorySlots).config = configuration;
	    	}
	    	else if(x > mh + 150 && x < mh + 160 && y > mv + 10 && y < mv + 20)
	    		HyperCombat.network.sendToServer(new GuiMessage(pos.getX(), pos.getY(), pos.getZ(), 5, inventory.getField(5) - 1000, inventory.getWorld().provider.getDimensionId()));
	    	else if(x > mh + 160 && x < mh + 170 && y > mv + 10 && y < mv + 20)
	    		HyperCombat.network.sendToServer(new GuiMessage(pos.getX(), pos.getY(), pos.getZ(), 5, inventory.getField(5) + 1000, inventory.getWorld().provider.getDimensionId()));
	    	else if(x >= mh + 57 && y >= mv + 25 && x <= mh + 121 && y <= mv + 110){
	    		int ry = y - 25 - mv;
	    		ry /= 15;
	    		HyperCombat.network.sendToServer(new GuiMessage(pos.getX(), pos.getY(), pos.getZ(), 6 + ry, inventory.getField(6 + ry) + 1, inventory.getWorld().provider.getDimensionId()));
	    	}
	    }

	    private int energyLevel(int max) {
	    	double energy = inventory.getField(4);
	    	int maxEnergy = inventory.maxEnergy();
			return (int) ((energy / maxEnergy) * max);
		}

		private int progressLevel(int progressIndicatorPixelWidth){
	        int ticksGrindingItemSoFar = inventory.getField(2); 
	        int ticksPerItem = inventory.getField(3);
	        return ticksPerItem != 0 && ticksGrindingItemSoFar != 0 ? ticksGrindingItemSoFar * progressIndicatorPixelWidth / ticksPerItem + 1 : 0;
	    }
}
