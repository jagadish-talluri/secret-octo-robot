package com.infi.citi.top.log.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyReader {

	private static final String FILE_NAME = "D:\\path\\to\\top.txt";

	private static final String RECORD_PATTERN = "^[0-9].*$";

	private static Pattern pattern;

	public void patternValidator() {
		pattern = Pattern.compile(RECORD_PATTERN);
	}

	public static boolean isProcessRecord(final String record) {

		Matcher matcher = pattern.matcher(record);
		return matcher.matches();

	}

	public static void main(String[] args) throws IOException {

		ArrayList<String> rl = new ArrayList<String>();
		HashMap hm = new HashMap();

		File topFile = new File(FILE_NAME);
		if (!topFile.exists()) {
			System.out.println("File Not Found");
		}

		try {

			BufferedReader br = new BufferedReader(new FileReader(topFile));
			String line;
			while ((line = br.readLine()) != null) {
//System.out.println(line);
				if (line.matches(RECORD_PATTERN)) {

					// code for processes
					// 1,2,9,10,12 fields
					String[] fields = line.split(" ", 12);
					// System.out.println(fields[0] +"-"+ fields[1] +"-"+
					// fields[8] +"-"+ fields[9] +"-"+ fields[11]);
					rl.add(fields[0] + "-" + fields[1] + "-" + fields[8] + "-"
							+ fields[9] + "-" + fields[11]);

				} else {

					if (line.contains("top -")) {
//						 System.out.println(line);
						String[] fields = line.split(",", 12);
						hm.put("timestamp",fields[0].split(" ",6)[2]);
						hm.put("UpTime",fields[1]);
						hm.put("No.OfUsers",fields[2].split(" ",6)[1]);
						hm.put("LoadAverage1min",fields[3].split(" ",6)[3]);
						hm.put("LoadAverage5min",fields[4]);
						hm.put("LoadAverage15min",fields[5]);
//					System.out.println(fields.length);
//						for (int i = 0; i < fields.length; i++)
//							System.out.println(fields[i]);

					} else if (line.contains("Tasks")) {
//						 System.out.println(line);
						String[] fields = line.split(",", 12);
						hm.put("TotalTasks",fields[0].split(" ",6)[1]);
						hm.put("RunningTasks",fields[1].split(" ",6)[1]);
						hm.put("SleepingTaks",fields[2].split(" ",6)[1]);
						hm.put("StoppedTaks",fields[3].split(" ",6)[1]);
						hm.put("ZombieTasks",fields[4].split(" ",6)[1]);
//						System.out.println(fields.length);
//						for (int i = 0; i < fields.length; i++)
//							System.out.println(fields[i]);
					} else if (line.contains("Cpu(s)")) {
//						 System.out.println(line);
						String[] fields = line.split(",", 12);
						hm.put("userCPUtime%",fields[0].split(" ",6)[1].split("%",6)[0]);
						hm.put("systemCPUtime%",fields[1].split("%",6)[0]);
						hm.put("userNiceCPUtime%",fields[2].split("%",6)[0]);
						hm.put("idleCPUtime%",fields[3].split("%",6)[0]);
						hm.put("ioWaitTime%",fields[4].split("%",6)[0]);
						hm.put("hardInterruptCPUtime%",fields[5].split("%",6)[0]);
						hm.put("softInterruptCPUtime%",fields[6].split("%",6)[0]);
						hm.put("stealTime%",fields[7].split("%",6)[0]);
//						System.out.println(fields.length);
//						for (int i = 0; i < fields.length; i++)
//							System.out.println(fields[i]);
					} else if (line.contains("Mem:")) {
//						 System.out.println(line);
						String[] fields = line.split(",", 12);
						hm.put("totalMemory",fields[0].split(" ",6)[1]);
						hm.put("usedMemory",fields[1].split(" ",6)[1]);
						hm.put("freeMemory",fields[2].split(" ",6)[1]);
						hm.put("bufferMemory",fields[3].split(" ",6)[1]);
//						System.out.println(fields.length);
//						for (int i = 0; i < fields.length; i++)
//							System.out.println(fields[i]);
					} else if (line.contains("Swap:")) {
//						 System.out.println(line);
						String[] fields = line.split(",", 12);
						hm.put("totalSWAP",fields[0].split(" ",6)[1]);
						hm.put("usedSWAP",fields[1].split(" ",6)[1]);
						hm.put("freeSWAP",fields[2].split(" ",6)[1]);
						hm.put("cachedSWAP",fields[3].split(" ",6)[1]);
//						System.out.println(fields.length);
//						for (int i = 0; i < fields.length; i++)
//							System.out.println(fields[i]);
					}
				}

			}
		} finally {
			
			// Get a set of the entries
		      Set set = hm.entrySet();
		      // Get an iterator
		      Iterator i = set.iterator();
		      // Display elements
		      while(i.hasNext()) {
		         Map.Entry me = (Map.Entry)i.next();
		         System.out.print(me.getKey() + ": ");
		         System.out.println(me.getValue());
		      }
			
		      System.out.println(rl);
//			System.out.println("exception aya");
		}

	}
}
