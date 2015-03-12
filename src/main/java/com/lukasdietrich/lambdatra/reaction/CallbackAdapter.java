package com.lukasdietrich.lambdatra.reaction;

/**
 * Abstract {@link Adapter} to hold a callback of class E
 * 
 * @author Lukas Dietrich
 *
 * @param <E> class of callback to be handled
 */
public abstract class CallbackAdapter<E> extends Adapter {
	
	private E callback;
	
	public CallbackAdapter(E callback) {
		this.callback = callback;
	}
	
	/**
	 * Returns the callback
	 * @return a callback
	 */
	protected E getCallback() {
		return this.callback;
	}
	
}
