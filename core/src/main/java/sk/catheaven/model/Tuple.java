package sk.catheaven.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import sk.catheaven.utils.TupleDeserializer;

/**
 * Simple storage unit that stores two entities as <i>left</i> and <i>right</i>.
 * Effective for coupling string and data objects as a single unit (labeled data).
 * @author catlord
 * @param <A>
 * @param <B>
 */
@JsonDeserialize(using = TupleDeserializer.class)
public class Tuple<A,B> {
	private A left;
	private B right;
	
	public Tuple(A left, B right){
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
