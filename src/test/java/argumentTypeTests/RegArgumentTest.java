/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package argumentTypeTests;

import org.junit.Test;
import static org.junit.Assert.*;
import sk.catheaven.utils.argumentTypes.*;

/**
 * Testing register argument type parsing.
 * @author catlord
 */
public class RegArgumentTest {
	ArgumentType arg;
	
	public RegArgumentTest() {
		arg = new RegArgumentType();
	}
	
	@Test
	public void test(){
		//
		// testing for success
		//
		try {
			for(int i = 0; i < 1000; i++){
				arg.parse("r"+ i);
				arg.parse("R"+ i);
			}
		} catch(Exception e){ fail("Exception was not expected !"); }

		
		//
		// testing for exceptions
		//
		try {
			arg.parse("r-2");
			fail("Exception was expected !"); 
		} catch(Exception e){}
		
		try {
			arg.parse("2");
			fail("Exception was expected !"); 
		} catch(Exception e){}
		
		try {
			arg.parse("r");
			fail("Exception was expected !"); 
		} catch(Exception e){}
		
		try {
			arg.parse("rr2");
			fail("Exception was expected !"); 
		} catch(Exception e){}
		
		try {
			arg.parse("i3");
			fail("Exception was expected !"); 
		} catch(Exception e){}
		
		try {
			arg.parse("reg3");
			fail("Exception was expected !"); 
		} catch(Exception e){}
		
		try {
			arg.parse("3r2");
			fail("Exception was expected !"); 
		} catch(Exception e){}
		
		try {
			arg.parse("2r2");
			fail("Exception was expected !"); 
		} catch(Exception e){}
		
		try {
			arg.parse("d7");
			fail("Exception was expected !"); 
		} catch(Exception e){}
	}
}
