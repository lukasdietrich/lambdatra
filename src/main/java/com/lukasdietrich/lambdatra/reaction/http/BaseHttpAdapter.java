package com.lukasdietrich.lambdatra.reaction.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.io.IOException;
import java.util.Map;

import com.lukasdietrich.lambdatra.NettyHandler;
import com.lukasdietrich.lambdatra.reaction.Adapter;
import com.lukasdietrich.lambdatra.reaction.CallbackAdapter;
import com.lukasdietrich.lambdatra.session.SessionStore;

/**
 * Arbitrary {@link Adapter} for http request/response communication.
 * 
 * @author Lukas Dietrich
 *
 */
public abstract class BaseHttpAdapter<S, E> extends CallbackAdapter<E> {
	
	private SessionStore<S> sessions;
	
	public BaseHttpAdapter(E callback, SessionStore<S> sessions) {
		super(callback);
		
		this.sessions = sessions;
	}

	@Override
	public boolean call(NettyHandler handler, ChannelHandlerContext ctx, FullHttpRequest req, Map<String, String> params) throws IOException {
		FullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		WrappedRequest<S> wreq = new WrappedRequest<>(req, params, sessions);
		WrappedResponse<S> wres = new WrappedResponse<>(res, sessions);
		
		if (handle(wreq, wres)) {
			wres.applyHeader();
			ctx.channel().writeAndFlush(res);
			ctx.close();
			wres.close();
			
			return true;
		}
		
		return false;
	}
	
	protected abstract boolean handle(WrappedRequest<S> req, WrappedResponse<S> res) throws IOException;

}
