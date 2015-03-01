package com.lukasdietrich.lambdatra.reaction.websocket;

import java.io.IOException;

/**
 * Callback for {@link WebSocket} connections
 * 
 * @author Lukas Dietrich
 *
 */
@FunctionalInterface
public interface WSCallback<E extends WebSocket> {

	/**
	 * Called on established and handshaken WebSocket-connection
	 * 
	 * @param socket the {@link WebSocket} connected
	 * @throws IOException may throw an exception on write failure
	 */
	public void call(E socket) throws IOException;
	
}
