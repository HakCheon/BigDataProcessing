package common;

import org.apache.hadoop.util.ProgramDriver;

public class Driver {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int exitCode= -1;
		ProgramDriver pgd = new ProgramDriver();
		try {
		    pgd.addClass("wordcount", lab01.WordCount.class," A map/recude program that counts the work");
		    pgd.addClass("pagerank", lab02.PageRankUsingHadoop.class," A map/recude program that calculate page rank");
		    pgd.addClass("matrix", lab03.MatrixMultiply.class," A map/recude program that calculate page rank");
		    pgd.driver(args);
			exitCode=0;
		}
		catch(Throwable e) {
		    e.printStackTrace();
		}
		System.exit(exitCode);
	}
}
