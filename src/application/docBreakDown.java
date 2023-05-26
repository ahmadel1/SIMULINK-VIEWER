package application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;

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
	    int flag = 0;
	    if(endRect.getX() == 1040 && endRect.getY() == 209) {
	    	flag = 1;
	    	endRect.setY(endPorts*4+endRect.getY());
	    	System.out.println("yarab: "  + endRect.getY());
	    }
	    if (startRect.getX() + startRect.getWidth() < endRect.getX() + startRect.getWidth()) {
	        // Start rectangle is to the left of the end rectangle
	        startX = startRect.getX() + startRect.getWidth();
	        startY = startRect.getY() + startRect.getHeight() / 2;
	        endX = endRect.getX() + endRect.getWidth() / 2;
	        endY = endRect.getY() + endRect.getHeight() / 2;
	        breakX = startX + (endX - startX) / 2;
	        breakY = endY;
	    } else {
	        // Start rectangle is to the right of the end rectangle
	        startX = startRect.getX();
	        startY = startRect.getY() + startRect.getHeight() / 2;
	        endX = endRect.getX() + endRect.getWidth();
	        endY = endRect.getY() + endRect.getHeight() / 2;
	        breakX = startX + (startX - endX) / 2;
	        breakY = endY;
	    }

	    
	    if(startRect.getX() == 1040 && startRect.getY() == 283) {
	    	Line segment1 = new Line(endRect.getX()+endRect.getWidth()*2, endRect.getY()+endRect.getWidth()/2-1, endRect.getX()+endRect.getWidth()*2, startRect.getY()+endRect.getWidth()/2);
		    Line segment2 = new Line(endRect.getX()+endRect.getWidth()*2,   startRect.getY()+startRect.getWidth()/2-1, startRect.getX()+startRect.getWidth(), startRect.getY()+startRect.getWidth()/2);
		    Line segment3 = new Line(0, 0, 0, 0);
		    segment1.setStroke(Color.CYAN);
		    segment2.setStroke(Color.CYAN);
		    segment3.setStroke(Color.CYAN);
	        pane.getChildren().addAll(segment1, segment2, segment3);

	    }else {
	    	Line segment1 = new Line(startX, startY, breakX, startY);
		    Line segment2 = new Line(breakX, startY, breakX, breakY);
		    Line segment3 = new Line(breakX, breakY, endX, breakY);
		    segment1.setStroke(Color.CYAN);
		    segment2.setStroke(Color.CYAN);
		    segment3.setStroke(Color.CYAN);
	        pane.getChildren().addAll(segment1, segment2, segment3);

	    }
	    if(flag==1)endRect.setY(209);

	}
	

	

}