package com.lukasdietrich.lambdatra.reaction.websocket;

import java.util.UUID;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * Abstract {@link WebSocket}
 * 
 * @author Lukas Dietrich
 *
 */
public abstract class WebSocket {
	
	private final long ID = Math.abs(UUID.randomUUID().getMostSignificantBits());
	private Channel ch;
	
	public abstract void onMessage(String msg);
	public abstract void onOpen();
	public abstract void onClose();
	
	/**
	 * Send a message to the connected endpoint.
	 * 
	 * @param msg message to be sent
	 */
	public final void sendMessage(String msg) {
		this.ch.writeAndFlush(new TextWebSocketFrame(msg));
	}
	
	/**
	 * Sets the {@link Channel} this {@link WebSocket} 
	 * write on.
	 * 
	 * @param ch {@link Channel} to handle io
	 */
	protected final void setChannel(Channel ch) {
		this.ch = ch;
	}
	
	/**
	 * Releases references to the used {@link Channel}.
	 */
	protected final void releaseReferences() {
		this.ch = null;
	}
	
	/**
	 * Returns the {@link WebSocket}s generated ID.
	 * 
	 * @return id as a {@link Long}
	 */
	public final long getId() {
		return ID;
	}
	
	/**
	 * Returns the {@link WebSocket}s generated ID
	 * as a hexadecimal {@link String}
	 * <br>
	 * <br>
	 * <i>{@link Long#toString}({@link #getId()}, 16)</i>.
	 * 
	 * @return id as a {@link String}
	 */
	public final String getIdHex() {
		return Long.toString(ID, 16);
	}
	
}
