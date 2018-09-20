package knokko.entities;

import knokko.main.HyperCombat;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class EntityHandler {
	
	public static void registerEntity(Class entityClass, String name){
		int entityId = EntityRegistry.findGlobalUniqueEntityId();
		EntityRegistry.registerGlobalEntityID(entityClass, name, entityId);
		EntityRegistry.registerModEntity(entityClass, name, entityId, HyperCombat.instance, 64, 1, true);
	}
	
	public static void registerEntities(){
		registerEntity(EntityLaser.class, "Laser");
		registerEntity(EntityBattleShip.class, "BattleShip");
	}
}
