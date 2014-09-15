package com.beng.anytopic.m;

public abstract class Event {
	private final String topic;
	public Event(final String topic) {
		this.topic = topic;
	}
	
	public String topic() {
		return this.topic;
	}
}
