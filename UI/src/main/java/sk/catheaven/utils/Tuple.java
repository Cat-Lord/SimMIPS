/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.utils;

/**
 * Simple storage unit that stores two entities as <i>left</i> and <i>right</i>.
 * Effective for coupling string and data objects as a single unit (labeled data).
 * @author catlord
 * @param <A>
 * @param <B>
 */
public class Tuple<A,B> {
	private final A left;
	private final B right;
	
	public Tuple(A left, B right){
		this.left = left;
		this.right = right;
	}
	
	/**
	 * @return the left
	 */
	public A getLeft() {
		return left;
	}

	/**
	 * @return the right
	 */
	public B getRight() {
		return right;
	}
}
