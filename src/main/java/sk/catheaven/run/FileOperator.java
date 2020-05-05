/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.run;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import sk.catheaven.run.Loader;

/**
 *
 * @author catlord
 */
public class FileOperator {
	FileChooser chooser;
	
	public FileOperator(){
		chooser = new FileChooser();
		chooser.setInitialDirectory(new File(System.getProperty("user.home")));
	}
	
	/**
	 * Tries to open selected file and return its content as String.
	 * @param stage
	 * @return
	 * @throws FileNotFoundException 
	 */
	public String openFile(Stage stage) throws FileNotFoundException {
		chooser.setTitle("Open file...");
		File selectedFile = chooser.showOpenDialog(stage);
		if(selectedFile == null)
			return "";
		
		Scanner scanner = new Scanner(selectedFile);
		String out = "";
		while(scanner.hasNextLine()){
			out += scanner.nextLine();
			if(scanner.hasNextLine())
				out += "\n";
		}
		return out;
	}
	
	public void saveFile(Stage stage, String filename, String out) throws Exception, FileNotFoundException, IOException {
		File selectedFile = new File(filename);
		
		// if the file doesn't exist, ask user to name it and save it
		if( ! selectedFile.exists()){
			saveFileAs(stage, filename, out);
			return;
		}
		
		// if it exists, open it and write into it
		FileWriter fos = new FileWriter(selectedFile);
		fos.write(out);
		fos.close();
	}
	
	public void saveFileAs(Stage stage, String filename, String out) throws Exception {
		File selectedFile = chooser.showSaveDialog(stage);
		if(selectedFile == null)
			throw new Exception("File not found !");
		
		// if it exists, open it and write into it
		FileWriter fos = new FileWriter(selectedFile);
		fos.write(out);
		fos.close();
	}
}
