package knokko.entities.render;

import knokko.main.HyperCombat;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderBattleShip extends Render {
	
	public Model model;

	public RenderBattleShip(RenderManager manager) {
		super(manager);
		model = new Model();
	}

	@Override
	public ResourceLocation getEntityTexture(Entity entity) {
		return new ResourceLocation(HyperCombat.modid + ":textures/entities/battleship.png");
	}
	
	public void doRender(Entity entity, double x, double y, double z, float f, float ticks){
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        GlStateManager.translate((float)x, (float)y, (float)z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(-1.0F, -1.0F, 1.0F);
        GlStateManager.enableAlpha();
        bindEntityTexture(entity);
        if(entity.riddenByEntity != null)
        	model.render(entity.riddenByEntity.rotationYaw, entity.riddenByEntity.rotationPitch);
        else
        	model.render(entity.rotationYaw, entity.rotationPitch);
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, f, ticks);
    }
	
	public static class Model extends ModelBase {
    	
    	public ModelRenderer floorBase;
    	public ModelRenderer topBase;
    	public ModelRenderer wallBaseLeft;
    	public ModelRenderer wallBaseRight;
    	public ModelRenderer wallBaseFront;
    	public ModelRenderer wallBaseBack;
    	public ModelRenderer wingBaseLeft;
    	public ModelRenderer wingBaseRight;
    	public ModelRenderer wingCannonLeft;
    	public ModelRenderer wingCannonRight;
    	
    	public Model() {
    		int y = 20;
    		float z = -12.5f;
    		textureWidth = 996;
    		textureHeight = 85;
    		floorBase = new ModelRenderer(this, 0, 0).addBox(-10, -10 + y, -20 + z, 20, 2, 65);
    		topBase = new ModelRenderer(this, 170, 0).addBox(-10, -32 + y, -20 + z, 20, 2, 65);
    		wallBaseLeft = new ModelRenderer(this, 340, 0).addBox(-10, -30 + y, -20 + z, 2, 20, 65);
    		wallBaseRight = new ModelRenderer(this, 474, 0).addBox(8, -30 + y, -20 + z, 2, 20, 65);
    		wallBaseFront = new ModelRenderer(this, 608, 0).addBox(-10, -30 + y, 45 + z, 20, 22, 2);
    		wallBaseBack = new ModelRenderer(this, 652, 0).addBox(-10, -30 + y, -22 + z, 20, 22, 2);
    		wingBaseLeft = new ModelRenderer(this, 696, 0).addBox(-25, -25 + y, -10 + z, 15, 10, 30);
    		wingBaseRight = new ModelRenderer(this, 786, 0).addBox(10, -25 + y, -10 + z, 15, 10, 30);
    		wingCannonLeft = new ModelRenderer(this, 876, 0).addBox(-20, -25 + y, 20 + z, 10, 10, 20);
    		wingCannonRight = new ModelRenderer(this, 936, 0).addBox(10, -25 + y, 20 + z, 10, 10, 20);
    	}
    	
    	public void render(float rotationYaw, float rotationPitch){
    		float ax = (float) -Math.toRadians(rotationPitch);
    		float ay = (float) Math.toRadians(rotationYaw);
    		render(floorBase, ax, ay);
    		render(topBase, ax, ay);
    		render(wallBaseLeft, ax, ay);
    		render(wallBaseRight, ax, ay);
    		render(wallBaseFront, ax, ay);
    		render(wallBaseBack, ax, ay);
    		render(wingBaseLeft, ax, ay);
    		render(wingBaseRight, ax, ay);
    		render(wingCannonLeft, ax, ay);
    		render(wingCannonRight, ax, ay);
    	}
    	
    	private void render(ModelRenderer model, float ax, float ay){
    		model.rotateAngleX = ax;
    		model.rotateAngleY = ay;
    		model.render(0.2F);
    	}
    }
}
