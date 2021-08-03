/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.argumentTypeTests;

import org.junit.Test;
import static org.junit.Assert.*;
import sk.catheaven.instructionEssentials.argumentTypes.ArgumentType;
import sk.catheaven.instructionEssentials.argumentTypes.DataArgumentType;

/**
 *
 * @author catlord
 */
public class DataArgumentTest {
	ArgumentType arg;
	
	public DataArgumentTest() {
		arg = new DataArgumentType();
	}
	
	@Test
	public void test(){
		//
		// testing for success
		//
		try {
			arg.parse("361(r8)");
		} catch(Exception e){ fail("Exception was not expected !"); }
	
		try {
			arg.parse("0000(r0)");
		} catch(Exception e){ fail("Exception was not expected !"); }
	
		try {
			arg.parse("62(r27)");
		} catch(Exception e){ fail("Exception was not expected !"); }
		
		try {
			arg.parse("0026(r52)");
		} catch(Exception e){ fail("Exception was not expected !"); }
		
		try {
			arg.parse("361(r8025)");
		} catch(Exception e){ fail("Exception was not expected !"); }
		
		try {
			arg.parse("123123(r8)");
		} catch(Exception e){ fail("Exception was not expected !"); }
		
		try {
			arg.parse("0032(r1)");
		} catch(Exception e){ fail("Exception was not expected !"); }
		
		try {
			arg.parse("361(r2)");
		} catch(Exception e){ fail("Exception was not expected !"); }
		
		try {
			arg.parse("361(r3)");
		} catch(Exception e){ fail("Exception was not expected !"); }
		
		try {
			arg.parse("361(r4)");
		} catch(Exception e){ fail("Exception was not expected !"); }
		
		try {
			arg.parse("361(r5)");
		} catch(Exception e){ fail("Exception was not expected !"); }
		
		try {
			arg.parse("5911(r32)");
		} catch(Exception e){ fail("Exception was not expected !"); }

		
		//
		// testing for exceptions
		//
		try {
			arg.parse("1");
			fail("Exception was expected !"); 
		} catch(Exception e){}
		
		try {
			arg.parse("rendom(teXt)");
			fail("Exception was expected !"); 
		} catch(Exception e){}
		
		try {
			arg.parse("(r1)");
			fail("Exception was expected !"); 
		} catch(Exception e){}
		
		try {
			arg.parse("f(r2)");
			fail("Exception was expected !"); 
		} catch(Exception e){}
		
		try {
			arg.parse("123()");
			fail("Exception was expected !"); 
		} catch(Exception e){}
		
		try {
			arg.parse("123(r1");
			fail("Exception was expected !"); 
		} catch(Exception e){}
		
		try {
			arg.parse("6(r)");
			fail("Exception was expected !"); 
		} catch(Exception e){}

		try {
			arg.parse("fff(r8)");
			fail("Exception was expected !"); 
		} catch(Exception e){}
		
		try {
			arg.parse("");
			fail("Exception was expected !"); 
		} catch(Exception e){}
		
		try {
			arg.parse("2a3(r81)");
			fail("Exception was expected !"); 
		} catch(Exception e){}
		
		try {
			arg.parse("3(e81)");
			fail("Exception was expected !"); 
		} catch(Exception e){}
		
		try {
			arg.parse("02(95)");
			fail("Exception was expected !"); 
		} catch(Exception e){}
		
		try {
			arg.parse("r32(r32)");
			fail("Exception was expected !"); 
		} catch(Exception e){}
	}
	
}
