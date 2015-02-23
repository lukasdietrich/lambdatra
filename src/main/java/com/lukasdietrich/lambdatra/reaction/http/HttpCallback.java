package com.lukasdietrich.lambdatra.reaction.http;

import java.io.IOException;

/**
 * Callback for HTTP requests
 * 
 * @author Lukas Dietrich
 *
 */
@FunctionalInterface
public interface HttpCallback {

	/**
	 * Called on incoming http request.
	 * 
	 * @param req Request
	 * @param res Response
	 * @throws IOException
	 */
	public void call(WrappedRequest req, WrappedResponse res) throws IOException;
	
}
