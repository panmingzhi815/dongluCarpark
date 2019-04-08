package com.donglu.carpark.ui.servlet;

import java.net.URI;

import org.java_websocket.handshake.ServerHandshake;

public class WebSocketClient extends org.java_websocket.client.WebSocketClient {

	public WebSocketClient(String serverUri) throws Exception {
		super(new URI(serverUri));
		setConnectionLostTimeout(10);
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {

	}

	@Override
	public void onMessage(String message) {

	}

	@Override
	public void onClose(int code, String reason, boolean remote) {

	}

	@Override
	public void onError(Exception ex) {

	}

}
