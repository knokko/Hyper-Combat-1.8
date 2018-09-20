package knokko.model;

import knokko.main.HyperCombat;
import knokko.tileentity.IEnergyStorage;
import knokko.tileentity.TileEntityCable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ModelCable extends ModelBase {

	public ModelCable(int width, int height){
		textureWidth = width;
        textureHeight = height;
	}

    public ModelCable(){
        this(46, 23);
    }
    
    public void render(TileEntityCable entity, float f){
    	World w = entity.getWorld();
    	new ModelRenderer(this, 0, 0).addBox(-3, 13, -3, 6, 6, 6).render(f);
    	if(entity.connect(w.getTileEntity(entity.getPos().down())))
    		new ModelRenderer(this, 0, 12).addBox(-3, 19, -3, 6, 5, 6).render(f);
    	if(entity.connect(w.getTileEntity(entity.getPos().up())))
    		new ModelRenderer(this, 0, 12).addBox(-3, 8, -3, 6, 5, 6).render(f);
    	if(entity.connect(w.getTileEntity(entity.getPos().north())))
    		new ModelRenderer(this, 24, 12).addBox(-3, 13, -8, 6, 6, 5).render(f);
    	if(entity.connect(w.getTileEntity(entity.getPos().south())))
    		new ModelRenderer(this, 24, 12).addBox(-3, 13, 3, 6, 6, 5).render(f);
    	if(entity.connect(w.getTileEntity(entity.getPos().west())))
    		new ModelRenderer(this, 24, 0).addBox(3, 13, -3, 5, 6, 6).render(f);
    	if(entity.connect(w.getTileEntity(entity.getPos().east())))
    		new ModelRenderer(this, 24, 0).addBox(-8, 13, -3, 5, 6, 6).render(f);
	}
}
