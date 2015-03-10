package com.lukasdietrich.lambdatra.session;

import java.util.Optional;

/**
 * Arbitrary {@link SessionStore} to hold any implemented data structure.
 * 
 * @author Lukas Dietrich
 *
 * @param <E> class of data to store as session
 */
public interface SessionStore<E> {

	/**
	 * Returns the name of the header value, the 
	 * session id will be stored at.
	 * 
	 * @return header key
	 */
	public String getCookieKey();
	
	/**
	 * Starts a session with an initial value.
	 * 
	 * @param value value of session
	 * @return a generated session id
	 */
	public String startSession(E value);
	
	/**
	 * Returns an {@link Optional} of a session value,
	 * that may be empty, if none exists.
	 * 
	 * @param id session id
	 * @return {@link Optional} of session
	 */
	public Optional<E> getSession(String id);
	
}
