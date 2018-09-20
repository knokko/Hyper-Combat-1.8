package knokko.render;

import knokko.main.HyperCombat;
import knokko.model.ModelCable;
import knokko.tileentity.TileEntityCable;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class RenderCable extends TileEntitySpecialRenderer {
	
	private ModelCable model;

	public RenderCable() {
		model = new ModelCable();
	}

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float scale, int i) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
        ResourceLocation textures = (new ResourceLocation(HyperCombat.modid + ":textures/blocks/coppercable model.png")); 
        Minecraft.getMinecraft().renderEngine.bindTexture(textures);                     
        GL11.glPushMatrix();
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
        model.render((TileEntityCable) tileEntity, 0.0625F);
        GL11.glPopMatrix();
        GL11.glPopMatrix();
	}
	
	 private void adjustRotatePivotViaMeta(World world, int x, int y, int z) {
         GL11.glPushMatrix();
         GL11.glPopMatrix();
	 }

}
