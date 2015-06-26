package com.lukasdietrich.lambdatra.routing;

import java.util.Map;

public class MatchedRoute {

	private Route route;
	private Map<String, String> params;
	
	public MatchedRoute(Route route, Map<String, String> params) {
		this.route = route;
		this.params = params;
	}
	
	public Route getRoute() {
		return route;
	}
	
	public Map<String, String> getParams() {
		return params;
	}
	
}
