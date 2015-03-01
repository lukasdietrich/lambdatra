package com.lukasdietrich.lambdatra;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.function.Consumer;

import com.lukasdietrich.lambdatra.reaction.CallbackAdapter;
import com.lukasdietrich.lambdatra.reaction.http.HttpAdapter;
import com.lukasdietrich.lambdatra.reaction.http.HttpCallback;
import com.lukasdietrich.lambdatra.reaction.http.StaticCallback;
import com.lukasdietrich.lambdatra.reaction.http.WrappedRequest;
import com.lukasdietrich.lambdatra.reaction.websocket.WSCallback;
import com.lukasdietrich.lambdatra.reaction.websocket.WebSocket;
import com.lukasdietrich.lambdatra.reaction.websocket.WsAdapter;
import com.lukasdietrich.lambdatra.routing.Route;
import com.lukasdietrich.lambdatra.routing.Router;

/**
 * Main class for Lambdatra server framework.
 * 
 * @author Lukas Dietrich
 *
 */
public class Lambdatra {
	
	/**
	 * Creates a server instance on a given port and calls
	 * the second parameter with the created server.
	 * 
	 * Eg.:
	 * {@link Lambdatra}.{@link #create(int, Consumer)}
	 * 
	 * @param port
	 * @param context
	 * @throws InterruptedException
	 */
	public static void create(int port, Consumer<Lambdatra> context) {
		Lambdatra instance = new Lambdatra();
		context.accept(instance);
		
		try {
			instance.serv
				.bind(port)
				.sync()
				.channel()
				.closeFuture()
				.sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			instance.bossGroup.shutdownGracefully();
			instance.workerGroup.shutdownGracefully();
		}
	}

	private ServerBootstrap serv;
	private Router router;
	
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	
	private Lambdatra() {
		this.router = new Router();
		
		this.bossGroup = new NioEventLoopGroup(1);
		this.workerGroup = new NioEventLoopGroup();
		
		this.serv = new ServerBootstrap();
		
		serv.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class)
			.childHandler(new NettyInitializer(router));
	}
	
	/**
	 * Shorthand for {@link #onWebSocket(String, Class, WSCallback)}
	 * with empty callback.
	 * 
	 * @param pattern url pattern to bind this handler to
	 * @param sockClass {@link WebSocket} implementation class
	 * @return {@link Lambdatra} for chaining
	 */
	public Lambdatra onWebSocket(String pattern, Class<? extends WebSocket> sockClass) {
		return onWebSocket(pattern, sockClass, s -> {});
	}
	
	/**
	 * Binds a given path to handle incoming Websockets.
	 * For each successful request a new instance of 
	 * the given class is created to represent the network
	 * entity.
	 * 
	 * @param pattern url pattern to bind this handler to
	 * @param sockClass {@link WebSocket} implementation class
	 * @param cb {@link WSCallback}, that is called on incoming connections
	 * @return {@link Lambdatra} for chaining
	 */
	public <E extends WebSocket> Lambdatra onWebSocket(String pattern, Class<E> sockClass, WSCallback<E> cb) {
		this.router.addRoute(new Route<CallbackAdapter<?>>(pattern, new WsAdapter<E>(pattern, cb, sockClass)));
		return this;
	}
	
	/**
	 * Binds a given {@link HttpCallback} to a path.
	 * That path can contain parameters and may end with an,
	 * if subpaths are allowed.
	 * <br><br>
	 * Pattern examples.:
	 * <ul>
	 * 	<li><code>/foo/:bar</code> - bar is a parameter</li>
	 *  <li><code>/foo/bar/*</code> - allows subpaths</li>
	 * </ul>
	 * {@link StaticCallback} is an implementation of {@link HttpCallback}
	 * for simple file servers.
	 * 
	 * @param pattern url pattern to bind this handler to
	 * @param cb {@link HttpCallback}, that has to respond to a {@link WrappedRequest}
	 * @return {@link Lambdatra} for chaining
	 */
	public Lambdatra on(String pattern, HttpCallback cb) {
		this.router.addRoute(new Route<>(pattern, new HttpAdapter(cb)));
		return this;
	}
	
}
