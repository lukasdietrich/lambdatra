package com.lukasdietrich.lambdatra.reaction.http;

import java.io.IOException;

/**
 * Callback for HTTP requests
 * 
 * @author Lukas Dietrich
 *
 */
@FunctionalInterface
public interface HttpCallback<S> {

	/**
	 * Called on incoming http request.
	 * 
	 * @param req Request
	 * @param res Response
	 * @throws IOException may throw an exception on write failure
	 */
	public void call(WrappedRequest<S> req, WrappedResponse<S> res) throws IOException;
	
}
