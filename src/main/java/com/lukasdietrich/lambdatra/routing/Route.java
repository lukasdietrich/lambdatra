package com.lukasdietrich.lambdatra.routing;

import java.util.Map;
import java.util.Optional;

import com.lukasdietrich.lambdatra.reaction.Adapter;

/**
 * Container for a {@link Adapater} bound to a {@link RoutePattern}
 * 
 * @author Lukas Dietrich
 *
 * @param <E> class of {@link Adapter} implementation
 */
public class Route<E extends Adapter> {
	
	private RoutePattern pattern;
	private E adapter;
	
	/**
	 * Creates a {@link Route} that holds both, 
	 * an {@link Adapter} and a {@link RoutePattern}.
	 * 
	 * @param pattern url pattern to match requests against
	 * @param adapter an {@link Adapter} to handle requests on match
	 */
	public Route(String pattern, E adapter) {
		this.pattern = new RoutePattern(pattern);
		this.adapter = adapter;
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
	 * Returns the {@link Adapter}
	 * 
	 * @return the underlying adapter
	 */
	public E getAdapter() {
		return this.adapter;
	}
	
}
