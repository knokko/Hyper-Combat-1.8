package knokko.packet;

import knokko.tileentity.TileEntityIngotEnchanter;
import io.netty.buffer.ByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class GuiMessage implements IMessage {
	
	private int x, y, z;
	private int value;
	private int dimension;
	private byte field;

	public GuiMessage() {}
	
	public GuiMessage(int x, int y, int z, int field, int value, int dimension){
		this.x = x;
		this.y = y;
		this.z = z;
		this.field = (byte) field;
		this.value = value;
		this.dimension = dimension;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		value = buf.readInt();
		dimension = buf.readInt();
		field = buf.readByte();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(value);
		buf.writeInt(dimension);
		buf.writeByte(field);
	}
	
	public static class Handler implements IMessageHandler<GuiMessage, IMessage> {

		@Override
		public IMessage onMessage(final GuiMessage message, MessageContext ctx) {
			IThreadListener listener = MinecraftServer.getServer().worldServerForDimension(message.dimension);
			listener.addScheduledTask(new Runnable(){

				@Override
				public void run() {
					TileEntity entity = MinecraftServer.getServer().worldServerForDimension(message.dimension).getTileEntity(new BlockPos(message.x, message.y, message.z));
					try {
						entity.getClass().getMethod("setField", int.class, int.class).invoke(entity, message.field, message.value);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				
			});
			return null;
		}
		
	}

}
