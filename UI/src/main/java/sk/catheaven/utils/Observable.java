/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.utils;

/**
 * Every class that implements this interface allows subscribers to be notified 
 * when it desires. Subscribers can then decide on what action they will take.
 * @author catlord
 */
public interface Observable {
	public void notifySubs();
	public void registerSub(Subscriber sub);
	public void unregisterSub(Subscriber sub);
}
