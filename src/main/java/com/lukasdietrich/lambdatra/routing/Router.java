package com.lukasdietrich.lambdatra.routing;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Vector;

public class Router {
	
	private Vector<Route<?>> routes;
	
	public Router() {
		this.routes = new Vector<>();
	}
	
	public void addRoute(Route<?> route) {
		this.routes.add(route);
	}
	
	public Optional<Entry<Route<?>, Map<String, String>>> findRoute(String uri) {
		for (Route<?> r : routes) {
			Optional<Map<String, String>> params = r.match(uri);
			
			if (params.isPresent()) {
				return Optional.of(new SimpleEntry<>(r, params.get()));
			}
		}
		
		return Optional.empty();
	}
	
}
