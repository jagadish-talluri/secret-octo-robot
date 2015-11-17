package a.b.c;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;

import java.util.StringTokenizer;

public class ReadXMLFile {

  public static void main(String argv[]) {

    try {

	File fXmlFile = new File("D:\\jagadishkumar_t\\citi act\\practice code\\tsa-table-conf.xml");
	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	Document doc = dBuilder.parse(fXmlFile);
			
	doc.getDocumentElement().normalize();

	System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			
	NodeList nList = doc.getElementsByTagName("tablecolumn");
	int nColumnCount = nList.getLength();
	
	System.out.println("-----------column-count-----------------"+nColumnCount);
	
	String str = "-1504053727, O, T, 20150603 00:00:00, A, S, +000000000000000.00000000, 8, 70, +000000000000000.000000";
	StringTokenizer st = new StringTokenizer(str, ",");
	
	int nTokenCount = st.countTokens();
	
	System.out.println("------------token-count----------------"+nTokenCount);
	
	if (nColumnCount == nTokenCount) {System.out.println("Valid");}
			
	System.out.println("----------------------------");

	for (int temp = 0; temp < nList.getLength(); temp++) {

		Node nNode = nList.item(temp);
				
		System.out.println("\nCurrent Element :" + nNode.getNodeName());
				
		if (nNode.getNodeType() == Node.ELEMENT_NODE) {

			Element eElement = (Element) nNode;
			
			
//			System.out.println("Staff id : " + eElement.getAttribute("id"));
			System.out.println("Column Name : " + eElement.getElementsByTagName("columnname").item(0).getTextContent());
			System.out.println("Data Type : " + eElement.getElementsByTagName("sourcedatatype").item(0).getTextContent());
			System.out.println("Data Length : " + eElement.getElementsByTagName("datalength").item(0).getTextContent());
			System.out.println("IsNull : " + eElement.getElementsByTagName("isnullable").item(0).getTextContent());

		}
	}
    } catch (Exception e) {
	e.printStackTrace();
    }
  }

}
