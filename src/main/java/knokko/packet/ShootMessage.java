package knokko.packet;

import knokko.entities.EntityBattleShip;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ShootMessage implements IMessage{
	
	private String name;
	private int dimension;
	private boolean left;

	public ShootMessage() {}
	
	public ShootMessage(EntityPlayer player, boolean isLeftGun){
		name = player.getDisplayNameString();
		dimension = player.worldObj.provider.getDimensionId();
		left = isLeftGun;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(left);
		buf.writeInt(dimension);
		ByteBufUtils.writeUTF8String(buf, name);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		left = buf.readBoolean();
		dimension = buf.readInt();
		name = ByteBufUtils.readUTF8String(buf);
	}
	
	public static class Handler implements IMessageHandler<ShootMessage, IMessage> {

		@Override
		public IMessage onMessage(final ShootMessage message, MessageContext ctx) {
			MinecraftServer.getServer().addScheduledTask(new Runnable(){

				@Override
				public void run() {
					WorldServer world = MinecraftServer.getServer().worldServerForDimension(message.dimension);
					EntityPlayer player = world.getPlayerEntityByName(message.name);
					if(player != null && player.ridingEntity instanceof EntityBattleShip)
						((EntityBattleShip) player.ridingEntity).shoot(message.left);
				}});
			
			return null;
		}
	}

}
