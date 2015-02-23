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
 * @param <E>
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
	 * @param handler
	 * @param ctx
	 * @param req
	 * @param params
	 * @throws IOException
	 */
	public abstract void call(NettyHandler handler, ChannelHandlerContext ctx, FullHttpRequest req, Map<String, String> params) throws IOException;
	
}
