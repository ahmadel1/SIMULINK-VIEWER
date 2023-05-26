package application;
	
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;


public class Main extends Application {
	public static Document doc;
	public static NodeList blocks;
	public static NodeList lines;
	private static String filePath;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			Button btn = new Button("load");
			btn.setOnAction(e ->{
				FileChooser fc = new FileChooser();
				File file = fc.showOpenDialog(primaryStage);
				filePath = file.toString();
				System.out.println(filePath);
				try {
					ParseFile.readFile();
					primaryStage.setScene(SimulationWindow());
					primaryStage.centerOnScreen();
				} catch (IOException | SAXException | ParserConfigurationException e1) {
					e1.printStackTrace();
				}
				System.out.println("Number of Blocks: " + blocks.getLength());
			});
			StackPane root = new StackPane();
			Scene scene = new Scene(root,400,400);
			root.getChildren().add(btn);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public Scene SimulationWindow() {
		Pane simulationPane = new Pane(); 
		Scene simulationScene = new Scene(simulationPane);
		int[][] blocksPos = docBreakDown.getBlocksPositions();
		
		List<Pair<Integer, Integer>> pairs = docBreakDown.getLines();
		Rectangle[] rectangles = docBreakDown.getRectangles();
		int[] recPorts = docBreakDown.getBlocksPorts();
		for(int i = 0; 5>i; i++) {
			System.out.println(recPorts[i]);
		}
		for (Pair<Integer, Integer> pair : pairs) {
            int key = pair.getKey();
            int value = pair.getValue();
            if(recPorts[value] == 0 )recPorts[value] = 1;
            docBreakDown.createRightAngleLine(rectangles[key], rectangles[value], recPorts[value], simulationPane);   
            recPorts[value]--;
        }
		
		
		for(int i = 0; Main.blocks.getLength() > i; i++) {
			double left = blocksPos[i][0];
			double top = blocksPos[i][1];
		    double width = Math.abs(left-blocksPos[i][2]); 
		    double height = Math.abs(top-blocksPos[i][3]);
		    VBox tmp = createBox(i, blocksPos, height, width);
		    tmp.setLayoutX(left);
	        tmp.setLayoutY(top); 
			simulationPane.getChildren().add(tmp);
		}

		return simulationScene;
	}
	public VBox createBox(int i, int[][] blocksPos, double height, double width) {
		 
		 docBreakDown.getBlocksAttributes();
		 String recName = docBreakDown.blocksAttributes.get(i).get("Name");
		 System.out.println(recName);
         Label label = new Label(recName);

		 VBox vBox = new VBox(5);
		 vBox.setAlignment(Pos.CENTER);
		 
	     Rectangle rec = new Rectangle();
	     rec.setHeight(height);
	     rec.setWidth(width);
	     rec.setArcWidth(10);
	     rec.setArcHeight(10);
	     rec.setFill(Color.WHITE);
	     rec.setStroke(Color.CYAN);
	     
		 vBox.getChildren().addAll(rec, label);
		 return vBox;
	}
	
	public static void main(String[] args){
		launch(args);
	}
	
	
}


