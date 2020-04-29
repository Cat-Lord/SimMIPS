/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.utils;

/**
 * Connecting two components, this class represent a simple way of
 * encapsulating these relationships. 
 * @author catlord
 */
public class Connector {
	private final String from, to;
	private final String selector;

	public Connector(String from, String to, String selector) {
		this.from = from;
		this.to = to;
		this.selector = selector;
	}

	/**
	 * @return the from
	 */
	public String getFrom() {
		return from;
	}

	/**
	 * @return the to
	 */
	public String getTo() {
		return to;
	}

	/**
	 * @return the selector
	 */
	public String getSelector() {
		return selector;
	}

	
	
}
