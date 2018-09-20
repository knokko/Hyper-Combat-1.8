package knokko.items;

import knokko.main.HyperCombat;
import knokko.proxy.Proxy;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemHyperBoots extends ItemArmor implements ISpecialArmor{

	public ItemHyperBoots() {
		super(ArmorMaterial.DIAMOND, 1, 3);
		setUnlocalizedName("hyperboots");
	}

	@Override
	public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
		return new ArmorProperties(-1, 0, 0);
	}

	@Override
	public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
		return 4;
	}

	@Override
	public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {}
	
	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack){
		if(Proxy.isKeyDown("jump"))
			player.motionY += 0.15;
		player.jumpMovementFactor = 0.05F;
		if(Proxy.isKeyDown("sneak"))
			player.motionY = 0;
		player.fallDistance = 0;
	}
	
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type)
    {
        return HyperCombat.modid + ":textures/entities/laser.png";
    }
	
	@SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot)
    {
		ModelBiped model = new ModelBiped();
		model.bipedLeftLeg.addBox(-2, -2.5F, -5.5F, 10, 15, 10);
		model.bipedLeftLeg.addBox(-2, 2.5F, -15.5F, 10, 10, 10);
		model.bipedRightLeg.addBox(-8, -2.5F, -5.5F, 10, 15, 10);
		model.bipedRightLeg.addBox(-8, 2.5F, -15.5F, 10, 10, 10);
        return model;
    }
}
