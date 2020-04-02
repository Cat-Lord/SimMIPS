/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package argumentTypeTests;

import org.junit.Test;
import static org.junit.Assert.*;
import sk.catheaven.utils.argumentTypes.ArgumentType;
import sk.catheaven.utils.argumentTypes.IntArgumentType;

/**
 *
 * @author catlord
 */
public class IntArgumentTest {
	ArgumentType arg;
	
	public IntArgumentTest() {
		arg = new IntArgumentType();
	}
	
	@Test
	public void test(){
		//
		// testing for success
		//
		try {
			arg.parse("1");
			arg.parse("2");
			arg.parse("3");
			arg.parse("094");
			arg.parse("10");
			arg.parse("18");
			arg.parse("123");
			arg.parse("1234");
			arg.parse("12345");
			arg.parse("123456");
			arg.parse("1234567");
		} catch(Exception e){ fail("Exception was not expected !"); }

		
		//
		// testing for exceptions
		//
		try {
			arg.parse("a");
			fail("Exception was expected !");
		} catch(Exception e){}
		
		try {
			arg.parse("1a");
			fail("Exception was expected !");
		} catch(Exception e){}
		
		try {
			arg.parse("a123");
			fail("Exception was expected !");
		} catch(Exception e){}
		
		try {
			arg.parse("1f3fd128");
			fail("Exception was expected !");
		} catch(Exception e){}
		
		try {
			arg.parse("0f");
			fail("Exception was expected !");
		} catch(Exception e){}
		
		try {
			arg.parse("1_2");
			fail("Exception was expected !");
		} catch(Exception e){}
		
		try {
			arg.parse("71.2");
			fail("Exception was expected !");
		} catch(Exception e){}
		
		try {
			arg.parse("7,17");
			fail("Exception was expected !");
		} catch(Exception e){}
		
		try {
			arg.parse("l91236");
			fail("Exception was expected !");
		} catch(Exception e){}

		try {
			arg.parse("912o");
			fail("Exception was expected !");
		} catch(Exception e){}
		
		try {
			arg.parse("111111111111111111111111111111111111111111111");
			fail("Exception was expected !");
		} catch(Exception e){}
		
		try {
			arg.parse("`");
			fail("Exception was expected !");
		} catch(Exception e){}
		
		try {
			arg.parse("incorrectNumber");
			fail("Exception was expected !");
		} catch(Exception e){}
		
		try {
			arg.parse("^ff3");
			fail("Exception was expected !");
		} catch(Exception e){}
		
		try {
			arg.parse("125*2318");
			fail("Exception was expected !");
		} catch(Exception e){}
		
		try {
			arg.parse("09)217");
			fail("Exception was expected !");
		} catch(Exception e){}
		
		try {
			arg.parse("21(2515)");
			fail("Exception was expected !");
		} catch(Exception e){}
		
		try {
			arg.parse("1!");
			fail("Exception was expected !");
		} catch(Exception e){}
		
		try {
			arg.parse("210@5");
			fail("Exception was expected !");
		} catch(Exception e){}
		
		try {
			arg.parse("7:");
			fail("Exception was expected !");
		} catch(Exception e){}
	}
	
}
