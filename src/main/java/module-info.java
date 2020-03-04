
// TODO: modularize to allow automatic modules
// source: https://stackoverflow.com/questions/46501047/what-does-required-filename-based-automodules-detected-warning-mean
module sk.catheaven.simmips {
    requires javafx.controls;
    requires javafx.fxml;
    requires junit;
	requires org.json;
	
	//opens sk.catheaven.simmips to javafx.fxml;
    exports sk.catheaven.run;
}