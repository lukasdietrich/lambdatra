package com.lukasdietrich.lambdatra.reaction.http;

import java.io.IOException;

import com.lukasdietrich.lambdatra.reaction.Adapter;
import com.lukasdietrich.lambdatra.session.SessionStore;

/**
 * {@link Adapter} for {@link MiddlewareCallback}
 * 
 * @author Lukas Dietrich
 *
 */
public class MiddlewareAdapter<S> extends BaseHttpAdapter<S, MiddlewareCallback<S>> {

	public MiddlewareAdapter(MiddlewareCallback<S> callback, SessionStore<S> sessions) {
		super(callback, sessions);
	}

	@Override
	protected boolean handle(WrappedRequest<S> req, WrappedResponse<S> res) throws IOException {
		return getCallback().call(req, res);
	}

}
