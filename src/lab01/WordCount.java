package lab01;

import java.io.IOException;
import java.util.StringTokenizer;

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


public class WordCount {

	  public static class TokenizerMapper 
	       extends Mapper<Object, Text, Text, IntWritable>{
	    
	    private final static IntWritable one = new IntWritable(1);
	    private Text word = new Text();
	      
	    public void map(Object key, Text value, Context context
	                    ) throws IOException, InterruptedException {
	      StringTokenizer itr = new StringTokenizer(value.toString());
	      
	      int i=0;
	      String bi1 = new String("");
	      String bi2 = new String("");
	      while (itr.hasMoreTokens()) {
	    	bi1 = bi2;
	    	bi2 = itr.nextToken();
	    	
	    	if (i<1) i++;
	    	else {
		        word.set(bi1+" "+bi2);
		        context.write(word, one);
	    	}
	      }
	    }
	  }
	  
	  public static class IntSumReducer 
      extends Reducer<Text,IntWritable,Text,IntWritable> {
   private IntWritable result = new IntWritable();

   public void reduce(Text key, Iterable<IntWritable> values, 
                      Context context
                      ) throws IOException, InterruptedException {
     int sum = 0;
     for (IntWritable val : values) {
       sum += val.get();
     }
     result.set(sum);
     context.write(key, result);
   }
 }
	  
	  public static void main(String[] args) throws Exception {
		    Configuration conf = new Configuration();	// job 수행하기 위한 설정 초기화
		    String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		    if (otherArgs.length != 2) {
		      System.err.println("Usage: wordcount <in> <out>");
		      System.exit(2);
		    }
		    Job job = new Job(conf, "word count");	// job 작성, 따옴표안은 설명을 쓰면됨(상관없음)
		    job.setJarByClass(WordCount.class);	// job을 수행할 class 선언, 파일명.class, 대소문자주의
		    job.setMapperClass(TokenizerMapper.class);	// Map class 선언, 위에서 작성한 class명
		    
		    job.setReducerClass(IntSumReducer.class);		// Reduce class 선언
		    job.setOutputKeyClass(Text.class);		// Output key type 선언
		    job.setOutputValueClass(IntWritable.class);	                 // Output value type 선언
		    //job.setMapOutputKeyClass(Text.class);		// Map은 Output key type이 다르다면 선언
		    //job.setMapOutputValueClass(IntWritable.class);	// Map은 Output value type이 다르다면 선언
		    job.setNumReduceTasks(1);			// 동시에 수행되는 reduce개수
		    FileInputFormat.addInputPath(job, new Path(otherArgs[0]));	// 입력 데이터가 있는 path
		    FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));	// 결과를 출력할 path
		    System.exit(job.waitForCompletion(true) ? 0 : 1);		// 실행
		  }
	  
}