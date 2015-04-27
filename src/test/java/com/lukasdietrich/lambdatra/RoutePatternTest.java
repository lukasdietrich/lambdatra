package com.lukasdietrich.lambdatra;

import java.util.Map;
import java.util.Optional;

import junit.framework.TestCase;

import com.lukasdietrich.lambdatra.routing.RoutePattern;

public class RoutePatternTest extends TestCase {

	public void testPatterns() {
		assertTrue(new RoutePattern("").match("").isPresent());
		assertTrue(new RoutePattern("/foo/bar").match("/foo/bar").isPresent());
	}
	
	public void testParameters() {
		Optional<Map<String, String>> o = new RoutePattern("/foo/:param/bar").match("/foo/foobar/bar");
		assertTrue(o.isPresent());
		assertEquals("foobar", o.get().get("param"));
	}
	
}
