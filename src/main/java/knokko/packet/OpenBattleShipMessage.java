package knokko.packet;

import knokko.main.HyperCombat;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class OpenBattleShipMessage implements IMessage {
	
	private String name;
	private int dimension;

	public OpenBattleShipMessage() {}
	
	public OpenBattleShipMessage(EntityPlayer player){
		name = player.getName();
		dimension = player.worldObj.provider.getDimensionId();
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		dimension = buf.readInt();
		name = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(dimension);
		ByteBufUtils.writeUTF8String(buf, name);
	}
	
	public static class Handler implements IMessageHandler<OpenBattleShipMessage, IMessage> {

		@Override
		public IMessage onMessage(final OpenBattleShipMessage message, MessageContext ctx) {
			IThreadListener listener = MinecraftServer.getServer().worldServerForDimension(message.dimension);
			listener.addScheduledTask(new Runnable(){

				@Override
				public void run() {
					WorldServer world = MinecraftServer.getServer().worldServerForDimension(message.dimension);
					EntityPlayer player = world.getPlayerEntityByName(message.name);
					if(player != null){
						player.openGui(HyperCombat.instance, 2, world, (int)player.posX, (int)player.posY, (int)player.posZ);
						System.out.println("opened gui on server side");
					}
				}
				
			});
			return null;
		}
		
	}
}
