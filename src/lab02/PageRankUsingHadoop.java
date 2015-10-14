package lab02;

import java.io.IOException;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class PageRankUsingHadoop{

	/*
	 * MapperClass
	 */
	public static class MapperClass
			extends Mapper<Object,Text,IntWritable,DoubleWritable> {

		private IntWritable emitKey = new IntWritable();
		private DoubleWritable emitVal = new DoubleWritable();

		private int npoint = 0;
		private double[] pagerank = null;

		private void loadPageRank (Configuration config) {
			for (int i=0; i<npoint; i++) {
				pagerank[i] = config.getFloat ("pagerank" + i, 0);
			}
		}
			
		// get the parameters set by the main function (see the main function. we call "conf.set")
		//
		protected void setup(Context context) throws IOException, InterruptedException {
			Configuration config = context.getConfiguration();

			npoint = config.getInt ("npoint", -1);

			pagerank = new double[npoint];

			loadPageRank (config);
		}
		// map function (Object, Text : input key-value pair
		//               Context : fixed parameter)
		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {

			int myid = 0;
			int outdegree;
//10 1 2 3 4 
			String[] arr = value.toString().split ("\t");
			myid = Integer.parseInt (arr[0]);
			//arr[0] means nodeId
			outdegree = arr.length - 1;

			/**
			 * fill here
			 */ 
			// -------------------------------
			// 
			for(int i=1; i<arr.length; i++) {
				context.write(new IntWritable(Integer.parseInt(arr[i])), new DoubleWritable(pagerank[myid]/outdegree));
			}
			// ------------------
		}
	}
	

	/*
	 * ReducerClass
	 */
	public static class ReducerClass extends Reducer<IntWritable,DoubleWritable,IntWritable,DoubleWritable> {

		private DoubleWritable result = new DoubleWritable();
		private double damping = 0.85;
		private int npoint = 0;

		protected void setup(Context context) throws IOException, InterruptedException {
			Configuration config = context.getConfiguration();
			npoint = config.getInt ("npoint", 1);
		}


		public void reduce(IntWritable key, Iterable<DoubleWritable> values, Context context) 
				throws IOException, InterruptedException {

			double sum = 0.;
			double pagerank_val = 0;
			/**
			 * fill here
			 */
			// -------------------------------
			for(DoubleWritable list : values) {
				sum += list.get();				
			}
			pagerank_val = damping * sum + (1 - damping) * 1 / Math.abs(npoint);
			// -------------------------------
			// emit a key-value pair 
			result.set(pagerank_val);
			context.write(key,result);
		}
	}

	public static int npoint;

	public static void initPageRank (Configuration conf) {
		for (int i=0; i<npoint; i++) {
			conf.setFloat ("pagerank" + i, (float)(1. / (double)npoint));
		}
	}

	public static void broadcastPageRank (Configuration conf, Path outdir) throws Exception {
		FileSystem fs = outdir.getFileSystem (conf);
		FSDataInputStream fp = fs.open (new Path (outdir + "/part-r-00000"));
		String line = null;
		while ( (line = fp.readLine ()) != null ) {
			String[] arr = line.split("\t");
			int id = Integer.parseInt (arr[0]);
			double val = Double.parseDouble (arr[1]);

			conf.setFloat ("pagerank" + id, (float)val);
		}
	}

	public static void main(String[] args) throws Exception {
		
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf,args).getRemainingArgs();
		if ( otherArgs.length != 3 ) {
			System.err.println("Usage: pagerank <npoint> <in> <out>");
			System.exit(1);
		}
		npoint = Integer.parseInt(otherArgs[0]);

		conf.setInt ("npoint", npoint);

		// maximum iteration
		int maxIter = 10;

		initPageRank (conf);

		for (int itr=0; itr<maxIter; itr++) {
			Job job = new Job(conf,"PageRankUsingHadoop");
			job.setJarByClass(PageRankUsingHadoop.class);

			// let hadoop know the map and reduce classes
			job.setMapperClass(MapperClass.class);
			job.setReducerClass(ReducerClass.class);

			// let hadoop know the key-value pair type
			job.setOutputKeyClass(IntWritable.class);
			job.setOutputValueClass(DoubleWritable.class);

			// set number of reduce functions (let's use 1 in the example, do not change!!)
			job.setNumReduceTasks(1);

			Path outdir = new Path (otherArgs[2]);
			if (FileSystem.get(conf).exists(outdir)) {
				FileSystem.get(conf).delete (outdir);
			}

			// read the output of reduce function to obtain the updated cluster centers
			// the updated cluster centers are broadcasted again
			FileInputFormat.addInputPath(job,new Path(otherArgs[1]));
			FileOutputFormat.setOutputPath(job,new Path(otherArgs[2]));
			job.waitForCompletion(true);

			// broadcast pagerank values for the next step
			broadcastPageRank (conf, outdir);
		}
	}
}
