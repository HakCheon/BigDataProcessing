package lab04;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

public class PlayTenis {
	public static void main(String[] args) {
		double yes=0, no=0, total;
		String line;
		BufferedReader reader;
		BufferedWriter out;
		HashMap<String, Double[]> counts = new HashMap<String, Double[]>();
		try {
			reader = new BufferedReader(new FileReader("Sample/play-tennis_train.txt"));
			while((line = reader.readLine()) != null) {
				String[] arr = line.split(",");
				
				if(arr[4].equals("Yes")) {
					yes++;
					for(int i=0; i<arr.length-1; i++) {
						if(counts.containsKey(arr[i])){
							counts.get(arr[i])[0] += 1;
						}
						else {
							Double[] tmp = {1.0,0.0};
							counts.put(arr[i],tmp);
						}
					}
				}
				else if(arr[4].equals("No")) {
					no++;
					for(int i=0; i<arr.length-1; i++) {
						if(counts.containsKey(arr[i])){
							counts.get(arr[i])[1] += 1;
						}
						else {
							Double[] tmp = {0.0,1.0};
							counts.put(arr[i],tmp);
						}
					}
				}
			}
			
			for(String key: counts.keySet()) {
				counts.get(key)[0] /= yes;
				counts.get(key)[1] /= no;
			}
			total = yes + no;
			yes = yes/total;
			no = no/total;
			
			
			out = new BufferedWriter(new FileWriter("Result.txt"));
			reader = new BufferedReader(new FileReader("Sample/play-tennis_test.txt"));
			
			for(int j=1; (line = reader.readLine()) != null; j++) {
				double yes_value = 1, no_value = 1;
				String arr[] = line.split(",");
				for(int i=0; i<arr.length; i++) {
					yes_value *= counts.get(arr[i])[0];
					no_value *= counts.get(arr[i])[1];
					out.write(arr[i]);
				}
				yes_value *= yes;
				no_value *= no;
				
				if(yes_value>no_value) out.write("Yes");
				else out.write("No");
				out.newLine();
				
				System.out.println("Case " + j);
				System.out.println("Yes = "+yes_value+"\tNo = "+no_value);
				if(yes_value>no_value) System.out.println("PLAY TENNIS!!");
				else System.out.println("DON'T PLAY TENNIS!!");
				System.out.println();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
