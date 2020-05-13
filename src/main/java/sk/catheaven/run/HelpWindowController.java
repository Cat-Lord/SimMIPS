/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.run;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.controlsfx.tools.Platform;

/**
 * Simple window that displays basic information about the application.
 * @author catlord
 */
public class HelpWindowController implements Initializable {
	@FXML private ImageView cpuImage;
	@FXML private Hyperlink link;
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		cpuImage.setImage(new Image(getClass().getResourceAsStream("/sk/catheaven/simmips/cpu.png")));	// Icon made by <a href="https://www.flaticon.com/authors/nhor-phai" title="Nhor Phai">Nhor Phai</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
	}
	
	// TODO
	public void displayLink(){
		//getHostServices().showDocument(link.getText());
	}
	
}
