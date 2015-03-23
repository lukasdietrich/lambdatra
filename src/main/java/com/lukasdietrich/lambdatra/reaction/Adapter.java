package com.lukasdietrich.lambdatra.reaction;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

import java.io.IOException;
import java.util.Map;

import com.lukasdietrich.lambdatra.NettyHandler;
import com.lukasdietrich.lambdatra.routing.Route;

/**
 * Adapter to handle matched {@link Route}
 * 
 * @author Lukas Dietrich
 *
 */
public abstract class Adapter {
	
	/**
	 * Process arbitrary request data.
	 * 
	 * @param handler {@link NettyHandler} middleware instance
	 * @param ctx {@link ChannelHandlerContext} to handle io with
	 * @param req a {@link FullHttpRequest} that represents an incoming request
	 * @param params mapped data of url parameters
	 * @return returns whether or not the request was fulfilled
	 * @throws IOException may throw an exception on write failure
	 */
	public abstract boolean call(NettyHandler handler, ChannelHandlerContext ctx, FullHttpRequest req, Map<String, String> params) throws IOException;
	
}
