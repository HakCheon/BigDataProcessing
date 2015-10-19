package lab04_2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class Car {
	public static void main(String[] args) {
		double acc=0, unacc=0, good=0, vgood=0, total;
		String line;
		BufferedReader reader;
		BufferedWriter out;
		HashMap<String, Double[]> counts = new HashMap<String, Double[]>();
		try {
			reader = new BufferedReader(new FileReader("Sample/car_train.txt"));
			while((line = reader.readLine()) != null) {
				String[] arr = line.split(",");
				
				if(arr[6].equals("acc")) {
					acc++;
					for(int i=0; i<arr.length-1; i++) {
						if(counts.containsKey(arr[i])){
							counts.get(arr[i])[0] += 1;
						}
						else {
							Double[] tmp = {1.0,0.0,0.0,0.0};
							counts.put(arr[i],tmp);
						}
					}
				}
				else if(arr[6].equals("unacc")) {
					unacc++;
					for(int i=0; i<arr.length-1; i++) {
						if(counts.containsKey(arr[i])){
							counts.get(arr[i])[1] += 1;
						}
						else {
							Double[] tmp = {0.0,1.0,0.0,0.0};
							counts.put(arr[i],tmp);
						}
					}
				}
				else if(arr[6].equals("good")) {
					good++;
					for(int i=0; i<arr.length-1; i++) {
						if(counts.containsKey(arr[i])){
							counts.get(arr[i])[2] += 1;
						}
						else {
							Double[] tmp = {0.0,0.0,1.0,0.0};
							counts.put(arr[i],tmp);
						}
					}
				}
				else if(arr[6].equals("vgood")) {
					vgood++;
					for(int i=0; i<arr.length-1; i++) {
						if(counts.containsKey(arr[i])){
							counts.get(arr[i])[3] += 1;
						}
						else {
							Double[] tmp = {0.0,0.0,0.0,1.0};
							counts.put(arr[i],tmp);
						}
					}
				}
			}
			
			for(String key: counts.keySet()) {
				counts.get(key)[0] /= acc;
				counts.get(key)[1] /= unacc;
				counts.get(key)[2] /= good;
				counts.get(key)[3] /= vgood;
			}
			total = acc + unacc + good + vgood;
			acc = acc/total;
			unacc = unacc/total;
			good = good/total;
			vgood = vgood/total;
			
			
			out = new BufferedWriter(new FileWriter("Result.txt"));
			reader = new BufferedReader(new FileReader("Sample/car_test.txt"));
			
			for(int j=1; (line = reader.readLine()) != null; j++) {
				double acc_value = 1, unacc_value = 1, good_value = 1, vgood_value = 1;
				String arr[] = line.split(",");
				for(int i=0; i<arr.length; i++) {
					acc_value *= counts.get(arr[i])[0];
					unacc_value *= counts.get(arr[i])[1];
					good_value *= counts.get(arr[i])[2];
					vgood_value *= counts.get(arr[i])[3];
//					System.out.print(arr[i]+",");
					out.write(arr[i]+",");
				}
				acc_value *= acc;
				unacc_value *= unacc;
				good_value *= good;
				vgood_value *= vgood;
				
//				System.out.println("Case " + j);
//				System.out.println("acc = "+acc_value+"\tunacc = "+unacc_value+"\tgood = "+good_value+"\tvgood= "+vgood_value);
				double[] tmp = {acc_value, unacc_value,good_value,vgood_value};
				double max=0;
				int max_index = 0;
				for(int i=0; i<tmp.length;i++) {
					if(tmp[i] > max) {
						max = tmp[i];
						max_index = i;
					}
				}
				
				if(max_index == 0) out.write("acc");
				else if(max_index == 1) out.write("unacc");
				else if(max_index == 2) out.write("good");
				else out.write("vgood");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
