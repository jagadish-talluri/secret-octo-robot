package a.b.c;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class ExcelToXml {
    
//	private static final String EXCEL_FILE_PATH = "C:\\Users\\jagadishkumar_t\\Desktop\\table-meta.xlsx";
	private static String EXCEL_FILE_PATH;
	private static String XML_FILE_PATH;

    public static void main(String args[]) {
    	
    	EXCEL_FILE_PATH = args[0];
    	XML_FILE_PATH = args[1];
    	
        List<Column> columnList = getColumnsListFromExcel();
//        System.out.println(columnList);
        setColumnsListToXml(columnList);
    }

    private static List<Column> getColumnsListFromExcel() {
        List<Column> columnList = new ArrayList<Column>();
        FileInputStream fis = null;
        
        try {
            fis = new FileInputStream(EXCEL_FILE_PATH);

            // Using XSSF for xlsx format, for xls use HSSF
            Workbook workbook = new XSSFWorkbook(fis);
            
            int numberOfSheets = workbook.getNumberOfSheets();
            String sheetName = workbook.getSheetName(0);

            System.out.println(numberOfSheets+" "+sheetName);
            
            //looping over each workbook sheet
            for (int i = 0; i < numberOfSheets; i++) {
                Sheet sheet = workbook.getSheetAt(i);
                Iterator<Row> rowIterator = sheet.iterator();

                int rowNumber = 0;
                
                //iterating over each row
                while (rowIterator.hasNext()) {
                	
                	//increment row number for every valid row entry 
                	rowNumber++;
                	
                    Column column = new Column();
                    Row row = rowIterator.next();
                    Iterator<Cell> cellIterator = row.cellIterator();

                    //Iterating over each cell (column wise)  in a particular row.
                    while ((rowNumber !=1) & cellIterator.hasNext()) {

                        Cell cell = cellIterator.next();
                        	//Cell with index 0 contains marks in columnName
                        	if (cell.getColumnIndex() == 0){
                        		column.setColumnName(cell.getStringCellValue());
                        	}
                        	//Cell with index 1 contains marks in sourceDataType
                        	else if (cell.getColumnIndex() == 1){
                        		column.setSourceDataType(cell.getStringCellValue());
                        	}
                        	//Cell with index 2 contains dataLength
                        	else if (cell.getColumnIndex() == 2) {
                        		column.setDataLength(String.valueOf(cell.getNumericCellValue()));
                        	}
                        	//Cell with index 3 contains marks in isNullable
                        	else if (cell.getColumnIndex() == 3){
                        		column.setIsNullable(cell.getStringCellValue());
                        	}
                        	//Cell with index 4 contains marks in hiveDataType
                        	else if (cell.getColumnIndex() == 4){
                        		column.setHiveDataType(cell.getStringCellValue());
                        	}

                    }
                    //end iterating a row, add all the elements of a row in list
//                    System.out.println(column);
                    if(rowNumber != 1) columnList.add(column);
                }
            }

            fis.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return columnList;
    }


	private static void setColumnsListToXml(List<Column> cl){
		  try {

			  	Iterator<Column> itr = cl.iterator();
			  
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

				// root elements
				Document doc = docBuilder.newDocument();
				Element rootElement = doc.createElement("table");
				doc.appendChild(rootElement);

				while(itr.hasNext()) {
					Column c = itr.next();
				// tablecolumn elements
				Element staff = doc.createElement("tablecolumn");
				rootElement.appendChild(staff);
				
				// columnname elements
				Element columnname = doc.createElement("columnname");
				columnname.appendChild(doc.createTextNode(c.getColumnName()));
				staff.appendChild(columnname);

				// sourcedatatype elements
				Element sourcedatatype = doc.createElement("sourcedatatype");
				sourcedatatype.appendChild(doc.createTextNode(c.getSourceDataType()));
				staff.appendChild(sourcedatatype);

				// datalength elements
				Element datalength = doc.createElement("datalength");
				datalength.appendChild(doc.createTextNode(c.getDataLength().toString()));
				staff.appendChild(datalength);

				// isnullable elements
				Element isnullable = doc.createElement("isnullable");
				isnullable.appendChild(doc.createTextNode(c.getIsNullable()));
				staff.appendChild(isnullable);

				// hivedatatype elements
				Element hivedatatype = doc.createElement("hivedatatype");
				hivedatatype.appendChild(doc.createTextNode(c.getHiveDataType()));
				staff.appendChild(hivedatatype);
				}
				
				// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new File(XML_FILE_PATH));

				// Output to console for testing
				// StreamResult result = new StreamResult(System.out);

				transformer.transform(source, result);

				System.out.println("File saved!");

		  } catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		  } catch (TransformerException tfe) {
			tfe.printStackTrace();
		  }

	}
}
