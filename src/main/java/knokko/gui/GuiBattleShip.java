package knokko.gui;

import knokko.container.ContainerBattleShip;
import knokko.entities.EntityBattleShip;
import knokko.main.HyperCombat;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

public class GuiBattleShip extends GuiContainer {
	
	private static final ResourceLocation textureBattleShip = new ResourceLocation(HyperCombat.modid + ":textures/gui/battle ship.png");
	private static final ResourceLocation textureEnergy = new ResourceLocation(HyperCombat.modid + ":textures/gui/battle ship energy.png");
	private final InventoryPlayer inventoryPlayer;
	private final EntityBattleShip ship;

	public GuiBattleShip(InventoryPlayer player, EntityBattleShip battleShip) {
		super(new ContainerBattleShip(player, battleShip));
		inventoryPlayer = player;
		ship = battleShip;
		xSize = 256;
		ySize = 200;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(textureBattleShip);
        int marginHorizontal = (width - xSize) / 2;
        int marginVertical = (height - ySize) / 2;
        drawTexturedModalRect(marginHorizontal, marginVertical, 0, 0, xSize, ySize);
        mc.getTextureManager().bindTexture(textureEnergy);
        int energyLevel = energyLevel(169);
        drawTexturedModalRect(marginHorizontal + 231, marginVertical + 180 - energyLevel, 0, 169, 18, energyLevel != 0 ? energyLevel + 1 : 0);
	}
	
	private int energyLevel(int max) {
    	double energy = ship.getField(0);
    	int maxEnergy = ship.maxEnergy();
		return (int) ((energy / maxEnergy) * max);
	}
}
