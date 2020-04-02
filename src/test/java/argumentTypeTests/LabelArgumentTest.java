/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package argumentTypeTests;

import org.junit.Test;
import static org.junit.Assert.*;
import sk.catheaven.utils.argumentTypes.ArgumentType;
import sk.catheaven.utils.argumentTypes.LabelArgumentType;

/**
 *
 * @author catlord
 */
public class LabelArgumentTest {
	ArgumentType arg;
	
	public LabelArgumentTest() {
		arg = new LabelArgumentType();
	}
	
	@Test
	public void test(){
		//
		// testing for success
		//
		try {
			arg.parse("correctLabel");
			arg.parse("correctLabel24680");
			arg.parse("correctLabel_15_AAAAAA");
			arg.parse("correctLabel_correctlabel_123_label_correct");
			arg.parse("l");
			arg.parse("it");
		} catch(Exception e){ fail("Exception was not expected !"); }
		
		//
		// testing for exceptions
		//
		try {
			arg.parse("2incorrectLabel");
			fail("Excpected exception");
		} catch(Exception e){}
		
		try {
			arg.parse("[incorrectLabel");
			fail("Excpected exception");
		} catch(Exception e){}
		
		try {
			arg.parse("incorrectLabel[");
			fail("Excpected exception");
		} catch(Exception e){}
		
		try {
			arg.parse("incorrectLabel(ss)");
			fail("Excpected exception");
		} catch(Exception e){}
		
		try {
			arg.parse("incorrectLabel;");
			fail("Excpected exception");
		} catch(Exception e){}
		
		try {
			arg.parse("incorrectLabel`");
			fail("Excpected exception");
		} catch(Exception e){}
		
		try {
			arg.parse("incorrectLabel'");
			fail("Excpected exception");
		} catch(Exception e){}
		
		try {
			arg.parse("incorrectLabel\\");
			fail("Excpected exception");
		} catch(Exception e){}
		
		try {
			arg.parse("incorrectLabel<");
			fail("Excpected exception");
		} catch(Exception e){}
		
		try {
			arg.parse("incorrect<3Label");
			fail("Excpected exception");
		} catch(Exception e){}
		
		try {
			arg.parse("incorrect?Label");
			fail("Excpected exception");
		} catch(Exception e){}
		
		try {
			arg.parse("incorrect-Label");
			fail("Excpected exception");
		} catch(Exception e){}
		
		try {
			arg.parse("1");
			fail("Excpected exception");
		} catch(Exception e){}
		
		try {
			arg.parse("90165");
			fail("Excpected exception");
		} catch(Exception e){}
		
		try {
			arg.parse("?");
			fail("Excpected exception");
		} catch(Exception e){}
	}
	
}
