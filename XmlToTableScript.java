package a.b.c;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlToTableScript {

	public static void main(String[] args){
		
		String ScriptFileName = "C:\\Users\\jagadishkumar_t\\Desktop\\hive-script.txt";
		String tablePrefix = "CREATE TABLE XYZ ("+System.lineSeparator();
		String tableSuffix = ")"+System.lineSeparator()
								+"ROW FORMAT DELIMITED"+System.lineSeparator()
								+"FIELDS TERMINATED BY \",\" "+System.lineSeparator()
								+"STORED AS TEXTFILE;";
		
		
	    try {
	    	
	    	ArrayList<String> fieldLengths = new ArrayList<String>();
	    	ArrayList<String> dataTypes = new ArrayList<String>();

	    	File fXmlFile = new File("C:\\Users\\jagadishkumar_t\\Desktop\\nfile.xml");
	    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    	Document doc = dBuilder.parse(fXmlFile);
	    				
	    	doc.getDocumentElement().normalize();

//	    	System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
	    	
	    	NodeList nList = doc.getElementsByTagName("tablecolumn");
	    	int nColumnCount = nList.getLength();

//	    	System.out.println(nColumnCount);
	    	
	    	for (int temp = 0; temp < nList.getLength(); temp++) {

	    		Node nNode = nList.item(temp);
	    				
//	    		System.out.println("\nCurrent Element :" + nNode.getNodeName());
	    				
	    		if (nNode.getNodeType() == Node.ELEMENT_NODE) {

	    			Element eElement = (Element) nNode;
	    			fieldLengths.add(eElement.getElementsByTagName("columnname").item(0).getTextContent());
	    			dataTypes.add(eElement.getElementsByTagName("sourcedatatype").item(0).getTextContent());
	    		}
	    	}

			File ScriptFile = new File(ScriptFileName);
			if (!ScriptFile.exists()) {
				ScriptFile.createNewFile();
			}
			FileWriter ScriptFileWritter = new FileWriter(ScriptFile, true);
			BufferedWriter bufferWritter = new BufferedWriter(ScriptFileWritter);
			
			bufferWritter.write(tablePrefix);
			
	    	Iterator<String> fItr = fieldLengths.iterator();
	    	Iterator<String> dItr = dataTypes.iterator();
	    	int i =0;
	    	while(fItr.hasNext()){ i++;
//	    		System.out.println(fItr.next() +"="+ dItr.next());
				StringBuilder sb = new StringBuilder().append("");
				if(i == 1){
					sb.append("  "+fItr.next()+" "+dItr.next());
				} else {
					sb.append(", "+fItr.next()+" "+dItr.next());
				}
				sb.append(System.lineSeparator());
				bufferWritter.write(sb.toString());

	    	}
	    	bufferWritter.write(tableSuffix);
			bufferWritter.close();
			
			System.out.println("Script Ready!");
	    	
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }

	}
	
}
