package com.lukasdietrich.lambdatra.reaction.http;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.CookieDecoder;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.HttpHeaders.Names;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.lukasdietrich.lambdatra.session.SessionStore;

/**
 * Wraps {@link HttpRequest} into a simpler class
 * and combines it with access to parsed url query
 * and parameters.
 * 
 * @author Lukas Dietrich
 *
 */
public final class WrappedRequest<S> {
	
	private FullHttpRequest req;
	private QueryStringDecoder query;
	
	private Map<String, String> params;
	private Map<String, Cookie> cookies;
	private SessionStore<S> sessions;
	
	public WrappedRequest(FullHttpRequest req, Map<String, String> params, SessionStore<S> sessions) {
		this.req = req;
		this.params = params;
		this.query = new QueryStringDecoder(req.getUri());
		this.cookies = new HashMap<>();
		this.sessions = sessions;
		
		{
			getHeader(Names.COOKIE).ifPresent(header -> {
				for (Cookie c : CookieDecoder.decode(header)) {
					cookies.put(c.getName(), c);
				}
			});
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
	public Optional<List<String>> getQuery(String key) {
		return Optional.ofNullable(query.parameters().get(key));
	}
	
	public HttpPostRequestDecoder parseBody() {
		return new HttpPostRequestDecoder(new DefaultHttpDataFactory(Short.MAX_VALUE), req);
	}
	
	public ByteBuf getBody() {
		return req.content();
	}
	
	/**
	 * Returns an {@link Optional} of the session
	 * 
	 * @return session value
	 */
	public Optional<S> getSession() {
		Optional<Cookie> cookie = getCookie(sessions.getCookieKey());
		
		return (cookie.isPresent())
				? sessions.getSession(cookie.get().getValue())
				: Optional.empty();
	}
	
	/**
	 * Returns a {@link Set} of {@link Cookie}s.
	 * 
	 * @deprecated use {@link #getCookie(String)} instead
	 * @return set of of cookies
	 */
	public Set<Cookie> getCookies() {
		return new HashSet<>(cookies.values());
	}
	
	/**
	 * Returns an {@link Optional} of a {@link Cookie} by key
	 * 
	 * @param key name of {@link Cookie}
	 * @return a cookie
	 */
	public Optional<Cookie> getCookie(String key) {
		return Optional.ofNullable(cookies.get(key));
	}
	
	/**
	 * Returns the request path omitting the query part.
	 * <br>
	 * (eg.: "http://example.com <b>/this/is/the/path</b> ?this=is&amp;not")
	 * 
	 * @return the requested path
	 */
	public String getPath()  {
		return query.path();
	}
	
}
