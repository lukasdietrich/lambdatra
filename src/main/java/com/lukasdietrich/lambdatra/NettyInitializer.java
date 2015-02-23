package com.lukasdietrich.lambdatra;

import com.lukasdietrich.lambdatra.routing.Router;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * {@link ChannelInitializer} implementation for underlying
 * Netty Framework
 * 
 * @author Lukas Dietrich
 *
 */
class NettyInitializer extends ChannelInitializer<SocketChannel> {

	private final int MAX_BODY_SIZE = 65536;
	private Router router;
	
	protected NettyInitializer(Router router) {
		this.router = router;
	}
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ch.pipeline()
			.addLast(new HttpServerCodec())
			.addLast(new HttpObjectAggregator(MAX_BODY_SIZE))
			.addLast(new NettyHandler(router));
	}

}
