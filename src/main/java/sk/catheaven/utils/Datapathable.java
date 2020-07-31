/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.utils;

import sk.catheaven.instructionEssentials.Data;

/**
 * Interface defining all the necessary methods for datapath simulation.
 * Selector is not required for use, some components ignore its value (
 * for example simple binary component with just one output value).
 * @author catlord
 */
public interface Datapathable {
	public abstract void execute();
	public abstract Data getOutput(String selector);
	public abstract boolean setInput(String selector, Data data);
}
