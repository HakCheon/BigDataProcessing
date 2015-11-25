package lab05;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;


public class DecisionTree {
	final static int CATEGORY_NUM = 4;
	final static String[] OUTLOOK= {"Sunny","Overcast","Rain"};
	final static String[] TEMPERATURE = {"Hot, Mild, Cool"};
	final static String[] HUMIDITY = {"High","Normal"};
	final static String[] Windy = {"Weak", "Strong"};
	
	public static void main(String[] args) {
		try {
			ArrayList<String[]> datas;
			HashMap<String, Integer> map = new HashMap<String,Integer>();
			Double[] entropy = new Double[4];
			Node root = new Node();
			String line;
			int total = 0;
//			String[] datas;
			int[][] counts = new int[CATEGORY_NUM][2];
			BufferedReader reader = new BufferedReader(new FileReader("Sample/play-tennis_train.txt"));
			
			datas = new ArrayList<String[]>();
			
//			for(int i=0; i<2; i++) 
//				for(int j=0; j<CATEGORY_NUM; j++) counts[i][j] = 0; 
			
			
			while((line = reader.readLine()) != null) {
				datas.add(line.split(","));
				total++;
			}
			
			for(int i=0; i<4; i++) {
				entropy[i] = 0.0;
				for(String[] data:datas) {
					if(map.containsKey(data[i])) map.put(data[i], map.get(data[i]) + 1);
					else	map.put(data[i], 1);
				}
				
				for(String k : map.keySet()) {
					double sum = map.get(k);
					System.out.println(sum);
					double p = map.get(k)/total;
					int yes=0, no=0;
					for(String[] data : datas) {
						if(data[i].equals(k)) {
							if(data[4].equals("Yes")) yes++;
							else no++;
						}
					}
					double h = -((yes/sum) * Math.log(yes/sum)/Math.log(2.0)) - ((no/sum) * Math.log(no/sum)/Math.log(2.0));
//					System.out.println(h);
					entropy[i] += p*h;
				}
//				System.out.println(entropy[i]);
				
			}
			
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
