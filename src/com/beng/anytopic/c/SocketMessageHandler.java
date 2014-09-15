package com.beng.anytopic.c;

import akka.actor.ActorRef;

import com.beng.anytopic.App;
import com.beng.anytopic.m.PublishEvent;
import com.beng.anytopic.m.SubscribeEvent;

import lombok.extern.slf4j.Slf4j;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler.ServerHandshakeStateEvent;

/**
 * No need for more fancy stuff, just wanna read the endpoint and payload
 * @author torbjorncla
 *
 */
@Slf4j
public class SocketMessageHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

	private final ActorRef actor;
	
	public SocketMessageHandler(final ActorRef actor) {
		this.actor = actor;
	}
	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		super.userEventTriggered(ctx, evt);
		if(evt instanceof ServerHandshakeStateEvent) {
			actor.tell(new SubscribeEvent(getTopic(ctx), ctx.channel()), ActorRef.noSender());
		}
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
		actor.tell(new PublishEvent(getTopic(ctx), msg.text()), ActorRef.noSender());
	}
	
	protected String getTopic(final ChannelHandlerContext ctx) {
		return ctx.channel().attr(App.CHANNEL_PATH).toString();
	}

}
