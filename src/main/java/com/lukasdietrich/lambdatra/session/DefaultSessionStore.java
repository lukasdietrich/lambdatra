package com.lukasdietrich.lambdatra.session;

import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * Default implementation of {@link SessionStore} using 
 * {@link Hashtable} to store data.
 * 
 * @author Lukas Dietrich
 *
 * @param <E> class of session value
 */
public class DefaultSessionStore<E> implements SessionStore<E> {

	private String cookieKey;
	private long lifetime;
	private Map<String, LifeTimeWrapper> values;
	
	public DefaultSessionStore(String cookieKey, long lifetime) {
		this.cookieKey = cookieKey;
		this.lifetime = lifetime;
		this.values = new Hashtable<>();
	
		if (lifetime > 0) {
			new Timer().schedule(new TimerTask() {
				
				@Override
				public void run() {
					final long NOW = System.currentTimeMillis();
					
					values.keySet().forEach(key -> {
						if (values.get(key).expires < NOW) {
							values.remove(key);
						}
					});
				}
				
			}, lifetime, lifetime);
		}
	}
	
	@Override
	public String getCookieKey() {
		return this.cookieKey;
	}

	@Override
	public String startSession(E value) {
		String key = UUID.randomUUID().toString();
		values.put(key, new LifeTimeWrapper(value));
		return key;
	}
	
	@Override
	public void stopSession(String id) {
		values.remove(id);
	}

	@Override
	public Optional<E> getSession(String id) {
		LifeTimeWrapper wrapper = values.get(id);
		return (wrapper != null) 
				? Optional.of(wrapper.getValue()) 
				: Optional.empty();
	}
	
	/**
	 * Associates a value with an expiration date
	 * 
	 * @author Lukas Dietrich
	 *
	 */
	private class LifeTimeWrapper {
		
		private long expires;
		private E value;
		
		private LifeTimeWrapper(E value) {
			this.value = value;
			updateExpire();
		}
		
		/**
		 * updates expiration date to {@link System#currentTimeMillis()} + {@link DefaultSessionStore#lifetime}
		 */
		private void updateExpire() {
			this.expires = System.currentTimeMillis() + lifetime;
		}
		
		/**
		 * Returns the underlying value
		 * 
		 * @return value of wrapper
		 */
		public E getValue() {
			updateExpire();
			return this.value;
		}
		
	}

}
