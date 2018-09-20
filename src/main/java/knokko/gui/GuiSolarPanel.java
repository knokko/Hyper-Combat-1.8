package knokko.gui;

import java.io.IOException;

import knokko.container.ContainerSolarPanel;
import knokko.main.HyperCombat;
import knokko.packet.GuiMessage;
import knokko.tileentity.TileEntityIngotEnchanter;
import knokko.tileentity.TileEntitySolarPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class GuiSolarPanel extends GuiContainer {
	
	private static final ResourceLocation texture = new ResourceLocation(HyperCombat.modid + ":textures/gui/solarpanel.png");
	
	private final TileEntitySolarPanel tileEntity;
	private int mh;
    private int mv;

	public GuiSolarPanel(InventoryPlayer player, TileEntitySolarPanel panel) {
		super(new ContainerSolarPanel(player, panel));
		tileEntity = panel;
		ySize = 236;
		mh = (width - xSize) / 2;
		mv = (height - ySize) / 2;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y){
		int t = 0;
    	while(t < 6){
    		fontRendererObj.drawString("Give energy to " + EnumFacing.values()[t].name().toLowerCase() + "?", 12, 64 + t * 15, 4034732);
    		fontRendererObj.drawString((tileEntity.getField(t + 3) > 0 ? "yes" : "no"), 137, 64 + t * 15, 4034732);
    		++t;
    	}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(texture);
        drawTexturedModalRect(mh, mv, 0, 0, xSize, ySize);
        int energy = energyLevel(38);
        drawTexturedModalRect(mh + 11, mv + 49 - energy, 176, 31, 18, energy != 0 ? energy + 1 : 0);
	}
	
	@Override
	public void setWorldAndResolution(Minecraft minecraft, int width, int height){
		super.setWorldAndResolution(minecraft, width, height);
		mh = (width - xSize) / 2;
		mv = (height - ySize) / 2;
	}
	
	@Override
	public void mouseClicked(int x, int y, int button) throws IOException{
		super.mouseClicked(x, y, button);
		BlockPos pos = tileEntity.getPos();
		if(x >= mh + 135 && x <= mh + 155 && y >= mv + 60 && y <= mv + 146){
			int i = y - 60 - mv;
			i /= 15;
			int c = tileEntity.getField(3 + i);
			HyperCombat.network.sendToServer(new GuiMessage(pos.getX(), pos.getY(), pos.getZ(), 3 + i, c > 0 ? 0 : 1, tileEntity.getWorld().provider.getDimensionId()));
		}
	}
	
	private int energyLevel(int max) {
    	double energy = tileEntity.energy();
    	int maxEnergy = tileEntity.maxEnergy();
		return (int) ((energy / maxEnergy) * max);
	}
}
