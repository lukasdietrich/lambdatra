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

/**
 * {@link CallbackAdapter} implementation for {@link HttpCallback}
 * 
 * @author Lukas Dietrich
 *
 */
public class HttpAdapter extends CallbackAdapter<HttpCallback> {
	
	public HttpAdapter(HttpCallback callback) {
		super(callback);
	}

	@Override
	public void call(NettyHandler handler, ChannelHandlerContext ctx, FullHttpRequest req, Map<String, String> params) throws IOException {
		FullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		getCallback().call(new WrappedRequest(req, params), new WrappedResponse(res));
		ctx.channel().writeAndFlush(res);
		ctx.close();
	}

}
