module simulinkViewer {
	requires javafx.controls;
	requires java.xml;
	
	opens application to javafx.graphics, javafx.fxml;
}
