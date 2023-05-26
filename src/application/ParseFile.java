package application;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ParseFile {
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
	    Main.doc = factory.newDocumentBuilder().parse(new InputSource(new StringReader(fileInTxt)));
	  
	    Main.blocks = Main.doc.getElementsByTagName("Block");
	    Main.lines = Main.doc.getElementsByTagName("Line");
	}
}
