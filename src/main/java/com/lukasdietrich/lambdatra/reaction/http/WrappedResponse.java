package com.lukasdietrich.lambdatra.reaction.http;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders.Names;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import de.neuland.jade4j.Jade4J;

/**
 * Wraps {@link HttpResponse} into a simpler class
 * and adding the ability to render {@link Jade4J}
 * Templates.
 * 
 * @author Lukas Dietrich
 *
 */
public class WrappedResponse {

	private FullHttpResponse res;
	
	public WrappedResponse(FullHttpResponse res) {
		this.res = res;
	}
	
	/**
	 * Sets an arbitrary header
	 * 
	 * @param key
	 * @param value
	 */
	public void setHeader(String key, String value) {
		res.headers().set(key.toString(), value);
	}
	
	/**
	 * Sets headers for {@link HttpResponseStatus}
	 * 
	 * @param status
	 */
	public void setStatus(HttpResponseStatus status) {
		res.setStatus(status);
	}
	
	/**
	 * Sets headers for mime type
	 * 
	 * @param mime
	 */
	public void setMime(String mime) {
		setHeader(CONTENT_TYPE, mime);
	}
	
	/**
	 * Sets headers for cache-control with the given 
	 * expiration date in seconds.
	 * 
	 * @param expires
	 */
	public void enableCache(long expires) {
		setHeader(Names.CACHE_CONTROL, String.format("max-age=%d", expires));
	}
	
	/**
	 * Returns a {@link ByteBuf} to write output to.
	 * 
	 * @return
	 */
	public ByteBuf getBuffer() {
		return res.content();
	}
	
	/**
	 * Renders a Jade Template (via {@link Jade4J}) and outputs
	 * it to the {@link ByteBuf}.
	 * 
	 * @param template
	 * @param partials
	 * @throws IOException
	 */
	public void render(URL template, Map<String, Object> partials) throws IOException {
		ByteBufUtil.writeUtf8(getBuffer(), Jade4J.render(template, partials));
	}
	
}
