package application;
	
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;


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
					readFile();
					docBreakDown.getBlocksPositions();
					primaryStage.setScene(SimulationWindow());
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
		BorderPane simulationPane = new BorderPane(); 
		Scene simulationScene = new Scene(simulationPane, 400, 400);
		Rectangle rect = new Rectangle(200, 100); // for test
		rect.setFill(Color.WHITE);
		rect.setStroke(Color.BLACK);
		simulationPane.setCenter(rect);
		return simulationScene;
	}
	
	public static void main(String[] args){
		launch(args);
	}
	
	public static void readFile() throws IOException, SAXException, ParserConfigurationException {
		String filePath = "D:\\simulinkViewer\\Example.mdl";
		String fileContent = new String(Files.readAllBytes(Paths.get(filePath)));
		 
		 //specifying the start and end of the needed part 
		int beginHere = fileContent.indexOf("__MWOPC_PART_BEGIN__ /simulink/systems/system_root.xml");
		int start = fileContent.indexOf("<System>", beginHere); 
		int end = fileContent.indexOf("</System>", beginHere); 
		String fileInTxt = fileContent.substring(start, end+10);
		 
		 //converting needed part to xml file 
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    doc = factory.newDocumentBuilder().parse(new InputSource(new StringReader(fileInTxt)));
	  
	    blocks = doc.getElementsByTagName("Block");
	    lines = doc.getElementsByTagName("Line");
	}
}

class docBreakDown{
	docBreakDown(){};
	public static Vector<Map<String, String>> blocksAttributes = new Vector<>();
	public static int[][] blocksPositions = new int[Main.blocks.getLength()][4];
	
	public static void getBlocksAttributes() {
		for(int i = 0; Main.blocks.getLength() > i; i++) {
			Map<String, String>tmp = new HashMap<>();
			for(int j = 0; Main.blocks.item(i).getAttributes().getLength() > j; j++) {
				String key = Main.blocks.item(i).getAttributes().item(j).getNodeName();
				String value = Main.blocks.item(i).getAttributes().item(j).getNodeValue();
				tmp.put(key, value);
			}
			blocksAttributes.add(tmp);
		}
	}	
	
	public static void getBlocksPositions() {
		for(int i = 0; Main.blocks.getLength() > i; i++) {
			Element tmp1 = (Element) Main.blocks.item(i);
			NodeList Ptags = tmp1.getElementsByTagName("P");
			
			for(int j = 0; Ptags.getLength()>j; j++) {
				Element tmp = (Element) Ptags.item(j);
				String name = tmp.getAttribute("Name");
				 if (name.equals("Position")) {
			        String dimensions = tmp.getTextContent();
			        String[] positionArr = dimensions.replaceAll("\\[|\\]", "").split(", ");
			        for(int k = 0; 4>k; k++) {
			        	blocksPositions[i][k] = Integer.parseInt(positionArr[k]);
			        }
			        break;
			    }
			}
		}
		for(int i = 0; 5>i; i++) {
			System.out.print("blcok " + i +" position: ");
			for(int k = 0; 4>k; k++) {
				System.out.print(blocksPositions[i][k] + " ");
			}
			System.out.println();
		}
	}
	
}
