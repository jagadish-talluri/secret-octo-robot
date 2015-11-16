package a.b.c.tsa.validation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.hadoop.mapreduce.Mapper#setup(org.apache.hadoop.mapreduce.
	 * Mapper.Context)
	 */
	@SuppressWarnings("deprecation")
	protected void setup(Context context) throws java.io.IOException,
			InterruptedException {

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
			fis = new BufferedReader(new FileReader(xmlConfFile.toString()));
			String stopWord = null;
			while ((stopWord = fis.readLine()) != null) {
				stopWordList.add(stopWord);
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

		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			
			if (stopWordList.contains(token)) {
				context.getCounter(RecordValidation.COUNTERS.VALID_RECORDS)
						.increment(1L);
			 
			 mos.write(fruitOutputName, NullWritable.get(), new Text(items[1]));
			 
			} else {
				context.getCounter(RecordValidation.COUNTERS.INVALID_RECORDS)
						.increment(1L);
				
				word.set(token);
				//context.write(word, null);
				mos.write(colorOutputName, NullWritable.get(), new Text(items[2]));
			}
		}
	}
	
	@override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        mos.close();
    }
}
