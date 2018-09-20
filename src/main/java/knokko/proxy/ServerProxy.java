package knokko.proxy;

import net.minecraftforge.fml.relauncher.Side;

public class ServerProxy extends Proxy {

	@Override
	public Side getSide() {
		return Side.SERVER;
	}

}
