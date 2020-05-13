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
import java.nio.file.Path;
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
	private FileChooser chooser;
	private String previousPath = "";
	private String filename = "";
	
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
	
	/**
	 * Tries to save file with a remembered filename. If there is no remembered filename,
	 * or we fail to locate a file with a given filename, saveFileAs is called to allow 
	 * the user to specify the location and name of the file.
	 * @param stage Stage to display possible saveFileAs interface to.
	 * @param out Data (text) to save.
	 * @throws Exception
	 * @throws FileNotFoundException
	 * @throws IOException 
	 */
	public void saveFile(Stage stage, String out) throws Exception, FileNotFoundException, IOException {
		if(previousPath.isEmpty())
			saveFileAs(stage, out);
		
		File selectedFile = new File(previousPath);
		
		// if the file doesn't exist, ask user to name it and save it
		if( ! selectedFile.exists()){
			saveFileAs(stage, out);
			return;
		}
		
		// if it exists, open it and write into it
		FileWriter fos = new FileWriter(selectedFile);
		fos.write(out);
		fos.close();
	}
	
	public void saveFileAs(Stage stage, String out) throws Exception {
		if( ! previousPath.isEmpty())
			chooser.setInitialDirectory(new File(previousPath));
		
		File selectedFile = chooser.showSaveDialog(stage);
		if(selectedFile == null)
			throw new Exception("File not found !");
		
		filename = selectedFile.getName();
		previousPath = selectedFile.getPath();
		
		// if it exists, open it and write into it
		FileWriter fos = new FileWriter(selectedFile);
		fos.write(out);
		fos.close();
	}
}
