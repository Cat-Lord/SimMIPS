/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.utils;

/**
 * Subscriber needs to implement this interface to be first prepared to be 
 * a subscriber (<code>prepareSub()</code> is called once when needed). In
 * addition they need to be able to receive updates from observable object.
 * If needed, they have to implement clear method to reset all their data.
 * @author catlord
 */
public interface Subscriber {
	public void prepareSub();
	public void updateSub();
	public void clear();
}
