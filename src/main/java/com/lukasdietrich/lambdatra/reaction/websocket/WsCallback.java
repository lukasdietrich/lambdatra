package com.lukasdietrich.lambdatra.reaction.websocket;

import io.netty.handler.codec.http.HttpResponseStatus;

import java.io.IOException;

import com.lukasdietrich.lambdatra.reaction.http.WrappedRequest;

/**
 * Callback for WebSocket requests
 * 
 * @author Lukas Dietrich
 *
 */
@FunctionalInterface
public interface WsCallback<S> {

	/**
	 * Called on incoming websocket request
	 * to create a new {@link WebSocket} instance.
	 * <br>
	 * If the returned {@link WebSocket} is null,
	 * a {@link HttpResponseStatus#FORBIDDEN} will 
	 * be sent.
	 * 
	 * @param req Request
	 * @return a newly created {@link WebSocket} instance
	 * @throws IOException may throw an exception on write failure
	 */
	public WebSocket newInstance(WrappedRequest<S> req);
	
}
