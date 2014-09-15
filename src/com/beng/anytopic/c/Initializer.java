package com.beng.anytopic.c;

import akka.actor.ActorRef;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

public class Initializer extends ChannelInitializer<SocketChannel>{

	private final ActorRef actor;
	
	public Initializer(final ActorRef actor) {
		this.actor = actor;
	}
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ch.pipeline().addLast(new HttpResponseEncoder());
		ch.pipeline().addLast(new HttpRequestDecoder());
		ch.pipeline().addLast(new PathSnooper(65536));
		ch.pipeline().addLast(new WebSocketServerProtocolHandler("/"));
		ch.pipeline().addLast(new SocketMessageHandler(actor));
	}

}
