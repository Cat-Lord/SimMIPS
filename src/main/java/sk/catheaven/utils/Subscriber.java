/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.utils;

/**
 *
 * @author catlord
 */
public interface Subscriber {
	public void prepareSub();
	public void updateSub();
	public void clear();
}
