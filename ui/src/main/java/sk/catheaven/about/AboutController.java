package sk.catheaven.about;

import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import sk.catheaven.main.Launcher;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Todo - automatically send bug reports, display icon authors, automatically publish logs, ...
 */
public class AboutController implements Initializable {
    @FXML private ImageView cpuImage;
    @FXML private Hyperlink link;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cpuImage.setImage(new Image(getClass().getResourceAsStream("/simmips.png")));	// Icon made by <a href="https://www.flaticon.com/authors/nhor-phai" title="Nhor Phai">Nhor Phai</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
    }

    public void displayLink(){
        Launcher.getLocalHostServices().showDocument(link.getText());
    }
}
