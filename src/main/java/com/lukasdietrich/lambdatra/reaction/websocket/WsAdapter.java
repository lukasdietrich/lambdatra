package com.lukasdietrich.lambdatra.reaction.websocket;

import io.netty.buffer.ByteBufUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.HttpHeaders.Names;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.function.BiConsumer;

import com.lukasdietrich.lambdatra.NettyHandler;
import com.lukasdietrich.lambdatra.reaction.Adapter;
import com.lukasdietrich.lambdatra.reaction.CallbackAdapter;
import com.lukasdietrich.lambdatra.reaction.http.WrappedRequest;
import com.lukasdietrich.lambdatra.session.SessionStore;

/**
 * {@link Adapter} implementation for WebSockets
 * 
 * @author Lukas Dietrich
 *
 */
public class WsAdapter<S> extends CallbackAdapter<WsCallback<S>> {
	
	private String pattern;
	private SessionStore<S> sessions;
	
	public WsAdapter(String pattern, WsCallback<S> callback, SessionStore<S> sessions) {
		super(callback);
		this.pattern = pattern;
		this.sessions = sessions;
	}

	@Override
	public boolean call(NettyHandler handler, ChannelHandlerContext ctx, FullHttpRequest req, Map<String, String> params) throws IOException {
		WebSocketServerHandshaker handshaker = new WebSocketServerHandshakerFactory(
													String.format("ws://%s%s", req.headers().get(Names.HOST), pattern), 
													null, 
													true
												).newHandshaker(req);
		
		if (handshaker == null) {
			WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
		} else {
			WebSocket ws = getCallback().newInstance(new WrappedRequest<>(req, params, sessions));
			
			if (ws instanceof WebSocket) {
				Channel ch = handshaker.handshake(ctx.channel(), req).channel();
				handler.onWsFrame(new WsBridge(handshaker, ch, ws));
				ws.onOpen();
			} else {
				FullHttpResponse forbidden = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN);
				ByteBufUtil.writeUtf8(forbidden.content(), "Forbidden.");
				
				ctx.writeAndFlush(forbidden);
				ctx.close();
			}
		}
		
		return true;
	}
	
	private class WsBridge implements BiConsumer<ChannelHandlerContext, WebSocketFrame> {
		
		private WebSocketServerHandshaker handshaker;
		private WebSocket endpoint;
		
		private WsBridge(WebSocketServerHandshaker handshaker, Channel ch, WebSocket endpoint) {
			this.handshaker = handshaker;
			this.endpoint = endpoint;
			
			endpoint.setChannel(ch);
		}

		@Override
		public void accept(ChannelHandlerContext ctx, WebSocketFrame frame) {
			if (frame instanceof CloseWebSocketFrame) {
				handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
				endpoint.releaseReferences();
				endpoint.onClose();
				return;
			}
			
			if (frame instanceof PingWebSocketFrame) {
				ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
				return;
			}
			
			if (frame instanceof TextWebSocketFrame) {
				endpoint.onMessage(((TextWebSocketFrame) frame).text());
				return;
			}
			
			throw new UnsupportedOperationException(String.format("Unsupported websocket frame of type %s", frame.getClass().getName()));
		}
		
	}

}
