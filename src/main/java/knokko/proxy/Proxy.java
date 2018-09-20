package knokko.proxy;

import knokko.main.HyperCombat;
import net.minecraftforge.fml.relauncher.Side;

public abstract class Proxy {
	
	public abstract Side getSide();
	
	public void load(){}
	
	boolean iskeyDown(String key){
		return false;
	}
	
	public static boolean isKeyDown(String key){
		return HyperCombat.proxy.iskeyDown(key);
	}
}
