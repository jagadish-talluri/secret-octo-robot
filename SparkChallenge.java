package main.java.com.oreilly.learningsparkexamples.java;

import java.util.Arrays;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

public class DuplicateTest {
	  public static void main(String[] args) throws Exception {
			String master = args[0];
			JavaSparkContext sc = new JavaSparkContext(master, "DuplicateTest", System.getenv("SPARK_HOME"), System.getenv("JARS"));
			
			int[] index = {0,3,2};
			String delimiter = ",";

			
	    JavaRDD<String> rdd = sc.textFile(args[1]); // input file
	    
	    JavaPairRDD<String, String> mapPairRdd = rdd.mapToPair(
	    		  new PairFunction<String, String, String>() {
	    			    public Tuple2<String, String> call(String x) { 
	    			    	
	    					String[] splits = x.split(delimiter);
	    					StringBuffer sb = new StringBuffer("");
	    					
	    					for (int i : index){
	    						sb.append(splits[i]);
	    					}
	
	    			    	return new Tuple2(sb, x); 
	    			    }
	    			});
	    
//	    counts.saveAsTextFile(args[2]);
		}

}
