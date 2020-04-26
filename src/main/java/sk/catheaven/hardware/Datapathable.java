/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.hardware;

import sk.catheaven.instructionEssentials.Data;

/**
 * Interface defining all the necessary methods for datapath simulation.
 * @author catlord
 */
interface Datapathable {
	public abstract void execute();
	public abstract Data getData(String selector);
	public abstract void setData(String selector, Data data);
}
