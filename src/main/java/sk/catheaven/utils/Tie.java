/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * To make the code more clear, this class represents relationship 
 * one {@literal ->} one {@literal ->} many. It describes target component and list of 
 * selectors, that have to be set for that component.
 * Thus the relationship one{@literal ->}one (component {@literal ->} targetComponent) and 
 * finally one{@literal ->}many (targetComponent {@literal ->} list of inputs)
 * @author catlord
 */
public class Tie {
	private final Map<String, List<String>> targetsAndSelectors;
	
	public Tie(){
		targetsAndSelectors = new HashMap<>();
	}
	
	/**
	 * Checks, if a given target component already is defined. If it is defined, 
	 * adds the given selector to the list of selectors (naively, without checking
	 * repetition). If it is not, adds it as new target component, and adds the 
	 * selector to the list.
	 * @param targetComponent
	 * @param selector 
	 */
	public void addTie(String targetComponent, String selector){
		List<String> selectorsList = targetsAndSelectors.get(targetComponent);
		if(selectorsList == null){
			selectorsList = createList();
			selectorsList.add(selector);
			targetsAndSelectors.put(targetComponent, selectorsList);
		}
		else
			selectorsList.add(selector);
	}
	
	/**
	 * Checks if such target component has been already added. If no, adds it to the list.
	 * @param targetComponent 
	 */
	public void addTargetComponent(String targetComponent){
		if(targetsAndSelectors.get(targetComponent) == null)
			targetsAndSelectors.put(targetComponent, createList());
	}

	public Map<String, List<String>> getTies(){
		return targetsAndSelectors;
	}
	
	/**
	 * Just to allow possible implementation modification. Initial list 
	 * size is set ti 5, because it is expected to have up to 5 elements.
	 * (and since the type is list, it can be exceeded easily).
	 * @return 
	 */
	private List<String> createList(){
		return new ArrayList<>(5);
	}
}
