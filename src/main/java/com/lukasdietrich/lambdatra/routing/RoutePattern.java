package com.lukasdietrich.lambdatra.routing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Defines an arbitrary path to be matched with actual requests.
 * 
 * @author Lukas Dietrich
 *
 */
public class RoutePattern {
	
	private static final Pattern PARAM_PATTERN 		= Pattern.compile(":([a-zA-Z0-9]+)");
	private static final String  PARAM_REPLACEMENT 	= "([^/]+)";

	private Pattern regex;
	private List<String> names;
	
	/**
	 * Creates a {@link RoutePattern} by its {@link String} representation.
	 * 
	 * @param simple
	 */
	public RoutePattern(String simple) {
		this.names = new Vector<>();
		
		boolean absolute = true;
		
		if (simple.endsWith("/*")) {
			absolute = false;
			simple = simple.substring(0, simple.length() - 2);
		}
		
		Matcher m = PARAM_PATTERN.matcher(simple);
		
		while (m.find())
			names.add(m.group(m.groupCount()));
		
		this.regex = Pattern.compile(
						"^" + 
						simple.replaceAll("([/\\|.()+*])", "\\\\$1")
							  .replaceAll(PARAM_PATTERN.pattern(), PARAM_REPLACEMENT)
						+ ((absolute) ? "$" : "")
					);
	}
	
	/**
	 * Matches a given path for a defined route.
	 * Returns an Optional indicating, if the route matched and contains
	 * a {@link Map} of parameters, if it does.
	 * 
	 * @param path
	 * @return
	 */
	public Optional<Map<String, String>> match(String path) {
		Matcher m = regex.matcher(path);
		
		if (m.find()) {
			Map<String, String> map = new HashMap<>();
			int i = 1;
			
			for (String n : names) {
				map.put(n, m.group(i++));
			}
			
			return Optional.of(map);
		} else {
			return Optional.empty();
		}
	}
	
}
