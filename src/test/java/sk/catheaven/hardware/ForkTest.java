/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.hardware;

import org.json.JSONObject;
import org.junit.Test;
import static org.junit.Assert.*;
import sk.catheaven.instructionEssentials.Data;
import sk.catheaven.utils.Tuple;

/**
 *
 * @author catlord
 */
public class ForkTest extends Container {
	
	public ForkTest() {
	}
	
	@Test
	public void test(){
		Fork af;
		Data testData = new Data(15);
		testData.setData(69);
		
		
		//
		//			FORK 1
		//
		JSONObject fork1json = createTestFor(15, new Tuple[] {
			new Tuple("address", "15")
		});
		
		try {
			af = new Fork("AF", fork1json);
		} catch(Exception e) { System.out.println(e.getMessage()); fail("AF not created"); return; }
		
		assertTrue(af.setInput("This is to be ignored", testData));			// should ignore the label, because "AF" fork has both outputs the same
		
		af.execute();
		
		for(int i = 0; i < 10; i++){
			Data d = af.getOutput("address");
			assertEquals(15, d.getBitSize());
			assertEquals(testData.getData(), d.getData());
		}
		
		//
		//			FORK 2
		//
		JSONObject fork2json = createTestFor(15, new Tuple[] {
			new Tuple("address", "15"),
			new Tuple("extra", "3")
		});
		
		Fork f;
		try {
			f = new Fork("F", fork2json);
		} catch(Exception e) { System.out.println(e.getMessage()); fail("AF not created"); return; }
		
		testData.setData(12345);
		assertTrue(f.setInput("ignored, because fork has only one input", testData));
		f.execute();	
	
		for(int i = 0; i < 20; i++){
			assertEquals(testData.getData(), f.getOutput("address").getData());
			assertEquals(1, f.getOutput("extra").getData());
		}
		
		//
		//			FORK 3 with cutting
		//
		JSONObject fork3json = createTestFor(10, new Tuple[] {
			new Tuple("address", "10"),
			new Tuple("extra", "2-6"),
			new Tuple("signal", "0-9")
		});
		
		Fork cutf;
		try {
			cutf = new Fork("cutF", fork3json);
		} catch(Exception e) { System.out.println(e.getMessage()); fail("AF not created"); return; }
		
		testData.setData(442);
		assertTrue(cutf.setInput("ignored, because fork has only one input", testData));
		cutf.execute();
		
		assertEquals(testData.getData(), cutf.getOutput("address").getData());
		assertEquals(11, cutf.getOutput("extra").getData());
		assertEquals(0, cutf.getOutput("signal").getData());
		
		testData.setData(1022);
		cutf.setInput("ignore me !", testData);
		cutf.execute();
		
		assertEquals(testData.getData(), cutf.getOutput("address").getData());
		assertEquals(15, cutf.getOutput("extra").getData());
		assertEquals(1, cutf.getOutput("signal").getData());
		
		
		
		// 
		// 'Descending' fork
		//
		
		JSONObject fork4json = createTestFor(11, new Tuple[] {
			new Tuple("left", "10"),
			new Tuple("signal", "0-10")
		});
		
		Fork descFork;
		try {
			descFork = new Fork("descendant", fork4json);
		} catch(Exception e) { System.out.println(e.getMessage()); fail("AF not created"); return; }
		
		testData.setData(1027);
		descFork.setInput("", testData);
		descFork.execute();
		
		assertEquals(3, descFork.getOutput("left").getData());
		assertEquals(1, descFork.getOutput("signal").getData());
		
		testData.setData(1859);
		descFork.setInput("", testData);
		descFork.execute();
		
		assertEquals(835, descFork.getOutput("left").getData());
		assertEquals(1, descFork.getOutput("signal").getData());
		
		testData.setData(835);
		descFork.setInput("", testData);
		descFork.execute();
		
		assertEquals(835, descFork.getOutput("left").getData());
		assertEquals(0, descFork.getOutput("signal").getData());
	}
	
	
	
	/**
	 * Creates fork json object with given output values. Good for testing.
	 * @param specs
	 * @return 
	 */
	private JSONObject createTestFor(int inputSize, Tuple<String,String>[] specs){
		String json = "{ \"label\": \"F1\",\n" +
"            \"type\": \"Fork\",\n" +
"            \"in\": " + inputSize + ",\n" +
"            \"out\": [\n";
		
		for(int i = 0; i < specs.length; i++){
			json = json.concat( "{ \"label\": \"" + specs[i].getLeft() + "\", \"bitSize\": \"" + specs[i].getRight());
			if(i == specs.length - 1)
				json = json.concat("\" }\n" );
			else
				json = json.concat("\" },\n" );
		}
		
        json = json.concat("]}");
		
		return new JSONObject(json);
	}
}
