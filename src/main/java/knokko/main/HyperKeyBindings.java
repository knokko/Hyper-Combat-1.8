package knokko.main;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class HyperKeyBindings {
	
	public static final KeyBinding z = new KeyBinding("key.z", Keyboard.KEY_Z, "key.categories.knokkohypercombat");
	
	public static void load(){
		ClientRegistry.registerKeyBinding(z);
	}

}
