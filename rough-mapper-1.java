package a.b.c.tsa.validation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

public class RecordValidationMapper extends Mapper<LongWritable, Text, Text, NullWritable> {

	private Text word = new Text();
	private Set<String> stopWordList = new HashSet<String>();
	private BufferedReader fis;
	private int nColumnCount;
	private NodeList nList;

	@SuppressWarnings("deprecation")
	protected void setup(Context context) throws java.io.IOException,
			InterruptedException {
			
			// initializing multiple-outputs
			mos = new MultipleOutputs(context);
			
		try {

			Path[] xmlConfFiles = new Path[0];
			xmlConfFiles = context.getLocalCacheFiles();
			System.out.println(xmlConfFiles.toString());
			if (xmlConfFiles != null && xmlConfFiles.length > 0) {
				for (Path xmlConfFile : xmlConfFiles) {
					readXmlConfFile(xmlConfFile);
				}
			}
		} catch (IOException e) {
			System.err.println("Exception reading xml conf file: " + e);

		}

	}

	/*
	 * Method to read the xmlConfFile and get the fields
	 */

	private void readXmlConfFile(Path xmlConfFile) {
		try {
		File fXmlFile = new File(xmlConfFile.toString());
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
				
		doc.getDocumentElement().normalize();

		System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
				
		nList = doc.getElementsByTagName("tablecolumn");
		nColumnCount = nList.getLength();
		
		//System.out.println("-----------column-count-----------------"+nColumnCount);

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
		} catch (IOException ioe) {
			System.err.println("Exception while reading xml conf file '"
					+ xmlConfFile + "' : " + ioe.toString());
		}
	}

	public void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		String line = value.toString();
		StringTokenizer tokenizer = new StringTokenizer(line);
		int nTokenCount = tokenizer.countTokens();
	
		System.out.println("------------token-count----------------"+nTokenCount);
	
		if (nColumnCount == nTokenCount) {

		
			while (tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken();
				
				if (stopWordList.contains(token)) {
					context.getCounter(RecordValidation.COUNTERS.VALID_RECORDS)
							.increment(1L);
				 
				 mos.write("valid-records", NullWritable.get(), new Text(items[1]));
				 
				} else {
					context.getCounter(RecordValidation.COUNTERS.INVALID_RECORDS)
							.increment(1L);
					
					word.set(token);
					//context.write(word, null);
					mos.write("invalid-records", NullWritable.get(), new Text(items[2]));
				}
			}
		} 
	}
	
	@override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        
		//closing multiple-outputs
		mos.close();
    }
}
