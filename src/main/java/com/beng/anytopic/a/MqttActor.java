package com.beng.anytopic.a;

import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.beng.anytopic.App;
import com.beng.anytopic.m.PublishEvent;
import com.beng.anytopic.m.SubscribeEvent;

import akka.actor.UntypedActor;

@Slf4j
public class MqttActor extends UntypedActor implements MqttCallback {
	
	private final static String clientId = UUID.randomUUID().toString().substring(0, 22);
	private MqttClient mqttClient;
	
	@Override
	public void preStart() throws Exception {
		super.preStart();
		this.mqttClient = new MqttClient(App.MQTT_HOST, clientId);
		mqttClient.connect();
		mqttClient.setCallback(this);
	}



	@Override
	public void onReceive(Object event) throws Exception {
		if(event instanceof PublishEvent) {
			handle((PublishEvent) event);
		} else if(event instanceof SubscribeEvent) {
			handle((SubscribeEvent) event);
		}
	}
	
	protected void handle(final PublishEvent event) throws MqttException {
		mqttClient.publish(event.topic(), createMqttMessage(event.message()));
		log.info(String.format("Published: %s to: %s", event.message(), event.topic()));
	}
	
	protected void handle(final SubscribeEvent event) throws MqttException {
		mqttClient.subscribe(event.topic());
		App.clientsCreateIfAbsent(event.topic()).add(event.channel());
		log.info(String.format("Subscribed to: %s", event.topic()));
	}


	public void connectionLost(Throwable cause) {
		log.error("Connection Lost", cause);
	}


	public void messageArrived(String topic, MqttMessage message) throws Exception {
		final ChannelGroup channelGroup = App.clients(topic);
		if(channelGroup != null) {
			channelGroup.writeAndFlush(new TextWebSocketFrame(new String(message.getPayload())));
			log.info(String.format("Wrote message to ChannelGroup: %s", topic));
		}
	}

	public void deliveryComplete(IMqttDeliveryToken token) {
		log.info("Delivery Complete: " + token.getMessageId());
	}
	
	protected MqttMessage createMqttMessage(final String msg) {
		final MqttMessage mqttMessage = new MqttMessage();
		mqttMessage.setPayload(msg.getBytes());
		mqttMessage.setQos(0);
		mqttMessage.setRetained(false);
		return mqttMessage;
	}

}
