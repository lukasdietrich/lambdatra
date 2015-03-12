package com.lukasdietrich.lambdatra;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.function.BiConsumer;

import com.lukasdietrich.lambdatra.routing.Router;

/**
 * Request handler middleware for underlying
 * Netty Framework
 * 
 * @author Lukas Dietrich
 *
 */
public class NettyHandler extends SimpleChannelInboundHandler<Object> {
	
	private Router router;
	private BiConsumer<ChannelHandlerContext, WebSocketFrame> wshandler;
	
	public NettyHandler(Router router) {
		this.router = router;
	}
	
	public void onWsFrame(BiConsumer<ChannelHandlerContext, WebSocketFrame> listener) {
		this.wshandler = listener;
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof FullHttpRequest) {
			handleHTTP(ctx, (FullHttpRequest) msg);
			return;
		}
		
		if (msg instanceof WebSocketFrame) {
			handleWebSocket(ctx, (WebSocketFrame) msg);
			return;
		}
	}
	
	private void handleHTTP(ChannelHandlerContext ctx, FullHttpRequest req) {
		router.findRoute(req.getUri()).ifPresent(match -> {
			try {
				match.getKey().getAdapter().call(this, ctx, req, match.getValue());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
	
	private void handleWebSocket(ChannelHandlerContext ctx, WebSocketFrame frame) {
		if (wshandler != null) 
			wshandler.accept(ctx, frame);
	}

}
