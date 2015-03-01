package com.lukasdietrich.lambdatra.routing;

import java.util.Map;
import java.util.Optional;

import com.lukasdietrich.lambdatra.reaction.CallbackAdapter;

/**
 * Container for a {@link CallbackAdapter} bound to a {@link RoutePattern}
 * 
 * @author Lukas Dietrich
 *
 * @param <E> class of {@link CallbackAdapter} implementation
 */
public class Route<E extends CallbackAdapter<?>> {
	
	private RoutePattern pattern;
	private E cb;
	
	/**
	 * Creates a {@link Route} that holds both, 
	 * a {@link CallbackAdapter} and a {@link RoutePattern}.
	 * 
	 * @param pattern url pattern to match requests against
	 * @param cb a {@link CallbackAdapter} to handle requests on match
	 */
	public Route(String pattern, E cb) {
		this.pattern = new RoutePattern(pattern);
		this.cb = cb;
	}
	
	/**
	 * Exposes {@link RoutePattern#match(String)}.
	 * 
	 * @see RoutePattern#match(String)
	 * @param path requested path
	 * @return an {@link Optional} of parameters or an empty {@link Optional}
	 */
	public Optional<Map<String, String>> match(String path) {
		return pattern.match(path);
	}
	
	/**
	 * Returns the {@link CallbackAdapter}
	 * 
	 * @return the underlying callback
	 */
	public E getCallback() {
		return this.cb;
	}
	
}
