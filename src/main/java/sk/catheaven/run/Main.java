/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.run;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONObject;

import sk.catheaven.essentials.Data;
import sk.catheaven.essentials.InstructionType;

/**
 *
 * @author catlord
 */
public class Main {
    public static void main(String[] args) throws IOException, URISyntaxException{
		
		// Test: Parsing input file 
		JSONObject inFile = new JSONObject(readFile("sk/catheaven/data/layout.json"));
		JSONObject types = inFile.getJSONObject("types");
		List<InstructionType> iTypes = new ArrayList<>();
		
		Iterator<String> currType = types.keys();
		while(currType.hasNext()){
			String typeLabel = currType.next();
			iTypes.add(new InstructionType(typeLabel, types.getJSONArray(typeLabel)));
		}
		
		// testing
		iTypes.forEach(iType -> {
			System.out.println("" + iType.getType());
			System.out.println("Fields: " );
			iType.getFields().forEach(field -> {
				System.out.println("" + field.getLabel() + " of size " + field.getBitSize() + "b");
			});
			System.out.println();
		});
    }
	
	private static String readFile(String fileName) throws IOException, URISyntaxException{
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		URI uri = classloader.getResource(fileName).toURI();
		List<String> lines = Files.readAllLines(Paths.get(uri));
		String json = "";
		
		for(String line : lines)
			json += line;
		return json;
	}
}
