/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.hardware;

/**
 *
 * @author catlord
 */
public abstract class Component implements Datapathable {
	protected String label;
	
	public Component(String label){
		this.label = label;
	}
	
	public String getLabel(){
		return label;
	}
}
