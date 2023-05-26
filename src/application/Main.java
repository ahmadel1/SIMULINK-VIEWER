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
            //if(rectangles[key].getX() == 1040 && rectangles[key].getY() == 283)continue;
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
	public static int[] blocksPorts = new int[Main.blocks.getLength()];
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
	
	public static int[] getBlocksPorts(){
		for(int i = 0; Main.blocks.getLength()>i; i++) {
			Element tmp = (Element) Main.blocks.item(i);
			NodeList Ptags = tmp.getElementsByTagName("P");
			for(int j=0; Ptags.getLength()>j; j++) {
				Element tmpP = (Element) Ptags.item(j);
				String name = tmpP.getAttribute("Name");
				System.out.println(name);
				if (name.equals("Ports")) {
					int noOfBlocks = tmpP.getTextContent().charAt(1) - 48;
					blocksPorts[i] = noOfBlocks;
					System.out.println(noOfBlocks);
					break;
				}
			}
		}
		return blocksPorts;
	}
	
	public static int[][] getBlocksPositions() {
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
		return blocksPositions;
		
	}

	public static int getBlockInd(char id) {
		getBlocksAttributes();
		int notFound = -1;
		for(int i = 0; Main.blocks.getLength()>i; i++) {
			char tmp = blocksAttributes.get(i).get("SID").charAt(0);
			if(tmp == id) return i;
		}
		return notFound;
	}
	
	
	public static List<Pair<Integer, Integer>> getLines() {
		
		List<Pair<Integer, Integer>> pairList = new ArrayList<>();
		
		for(int i = 0; Main.lines.getLength()>i; i++) {
			
			Element tmp = (Element)  Main.lines.item(i);
			//check if line has branches or not
			if(tmp.getElementsByTagName("Branch").getLength()>0) {
				int sourceBlock = -1;
				int distBlock = -1;
				NodeList Ptags = tmp.getElementsByTagName("P");
				for(int j = 0; Ptags.getLength()>j; j++) {
					Element tmpP = (Element) Ptags.item(j);
					String name = tmpP.getAttribute("Name");
					if (name.equals("Src")) {
				        char src= tmpP.getTextContent().charAt(0);
				        sourceBlock = getBlockInd(src);  
					 }else if (name.equals("Dst")) {
					    char dst= tmpP.getTextContent().charAt(0);	
					    distBlock = getBlockInd(dst);
					 }
					 if(distBlock!=-1 && sourceBlock!=-1) {
						 pairList.add(new Pair<>(sourceBlock, distBlock));
						 distBlock=-1;
					 }
					System.out.println(tmpP.getTextContent());
				}
				
				continue;
			}else {
				NodeList Ptags = tmp.getElementsByTagName("P");
				int sourceBlock = -1;
				int distBlock = -1;
				for(int j = 0; Ptags.getLength()>j; j++) {
					
					Element tmpP = (Element) Ptags.item(j);
					String name = tmpP.getAttribute("Name");
					
					 if (name.equals("Src")) {
				        char src= tmpP.getTextContent().charAt(0);
				        sourceBlock = getBlockInd(src);
				        
					 }else if (name.equals("Dst")) {
					    char dst= tmpP.getTextContent().charAt(0);	
					    distBlock = getBlockInd(dst);
					 }
					 if(distBlock!=-1 && sourceBlock!=-1) {
						 pairList.add(new Pair<>(sourceBlock, distBlock));
						 break;
					 }
				}
				
			 }
		}
		return pairList;
	}
	
	public static Rectangle[] getRectangles() {
		Rectangle[] rectangles = new Rectangle[Main.blocks.getLength()];
		int[][] positions = getBlocksPositions();
		for (int i = 0; i < rectangles.length; i++) {
            Rectangle rectangle = new Rectangle(positions[i][0], positions[i][1], Math.abs(positions[i][0]-positions[i][2]), Math.abs(positions[i][1]-positions[i][3]));
            rectangles[i] = rectangle;
        }
		return rectangles;
	}
	
	public static void createRightAngleLine(Rectangle startRect, Rectangle endRect, int endPorts, Pane pane) {
	    double startX, startY, endX, endY, breakX, breakY;
	    
	    if (startRect.getX() + startRect.getWidth() < endRect.getX()+ startRect.getWidth()) {
	        // Start rectangle is to the left of the end rectangle
	        startX = startRect.getX() + startRect.getWidth();
	        startY = startRect.getY() + startRect.getHeight() / 2;
	        endX = endRect.getX() + (endRect.getWidth()/2);
	        endY = endRect.getY() + (endRect.getHeight()/2);
	        breakX = startX + (endX - startX) / 2;
	        breakY = endY;
	    } else {
	        // Start rectangle is to the right of the end rectangle
	    	startX = startRect.getX() + startRect.getWidth();
	        startY = startRect.getY() + startRect.getHeight() / 2;
	        endX = endRect.getX() + (endRect.getWidth());
	        endY = endRect.getY() + (endRect.getHeight()/2);;
	        
	        breakX = startX + (startX - endX) / 2;
	        breakY = endY;
	    }

	    Line segment1 = new Line(startX, startY, breakX, startY);
	    Line segment2 = new Line(breakX, startY, breakX, breakY);
	    Line segment3 = new Line(breakX, breakY, endX, breakY);
	    segment1.setStroke(Color.CYAN);	    
        segment2.setStroke(Color.CYAN);
        segment3.setStroke(Color.CYAN);
        
        
        pane.getChildren().addAll(segment1, segment2, segment3);
	}

	

}
