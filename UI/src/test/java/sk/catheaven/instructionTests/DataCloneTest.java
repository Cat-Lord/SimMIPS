/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.instructionTests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sk.catheaven.instructionEssentials.Data;

/**
 *
 * @author catlord
 */
public class DataCloneTest {
	
	public DataCloneTest() {
	}
	
	@Before
	public void setUp() {
	}
	
	@Test 
	public void test(){
		for(int i = 0; i < 35; i++){
			Data d = new Data(i);
			int num = (int) (Math.random() * 100);
			d.setData(num);
			Data clone = d.duplicate();
			Assert.assertEquals(d.getBitSize(), clone.getBitSize());
			Assert.assertEquals(d.getData(), clone.getData());
		}
	}
}
