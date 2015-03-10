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
import com.lukasdietrich.lambdatra.reaction.CallbackAdapter;
import com.lukasdietrich.lambdatra.session.SessionStore;

/**
 * {@link CallbackAdapter} implementation for {@link HttpCallback}
 * 
 * @author Lukas Dietrich
 *
 */
public class HttpAdapter<S> extends CallbackAdapter<HttpCallback<S>> {
	
	private SessionStore<S> sessions;
	
	public HttpAdapter(HttpCallback<S> callback, SessionStore<S> sessions) {
		super(callback);
		
		this.sessions = sessions;
	}

	@Override
	public void call(NettyHandler handler, ChannelHandlerContext ctx, FullHttpRequest req, Map<String, String> params) throws IOException {
		FullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		WrappedRequest<S> wreq = new WrappedRequest<>(req, params, sessions);
		WrappedResponse<S> wres = new WrappedResponse<>(res, sessions);
		
		getCallback().call(wreq, wres);
		wres.applyHeader();
		ctx.channel().writeAndFlush(res);
		ctx.close();
	}

}
