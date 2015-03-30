package com.lukasdietrich.lambdatra;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.lang.reflect.Constructor;
import java.security.InvalidParameterException;
import java.util.Map;
import java.util.function.Consumer;

import com.lukasdietrich.lambdatra.reaction.http.HttpAdapter;
import com.lukasdietrich.lambdatra.reaction.http.HttpCallback;
import com.lukasdietrich.lambdatra.reaction.http.MiddlewareAdapter;
import com.lukasdietrich.lambdatra.reaction.http.MiddlewareCallback;
import com.lukasdietrich.lambdatra.reaction.http.WrappedRequest;
import com.lukasdietrich.lambdatra.reaction.websocket.WebSocket;
import com.lukasdietrich.lambdatra.reaction.websocket.WsAdapter;
import com.lukasdietrich.lambdatra.reaction.websocket.WsCallback;
import com.lukasdietrich.lambdatra.routing.Route;
import com.lukasdietrich.lambdatra.routing.Router;
import com.lukasdietrich.lambdatra.session.DefaultSessionStore;
import com.lukasdietrich.lambdatra.session.SessionStore;

/**
 * Main class for Lambdatra server framework.
 * 
 * @author Lukas Dietrich
 *
 * @param <S> class of session value
 */
public class Lambdatra<S> {
	
	/**
	 * Creates a server instance on a given port and calls
	 * the second parameter with the created server.
	 * 
	 * Eg.:
	 * {@link Lambdatra}.{@link #create(int, Consumer)}
	 * 
	 * @param port port to listen for connections
	 * @param sessions {@link SessionStore} to use
	 * @param context a {@link Consumer} callback, that exposes the {@link Lambdatra} instance
	 * @param <S> class of session value
	 */
	public static <S> void create(int port, SessionStore<S> sessions, Consumer<Lambdatra<S>> context) {
		Lambdatra<S> instance = new Lambdatra<>(sessions);
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
	
	/**
	 * Shorthand for {@link #create(int, SessionStore, Consumer)} using
	 * a {@link DefaultSessionStore}.
	 * 
	 * @param port port to listen for connections
	 * @param context a {@link Consumer} callback, that exposes the {@link Lambdatra} instance
	 */
	public static void create(int port, Consumer<Lambdatra<Map<String, String>>> context) {
		create(port, new DefaultSessionStore<Map<String, String>>("LAMBDATRASES", 1_800_000), context);
	}

	private ServerBootstrap serv;
	private Router router;
	private SessionStore<S> sessions;
	
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	
	private Lambdatra(SessionStore<S> sessions) {
		this.router = new Router();
		this.sessions = sessions;
		
		this.bossGroup = new NioEventLoopGroup(1);
		this.workerGroup = new NioEventLoopGroup();
		
		this.serv = new ServerBootstrap();
		
		serv.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class)
			.childHandler(new NettyInitializer(router));
	}

	/**
	 * Shorthand for {@link #onWebSocket(String, WsCallback)}
	 * that creates a new instance of the class.
	 * <br>
	 * <b>Warning:</b> The class has to implement a default constructor
	 * or an {@link InvalidParameterException} will be thrown !
	 * 
	 * @param pattern url pattern to bind this handler to
	 * @param sockClass {@link WebSocket} implementation class
	 * @return {@link Lambdatra} for chaining
	 */
	public Lambdatra<S> onWebSocket(String pattern, Class<? extends WebSocket> sockClass) {
		boolean hasDefaultConstructor = false;
		
		for (Constructor<?> c : sockClass.getConstructors())
			if (hasDefaultConstructor = c.getParameterCount() == 0)
				break;
		
		if (hasDefaultConstructor)
			return onWebSocket(pattern, r -> {
				try {
					return sockClass.newInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return null;
			});
		
		throw new InvalidParameterException("Socket class has to implement default constructor !");
	}
	
	/**
	 * Binds a given path to handle incoming Websockets.
	 * <br>
	 * For each successful request the {@link WsCallback} will
	 * be called to create a new {@link WebSocket} instance.
	 * 
	 * @param pattern url pattern to bind this handler to
	 * @param cb {@link WsCallback} for new {@link WebSocket} instances
	 * @return {@link Lambdatra} for chaining
	 */
	public Lambdatra<S> onWebSocket(String pattern, WsCallback<S> cb) {
		this.router.addRoute(new Route<>(pattern, new WsAdapter<>(pattern, cb, sessions)));
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
	 * 
	 * @param pattern url pattern to bind this handler to
	 * @param cb {@link HttpCallback}, that has to respond to a {@link WrappedRequest}
	 * @return {@link Lambdatra} for chaining
	 */
	public Lambdatra<S> on(String pattern, HttpCallback<S> cb) {
		this.router.addRoute(new Route<>(pattern, new HttpAdapter<>(cb, sessions)));
		return this;
	}
	
	/**
	 * Binds a given {@link MiddlewareCallback} to a path.
	 * It will work similar to {@link #on(String, HttpCallback)},
	 * but the {@link MiddlewareCallback} has to return, whether
	 * the request is fullfilled or not.
	 * 
	 * @param pattern url pattern to bind this handler to
	 * @param cb {@link MiddlewareCallback}, that has to respond to a {@link WrappedRequest}
	 * @return {@link Lambdatra} for chaining
	 */
	public Lambdatra<S> use(String pattern, MiddlewareCallback<S> cb) {
		this.router.addRoute(new Route<>(pattern, new MiddlewareAdapter<>(cb, sessions)));
		return this;
	}
	
}
