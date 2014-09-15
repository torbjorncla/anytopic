package com.beng.anytopic.m;

public class PublishEvent extends Event {
	private final String message;
	
	public PublishEvent(final String topic, final String message) {
		super(topic);
		this.message = message;
	}
	
	public String message() {
		return this.message;
	}
}
