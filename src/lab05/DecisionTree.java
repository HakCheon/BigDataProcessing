package lab05;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;


public class DecisionTree {
	final static int CATEGORY_NUM = 4;
	final static String[] OUTLOOK= {"Sunny","Overcast","Rain"};
	final static String[] TEMPERATURE = {"Hot, Mile, Cool"};
	final static String[] HUMIDITY = {"High","Weak","Normal"};
	final static String[] Windy = {"Yes", "No"};
	
	public static void main(String[] args) {
		try {
			ArrayList<String[]> datas;
			Node root = new Node();
			String line;
//			int total;
//			String[] datas;
			int[][] counts = new int[CATEGORY_NUM][2];
			BufferedReader reader = new BufferedReader(new FileReader("Sample/play-tennis_train.txt"));
			
			datas = new ArrayList<String[]>();
			
//			for(int i=0; i<2; i++) 
//				for(int j=0; j<CATEGORY_NUM; j++) counts[i][j] = 0; 
			
			
			while((line = reader.readLine()) != null) datas.add(line.split(","));
			
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
