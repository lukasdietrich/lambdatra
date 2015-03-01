package com.lukasdietrich.lambdatra.reaction.http;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Wraps {@link HttpRequest} into a simpler class
 * and combines it with access to parsed url query
 * and parameters.
 * 
 * @author Lukas Dietrich
 *
 */
public final class WrappedRequest {

	private static final String ENCODING = "UTF-8";
	
	private FullHttpRequest req;
	private Map<String, String> params;
	private Map<String, String> query;
	
	private String path;
	
	public WrappedRequest(FullHttpRequest req, Map<String, String> params) {
		this.req = req;
		this.params = params;
		this.query = new HashMap<>();
	
		{
			String[] uri = req.getUri().split("\\?", 2);
			path = uri[0];
			
			if (uri.length > 1) {
				for (String qpart : uri[1].split("&")) {
					String[] q = qpart.split("=", 2);
					
					try {
						query.put(URLDecoder.decode(q[0], ENCODING),
								 (q.length > 1) 
								 	? URLDecoder.decode(q[1], ENCODING) 
								 	: "");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	/**
	 * Returns an {@link Optional} of a header by key
	 * 
	 * @param key header name
	 * @return header value
	 */
	public Optional<String> getHeader(String key) {
		return Optional.ofNullable(req.headers().get(key));
	}
	
	/**
	 * Returns an {@link Optional} of a parameter by key
	 * 
	 * @param key parameter name
	 * @return parameter value
	 */
	public Optional<String> getParam(String key) {
		return Optional.ofNullable(params.get(key));
	}
	
	/**
	 * Returns an {@link Optional} of a querystring value by key
	 * 
	 * @param key query name
	 * @return query value
	 */
	public Optional<String> getQuery(String key) {
		return Optional.ofNullable(query.get(key));
	}
	
	/**
	 * Returns the request path omitting the query part.
	 * <br>
	 * (eg.: "http://example.com <b>/this/is/the/path</b> ?this=is&amp;not")
	 * 
	 * @return the requested path
	 */
	public String getPath()  {
		return path;
	}
	
}
