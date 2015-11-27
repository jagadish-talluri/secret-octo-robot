package a.b.c.tsa.validation;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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

        private int nColumnCount;
        private NodeList nList;
        private String delimiter, dateFormat;

        // create arraylist
        private ArrayList<String> fieldLengths = new ArrayList<String>();
        private ArrayList<String> isNullableList = new ArrayList<String>();
        private ArrayList<String> columnNames = new ArrayList<String>();
        private ArrayList<String> dataTypes = new ArrayList<String>();

        private MultipleOutputs<Text, Text> mos;

        @SuppressWarnings("deprecation")
        protected void setup(Context context) throws java.io.IOException, InterruptedException {

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

                    	NodeList dNodeList = doc.getElementsByTagName("delimiter");
                    	Node delimiterNode = dNodeList.item(0);
                    	delimiter = delimiterNode.getTextContent();
                    	
                    	NodeList dfNodeList = doc.getElementsByTagName("dateformat");
                    	Node dfNode = dfNodeList.item(0);
                    	dateFormat = dfNode.getTextContent();

                        
                        System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

                        // list of all column tags
                        nList = doc.getElementsByTagName("tablecolumn");
                        nColumnCount = nList.getLength();

                        // System.out.println("-----------column-count-----------------"+nColumnCount);

                        for (int temp = 0; temp < nList.getLength(); temp++) {

                                // a particular column tag
                                Node nNode = nList.item(temp);

//                              System.out.println("\nCurrent Element :" + nNode.getNodeName());

                                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                                        Element eElement = (Element) nNode;

                                        fieldLengths.add(eElement.getElementsByTagName("datalength").item(0).getTextContent());
                                        isNullableList.add(eElement.getElementsByTagName("isnullable").item(0).getTextContent());
                                        columnNames.add(eElement.getElementsByTagName("columnname").item(0).getTextContent());
                                        dataTypes.add(eElement.getElementsByTagName("sourcedatatype").item(0).getTextContent());
                                }
                        }
                } catch (Exception ioe) {
                        System.err.println("Exception while reading xml conf file '" + xmlConfFile + "' : " + ioe.toString());
                }
        }
        
		public boolean isDateValid(String dateToValidate, String dateFromat) {
			SimpleDateFormat sdf = new SimpleDateFormat(dateFromat);
			sdf.setLenient(false);
	
			try {
	
				// if not valid, it will throw ParseException
				Date date = sdf.parse(dateToValidate);
				//System.out.println(date);
	
			} catch (ParseException e) {
	
				e.printStackTrace();
				return false;
			}
	
			return true;
		}
    	
		public boolean isDataTypeValid(String dataToValiate, String dataType){
			
			if (dataType.equals("int")){
				try{int i = Integer.parseInt(dataToValiate);}
				catch(Exception e) {return false;}
			} else if (dataType.equals("float")){
				try{float f = Float.parseFloat(dataToValiate);}
				catch(Exception e) {return false;}
				
			}
			
			return true;
		}
		
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

                String errorMsg = " [Error Msg";
                String line = value.toString();
                
                
                /* string tokenizer failed to identify empty tokens */
                //StringTokenizer tokenizer = new StringTokenizer(line, ",");
                //int nTokenCount = tokenizer.countTokens();

                // replace StringTokenizer logic with String splits to add empty token recognition 
                // string splits in a List, this helps not to change the while loop, -1 for including trailing empty tokens
                String[] lineSplits = line.split(delimiter, -1);
                int nTokenCount = lineSplits.length;
                List<String> lineSplitsList = Arrays.asList(lineSplits);
                Iterator tokenizer = lineSplitsList.iterator();
                

                Iterator itrFieldLen = fieldLengths.iterator();
                Iterator itrNullable = isNullableList.iterator();
                Iterator itrColNames = columnNames.iterator();
                Iterator itrDataTypes = dataTypes.iterator();

//              System.out.println("------------token-count----------------" + nTokenCount);

                boolean isValid = false;
                
                // column count validation, delimiter check
                if (nColumnCount == nTokenCount) {
                        isValid = true;
                        // stop iteration if any column validation fails, performance tip
                        while (isValid && tokenizer.hasNext()) {
                        		
                        		// these are from input file
                                String token = tokenizer.next().toString().trim();
                                
                                // these are from xml conf file
                                int fieldLength = Integer.parseInt(itrFieldLen.next().toString());
                                String isNullableValue = itrNullable.next().toString();
                                String colName = itrColNames.next().toString();
                                String DataType = itrDataTypes.next().toString();
                                
                                // check for validation of NULLs, is it NULL? and can it be NULL?
                                if (token.length() == 0 & isNullableValue.equals("N")) {
                                        isValid = false;
                                        errorMsg = errorMsg.concat(" : \'"+colName+"\' Field is Null");
                                }

                                // field length validation, is the expected length same?
//                                else if (fieldLength != token.length()) {
//                                        isValid = false;
//                                        errorMsg = errorMsg.concat(" : \'"+colName+"\' Field expected length is \'"+fieldLength+"\' but received \'"+token.length()+"\'") ;
//                                }
                                
                                // date format validation, is the field datetime? is it valid?
                                else if (DataType.equals("datetime") & (!isDateValid(token,dateFormat))){
                                	isValid = false;
                                	errorMsg = errorMsg.concat(" : \'"+colName+"\' Field expected date format is \'"+dateFormat+"\' but received \'"+token+"\'");
                                }
                                
                                // integer data type validation, is the field int? is it valid?
                                else if (DataType.equals("int") & (!isDataTypeValid(token, "int"))){
                                	isValid = false;
                                	errorMsg = errorMsg.concat(" : unable to pasre as Integer, received \'"+token+"\'");
                                }

                                // float data type validation, is the field float? is it valid?
                                else if (DataType.equals("float") & (!isDataTypeValid(token, "float"))){
                                	isValid = false;
                                	errorMsg = errorMsg.concat(" : unable to pasre as Float, received \'"+token+"\'");
                                }
                                
                        }
                } else {errorMsg = errorMsg.concat(" : either delimiter is wrong or column count doesn't match, expected \'"+nColumnCount+"\' but received \'"+nTokenCount+"\'");}
                
                // write the output to files
                if (isValid) {
                        context.getCounter(RecordValidation.COUNTERS.VALID_RECORDS).increment(1L);
                        mos.write("validrecords", NullWritable.get(), value);

                } else {
                        context.getCounter(RecordValidation.COUNTERS.INVALID_RECORDS).increment(1L);
//                        String r = errorMsg.toString()+"]";
//                        Text t = new Text(value.toString()+r);
                        value = new Text(value.toString() + errorMsg.toString() + "]");
                        mos.write("invalidrecords", NullWritable.get(), value);
                }
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {

                // closing multiple-outputs
                mos.close();
        }
}
