package sk.catheaven.model;

import sk.catheaven.core.Tuple;

/**
 * Simple storage unit that stores two entities as <i>left</i> and <i>right</i>.
 * Effective for coupling string and data objects as a single unit (labeled data).
 * @author catlord
 * @param <A>
 * @param <B>
 */
public class TupleImpl<A,B> implements Tuple<A, B> {
	private A left;
	private B right;
	
	public TupleImpl(A left, B right){
		this.left = left;
		this.right = right;
	}
	
	public void setLeft(A left) {
		this.left = left;
	}
	
	public void setRight(B right) {
		this.right = right;
	}
	
	/**
	 * @return the left
	 */
	@Override
	public A getLeft() {
		return left;
	}

	/**
	 * @return the right
	 */
	@Override
	public B getRight() {
		return right;
	}
}
