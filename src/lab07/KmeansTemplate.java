package lab07;

import java.io.BufferedReader;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Random;

import org.apache.hadoop.fs.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class KmeansTemplate {

	/*
	 * MapperClass
	 *
	 * parameters:
	 *   Object, Text : input key-value pair type (always same - get a line of input file)
	 *   IntWritable, Text : output key-value pair type
	 *       IntWriteable --> the closest cluster center number
	 *       Text : vector (separated by tab)
	 */
		public static class MapperClass extends Mapper<Object,Text,IntWritable,VectorWritable> {

		private final int d = 3; //d means Dimension of Vector
		private IntWritable emitKey = new IntWritable();
		private VectorWritable emitVal = new VectorWritable();

		// IMPORTANT: the number of clusters
		private int mK;

		// IMPORTANT: the vectors for cluster centers
		private double[][] centers =  null;
		private double[] point = null;
			//centers have config centerols
		// get the parameters set by the main function (see the main function. we call "conf.set")
		//
		protected void setup(Context context) throws IOException, InterruptedException {
		
			Configuration config = context.getConfiguration();
			
			mK=config.getInt("k", 0);
			centers= new double[mK][d]; 
			// mK Clusters has 3 Vectors  ex) x,y,z
			/*
			 * fill here
			 */
			for(int i=0; i < mK; i++) {
				String[] tmp = config.get("strCenters."+i).split("\t");
				for(int j=0; j<d; j++) {
					centers[i][j] = Double.parseDouble(tmp[j]);
				}
			}
			
			
		}
		// map function (Object, Text : input key-value pair
		//               Context : fixed parameter)
		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
				
			double mindist = Double.MAX_VALUE, dist;
			int which_center = 0;
			/*
			 * 
			 * fill here
			 */
			String[] tmp = value.toString().split("\t");
			for(int i = 0; i<mK; i++) {
				double sum = 0;
				for(int j=0; j<d; j++) sum += Math.pow(Double.parseDouble(tmp[j])-centers[i][j], 2.0);
				dist = Math.sqrt(sum);
				if(sum < mindist) {
					which_center = i;
					mindist = sum;
				}
			}
			emitKey.set(which_center);
			for(int i=0; i<d; i++) emitVal.vec[i] = Double.parseDouble(tmp[i]);
			context.write(emitKey, emitVal);
			// emit a key-value pair 
			//   emitKey : cluster id that the input point belongs to
			//   value : the input point
			
			
		}
	}
	

	/*
	 * ReducerClass
	 *
	 * parameters:
	 *   IntWritable, Text : input key-value pair type which is the same with the output key-value pair
	 *                       type of MapperClass
	 *   IntWritable, Text : output key-value pair type
	 *      IntWritable --> the cluster number
	 *      Text --> the updated vector
	 */
	public static class ReducerClass
			extends Reducer<IntWritable,VectorWritable,IntWritable,VectorWritable> {

		private VectorWritable result = new VectorWritable();
		private int d = 3;

		public void reduce(IntWritable key, Iterable<VectorWritable> values, Context context) 
				throws IOException, InterruptedException {
			/*
			 * 
			 * fill here
			 */
			int i=0;
			double x=0,y=0,z=0;
			for(VectorWritable value : values) {
				i++;
				String[] data = value.getVector().split("\t");
				x += Double.parseDouble(data[0]);
				y += Double.parseDouble(data[1]);
				z += Double.parseDouble(data[2]);
			}
			x /= i;
			y /= i;
			z /= i;
			
			result.vec[0] = x;
			result.vec[1] = x;
			result.vec[2] = x;
			
			context.write(key,result);
		    // emit a key-value pair 
			}
	}


	/*
	 * return the square of the Euclidean distance between two points
	 * ( we do not compute the square root )
	 */
	public static double computeDistance (double[] arr1, double[] arr2, int d) {
		if (arr1.length < d || arr2.length < d)
			return -1;

		double sum = 0;
		for (int i=0; i<d; i++) {
			sum += (arr1[i] - arr2[i]) * (arr1[i] - arr2[i]);
		}
		return sum;
	}
	/*
	 * convert a double type array to a string
	 */
	public static String doubleArrayToString (double[] arr, int d) {
		String str = "";
		if (d == 0) return str;

		str += arr[0];
		for (int i=1; i<d; i++) {
			str += "\t" + arr[i];
		}
		return str;
	}
	
	public static void main(String[] args) throws Exception {
		
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf,args).getRemainingArgs();
		if ( otherArgs.length != 4 ) {
			System.err.println("Usage: <in> <out> <# of dimensions> <K>");
			System.exit(2);
		}
		// maximum iteration
		int maxIter = 10;

		// dimension
		int d = Integer.parseInt(otherArgs[2]);
		int k = Integer.parseInt(otherArgs[3]);

		// initial centers
		// broadcasted to the map functions by using "conf.set"
		// infor Inittial Centers to Map
		Random rand = new Random();
		conf.setInt("k", k);
		
		for (int i=0; i<k; i++) {
			String str = "";
			double v = rand.nextDouble() * 100;
			
			str = "" + v;
			for (int j=1; j<d; j++) {
				v = rand.nextDouble() * 100;
				str += "\t" + v;
			}
			conf.set("strCenters." + i, str); // String key , String value
			
		}
		double[] center=new double [d];

		for (int itr=0; itr<maxIter; itr++) {
			Job job = new Job(conf,"k-means clustering");
			job.setJarByClass(KmeansTemplate.class);

			// let hadoop know the map and reduce classes
			job.setMapperClass(MapperClass.class);
			job.setReducerClass(ReducerClass.class);

			// let hadoop know the key-value pair type
			job.setOutputKeyClass(IntWritable.class);
			job.setOutputValueClass(VectorWritable.class);

			// set number of reduce functions (let's use 1 in the example, do not change!!)
			job.setNumReduceTasks(1);

			Path outdir = new Path (otherArgs[1]);
			if (FileSystem.get(conf).exists(outdir)) {
				FileSystem.get(conf).delete (outdir, true);
			}
			// read the output of reduce function to obtain the updated cluster centers
			// the updated cluster centers are broadcasted again
			FileInputFormat.addInputPath(job,new Path(otherArgs[0]));
			FileOutputFormat.setOutputPath(job,new Path(otherArgs[1]));
			job.waitForCompletion(true);

			FileSystem fs = outdir.getFileSystem (conf);
			FSDataInputStream fp = fs.open (new Path (outdir + "/part-r-00000"));
			BufferedReader reader = new BufferedReader(new InputStreamReader(fp));
			
			String line = null;
			while ( (line = reader.readLine ()) != null ) {
				String[] arr = line.split("\t");
				int centerId = Integer.parseInt (arr[0]);
				for (int i=1; i<=d; i++)
					center[i-1] = Double.parseDouble (arr[i]);
				
				conf.set ("strCenters." + centerId, doubleArrayToString (center, d));
			}
			reader.close();
			fp.close();
		}
	}


	}
