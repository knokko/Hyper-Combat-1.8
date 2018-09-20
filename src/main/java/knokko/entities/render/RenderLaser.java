package knokko.entities.render;

import knokko.entities.EntityBlastLaser;
import knokko.main.HyperCombat;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.util.ResourceLocation;

public class RenderLaser extends Render {
	
	public ModelLaser model = new ModelLaser();
	
	public RenderLaser(RenderManager manager){
        super(manager);
    }

    public void render(Entity entity, double x, double y, double z, float f, float ticks){
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        GlStateManager.translate((float)x, (float)y, (float)z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(-1.0F, -1.0F, 1.0F);
        GlStateManager.enableAlpha();
        bindEntityTexture(entity);
        model.render(entity.rotationYaw, entity.rotationPitch);
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, f, ticks);
    }

    public ResourceLocation getEntityTexture(Entity entity){
    	if(entity instanceof EntityBlastLaser)
    		return new ResourceLocation(HyperCombat.modid + ":textures/entities/red laser.png");
        return new ResourceLocation(HyperCombat.modid + ":textures/entities/laser.png");
    }

    public void doRender(Entity entity, double x, double y, double z, float f, float partialTicks){
        render(entity, x, y, z, f, partialTicks);
    }
    
    public static class ModelLaser extends ModelBase {
    	
    	public ModelRenderer model;
    	
    	public ModelLaser() {
    		model = new ModelRenderer(this);
    		model.addBox(-2.5F, -2.5F, -10, 5, 5, 20);
    	}
    	
    	public void render(float rotationYaw, float rotationPitch){
    		model.rotateAngleX = (float) -Math.toRadians(rotationPitch);
    		model.rotateAngleY = (float) Math.toRadians(rotationYaw);
    		model.render(0.05F);
    	}
    }
}
