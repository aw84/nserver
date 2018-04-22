package pl.training.poc.discard;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class DiscardServerHandler extends ChannelInboundHandlerAdapter {
	private StringBuilder sb = new StringBuilder(300);

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		ByteBuf in = (ByteBuf) msg;
		sb.delete(0, sb.length());
		while (in.isReadable()) {
			Byte b = in.readByte();
			sb.append(String.format("%02x ", b));
		}
		System.err.println(sb.toString());
		in.release();
		ctx.close();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
