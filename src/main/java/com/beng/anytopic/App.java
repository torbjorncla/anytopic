package com.beng.anytopic;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.LoggerFactory;

import lombok.extern.slf4j.Slf4j;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

import com.beng.anytopic.a.MqttActor;
import com.beng.anytopic.c.Initializer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * AnyTopic 
 * @author torbjorncla
 *
 */
@Slf4j
public class App {
	
	private final static int PORT = 1884;
	public final static String MQTT_HOST = "tcp://m2m.eclipse.org:1883";
	public static final AttributeKey<String> CHANNEL_PATH = AttributeKey.valueOf("channelPath");
	
	//Move to Actor?
	private static final Map<String, ChannelGroup> clients = new ConcurrentHashMap<String, ChannelGroup>();
	
	
	public static ChannelGroup clientsCreateIfAbsent(final String topic) {
		if(clients.containsKey(topic)) {
			return clients.get(topic);
		}
		final ChannelGroup channelGroup = new DefaultChannelGroup(topic, GlobalEventExecutor.INSTANCE);
		clients.put(topic, channelGroup);
		return channelGroup;
	}
	
	public static ChannelGroup clients(final String topic) {
		if(clients.containsKey(topic)) {
			return clients.get(topic);
		}
		return null;
	}
	
	
	static {
		Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		rootLogger.setLevel(Level.INFO);
	}
	
	public void run(final ActorRef actor) throws InterruptedException {
		EventLoopGroup boss = new NioEventLoopGroup();
		EventLoopGroup worker = new NioEventLoopGroup();
		
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(boss, worker)
			.channel(NioServerSocketChannel.class)
			.childHandler(new Initializer(actor))
			.bind(PORT).sync().channel().closeFuture().sync();
		} finally {
			boss.shutdownGracefully();
			worker.shutdownGracefully();
		}
	}
	
	public static void main(String... x) throws Exception {
		log.info("----- Starting anyTopic -----");
		
		final ActorSystem system = ActorSystem.create("anyTopic");
		final ActorRef mqttActor = system.actorOf(Props.create(MqttActor.class), "mqttActor");
		
		new App().run(mqttActor);
	}
}
