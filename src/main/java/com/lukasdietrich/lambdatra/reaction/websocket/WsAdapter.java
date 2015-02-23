package com.lukasdietrich.lambdatra.reaction.websocket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders.Names;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;

import java.io.IOException;
import java.io.InvalidClassException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.function.BiConsumer;

import com.lukasdietrich.lambdatra.NettyHandler;
import com.lukasdietrich.lambdatra.reaction.CallbackAdapter;

/**
 * {@link CallbackAdapter} implementation for {@link WSCallback}
 * 
 * @author Lukas Dietrich
 *
 * @param <E>
 */
public class WsAdapter<E extends WebSocket> extends CallbackAdapter<WSCallback<E>> {
	
	private String pattern;
	private Class<E> sockClass;
	
	public WsAdapter(String pattern, WSCallback<E> callback, Class<E> sockClass) {
		super(callback);
		
		this.pattern = pattern;
		this.sockClass = sockClass;
	}

	@Override
	public void call(NettyHandler handler, ChannelHandlerContext ctx, FullHttpRequest req, Map<String, String> params) throws IOException {
		WebSocketServerHandshaker handshaker = new WebSocketServerHandshakerFactory(
													String.format("ws://%s%s", req.headers().get(Names.HOST), pattern), 
													null, 
													true
												).newHandshaker(req);
		
		if (handshaker == null) {
			WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
		} else {
			Channel ch = handshaker.handshake(ctx.channel(), req).channel();
			
			try {
				E ws = sockClass.getConstructor().newInstance();
				handler.onWsFrame(new WsBridge(handshaker, ch, ws));
				getCallback().call(ws);
				ws.onOpen();
			} catch (InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				throw new InvalidClassException(String.format(
							"Invalid class '%s' does not implement a standard constructor.",
							sockClass.getName()
						));	
			}
		}
		
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