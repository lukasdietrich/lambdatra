package com.lukasdietrich.lambdatra.reaction;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

import java.io.IOException;
import java.util.Map;

import com.lukasdietrich.lambdatra.NettyHandler;

/**
 * Abstract container to hold a callback of type E
 * 
 * @author Lukas Dietrich
 *
 * @param <E> class of callback to be handled
 */
public abstract class CallbackAdapter<E> {
	
	private E callback;
	
	public CallbackAdapter(E callback) {
		this.callback = callback;
	}
	
	/**
	 * Returns the callback
	 * @return a callback
	 */
	protected E getCallback() {
		return this.callback;
	}

	/**
	 * Process arbitrary request data to call the underlying callback.
	 * 
	 * @param handler {@link NettyHandler} middleware instance
	 * @param ctx {@link ChannelHandlerContext} to handle io with
	 * @param req a {@link FullHttpRequest} that represents an incoming request
	 * @param params mapped data of url parameters
	 * @throws IOException may throw an exception on write failure
	 */
	public abstract void call(NettyHandler handler, ChannelHandlerContext ctx, FullHttpRequest req, Map<String, String> params) throws IOException;
	
}
