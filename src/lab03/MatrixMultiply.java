package lab03;

import java.io.IOException;
import java.util.HashMap;

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


public class MatrixMultiply {
	public static int m=100,n=100;
	
	  public static class MapperClass 
	       extends Mapper<Object, Text, Text, Text>{
	     
	    public void map(Object key, Text value, Context context
	                    ) throws IOException, InterruptedException {
	    	if(!value.equals("")){
				String[] arr = value.toString().split ("\t");
				
				if(arr.length > 0) {
					if(arr[0].equals("A")) {
						for( int k=0; k<n; k++) 
							context.write(new Text(arr[1] + "," + k), new Text(arr[2] + "," + arr[3]));
					}
					else if(arr[0].equals("B")) {
						for( int k=0; k<m; k++) 
							context.write(new Text(k + "," + arr[2]), new Text(arr[1] + "," + arr[3]));
					}
				}
	    	}
	    }
	  }
	  
	  public static class ReducerClass 
      extends Reducer<Text,Text,Text,IntWritable> {
	
	   public void reduce(Text key, Iterable<Text> values, 
	                      Context context
	                      ) throws IOException, InterruptedException {
		   int sum=0;
		   HashMap<String, Integer> map = new HashMap<String, Integer>();
		     for (Text val : values) {
		    	 String[] arr = val.toString().split(",");
		    	 if(map.containsKey(arr[0])) {
		    		 map.put(arr[0], map.get(arr[0]) * Integer.parseInt(arr[1]));
		    	 }
		    	 else {
		    		 map.put(arr[0],Integer.parseInt(arr[1]));
		    	 }
		     }
		     
		     for(int value: map.values()) {
		    	 sum += value;
		     }
		     context.write(key, new IntWritable(sum));
	   }
	   
	 }
	  
	  public static void main(String[] args) throws Exception {
		    Configuration conf = new Configuration();	// job 수행하기 위한 설정 초기화
		    String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		    if (otherArgs.length != 2) {
		      System.err.println("Usage: MatrixMultiply <in> <out>");
		      System.exit(2);
		    }
		    Job job = new Job(conf, "Matrix Multiply");	// job 작성, 따옴표안은 설명을 쓰면됨(상관없음)
		    job.setJarByClass(MatrixMultiply.class);	// job을 수행할 class 선언, 파일명.class, 대소문자주의
		    job.setMapperClass(MapperClass.class);	// Map class 선언, 위에서 작성한 class명
		    
		    job.setReducerClass(ReducerClass.class);		// Reduce class 선언
		    job.setOutputKeyClass(Text.class);		// Output key type 선언
		    job.setOutputValueClass(IntWritable.class);	                 // Output value type 선언
		    job.setMapOutputKeyClass(Text.class);		// Map은 Output key type이 다르다면 선언
		    job.setMapOutputValueClass(Text.class);	// Map은 Output value type이 다르다면 선언
		    job.setNumReduceTasks(1);			// 동시에 수행되는 reduce개수
		    FileInputFormat.addInputPath(job, new Path(otherArgs[0]));	// 입력 데이터가 있는 path
		    FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));	// 결과를 출력할 path
		    System.exit(job.waitForCompletion(true) ? 0 : 1);		// 실행
		  }
	  
}