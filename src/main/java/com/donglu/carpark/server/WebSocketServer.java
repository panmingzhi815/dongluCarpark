package com.donglu.carpark.server;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSocketServer extends org.java_websocket.server.WebSocketServer {
	private static Logger LOGGER=LoggerFactory.getLogger(WebSocketServer.class);
	private static final List<WebSocket> listConn=new ArrayList<>();
	
	public WebSocketServer(int port) {
		super(new InetSocketAddress(port));
		setConnectionLostTimeout(10);
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		LOGGER.info("客户端连接：{}",conn.getRemoteSocketAddress());
		listConn.add(conn);
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		LOGGER.info("连接：{}断开:{}",conn,reason);
		listConn.remove(conn);
	}

	@Override
	public void onMessage(WebSocket conn, String message) {

	}

	@Override
	public void onError(WebSocket conn, Exception ex) {

	}

	@Override
	public void onStart() {
		
	}
	
	public static void sendToAll(String msg) {
		for (WebSocket webSocket : listConn) {
			webSocket.send(msg);
			LOGGER.info("通知：{} 成功",webSocket.getRemoteSocketAddress());
		}
	}
		
}
