package knokko.gui;

import java.io.IOException;

import knokko.container.ContainerEnergyStorage;
import knokko.container.ContainerGenerator;
import knokko.main.HyperCombat;
import knokko.packet.GuiMessage;
import knokko.tileentity.TileEntityEnergyStorage;
import knokko.tileentity.TileEntityGenerator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class GuiGenerator extends GuiContainer {

private static final ResourceLocation texture = new ResourceLocation(HyperCombat.modid + ":textures/gui/generator.png");
	
	private final TileEntityGenerator generator;
	
	private int mh;
    private int mv;

	public GuiGenerator(InventoryPlayer player, TileEntityGenerator tileEntity) {
		super(new ContainerGenerator(player, tileEntity));
		generator = tileEntity;
		ySize = 236;
		mh = (width - xSize) / 2;
		mv = (height - ySize) / 2;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(texture);
        drawTexturedModalRect(mh, mv, 0, 0, xSize, ySize);
        int energy = energyLevel(38);
        int burn = burnLevel(14);
        drawTexturedModalRect(mh + 11, mv + 49 - energy, 176, 31, 18, energy != 0 ? energy + 1 : 0);
        drawTexturedModalRect(mh + 40, mv + 51 - burn, 176, 13 - burn, 14, burn != 0 ? burn + 1 : burn);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y){
		int t = 0;
    	while(t < 6){
    		fontRendererObj.drawString("Give energy to " + EnumFacing.values()[t].name().toLowerCase() + "?", 12, 64 + t * 15, 4034732);
    		fontRendererObj.drawString((generator.giveEnergyToSide[t] ? "yes" : "no"), 137, 64 + t * 15, 4034732);
    		++t;
    	}
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
		BlockPos pos = generator.getPos();
		if(x >= mh + 135 && x <= mh + 155 && y >= mv + 60 && y <= mv + 146){
			int i = y - 60 - mv;
			i /= 15;
			boolean c = generator.giveEnergyToSide[i];
			HyperCombat.network.sendToServer(new GuiMessage(pos.getX(), pos.getY(), pos.getZ(), 3 + i, c ? 0 : 1, generator.getWorld().provider.getDimensionId()));
		}
	}
	
	private int energyLevel(int max) {
    	double energy = generator.energy;
    	int maxEnergy = generator.maxEnergy();
		return (int) ((energy / maxEnergy) * max);
	}
	
	private int burnLevel(int max){
		double progress = generator.burnProgress;
		int end = generator.ticksPerItem;
		return (int) ((progress / end) * max);
	}
}
