
// TODO: modularize to allow automatic modules
// source: https://stackoverflow.com/questions/46501047/what-does-required-filename-based-automodules-detected-warning-mean
module sk.catheaven.simmips {
    requires javafx.controls;
    requires javafx.fxml;
    requires junit;
	requires org.json;
	requires org.fxmisc.richtext;
	requires flowless;
	requires reactfx;
	requires java.logging;
	requires org.controlsfx.controls;
	
	opens sk.catheaven.utils to javafx.base;
	opens sk.catheaven.instructionEssentials to javafx.base;
	opens sk.catheaven.run to javafx.fxml;
    exports sk.catheaven.run;
}