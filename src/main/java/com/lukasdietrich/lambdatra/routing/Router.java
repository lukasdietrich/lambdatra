package com.lukasdietrich.lambdatra.routing;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Vector;

public class Router {
	
	private Vector<Route> routes;
	
	public Router() {
		this.routes = new Vector<>();
	}
	
	public void addRoute(Route route) {
		this.routes.add(route);
	}
	
	public Iterable<MatchedRoute> findRoute(String uri) {
		return () -> new RouteIterator(uri);
	}
	
	private class RouteIterator implements Iterator<MatchedRoute> {

		private String uri;
		private Iterator<Route> routes;
		
		private Optional<MatchedRoute> next;
		
		private RouteIterator(String uri) {
			this.uri = uri;
			this.routes = Router.this.routes.iterator();
			
			this.findNext();
		}
		
		private void findNext() {
			while (routes.hasNext()) {
				Route route = routes.next();
				
				Optional<Map<String, String>> params = route.match(uri);
				
				if (params.isPresent()) {
					next = Optional.of(new MatchedRoute(route, params.get()));
				}
			}
			
			next = Optional.empty();
		}
		
		@Override
		public boolean hasNext() {
			return next.isPresent();
		}

		@Override
		public MatchedRoute next() {
			MatchedRoute value = next.get();
			findNext();
			return value;
		}
		
	}
	
}
