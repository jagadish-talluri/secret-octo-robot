package a.b.c.tsa.validation;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

@SuppressWarnings("deprecation")
public class RecordValidation {
	
	public enum COUNTERS {
		  VALID_RECORDS,
		  INVALID_RECORDS
		 }
	public static void main(String[] args) throws Exception {
		
		final String NAME_NODE = "hdfs://localhost:9000";
		
		Configuration conf = new Configuration();
//		GenericOptionsParser parser = new GenericOptionsParser(conf, args);
//		String[] other_args = parser.getRemainingArgs();
		
		Job job = new Job(conf, "RecordValidation");
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		job.setJarByClass(RecordValidation.class);
		job.setMapperClass(RecordValidationMapper.class);
		job.setNumReduceTasks(0);
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		
		//adding this file from HDFS
	    DistributedCache.addCacheFile(new URI(NAME_NODE + "/tsa-table-conf.xml"), job.getConfiguration());
	
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		MultipleOutputs.addNamedOutput(job, "valid-records", TextOutputFormat.class, NullWritable.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "invalid-records", TextOutputFormat.class, NullWritable.class, Text.class);
		
		job.waitForCompletion(true);
		
		Counters counters = job.getCounters();
		System.out.printf("Valid Records: %d, Invalid Records: %d\n",
			      counters.findCounter(COUNTERS.VALID_RECORDS).getValue(),
			      counters.findCounter(COUNTERS.INVALID_RECORDS).getValue());
			 }
		}
	
