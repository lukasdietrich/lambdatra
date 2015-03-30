package com.lukasdietrich.lambdatra.reaction.http;

import java.io.IOException;

/**
 * Callback for HTTP requests
 * 
 * @author Lukas Dietrich
 *
 */
@FunctionalInterface
public interface MiddlewareCallback<S> {

	/**
	 * Called on incoming http request.
	 * 
	 * @param req Request
	 * @param res Response
	 * @return returns whether or not the request was fulfilled
	 * @throws IOException may throw an exception on write failure
	 */
	public boolean call(WrappedRequest<S> req, WrappedResponse<S> res) throws IOException;
	
}

