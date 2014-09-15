package com.beng.anytopic.m;

import io.netty.channel.Channel;

public class SubscribeEvent extends Event {
	private final Channel channel;
	public SubscribeEvent(final String topic, final Channel channel) {
		super(topic);
		this.channel = channel;
	}
	
	public Channel channel() {
		return this.channel;
	}
}
