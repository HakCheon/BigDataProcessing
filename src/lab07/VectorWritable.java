package lab07;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

public class VectorWritable implements WritableComparable<VectorWritable>
{
	public double[] vec= new double[3]; 
	
	public String toString()
	{
		return getVector();
	}
	public String getVector() { return ""+vec[0] + "\t" + vec[1] + "\t" + vec[2]; } 

	@Override
	public void readFields(DataInput arg0) throws IOException {
		vec[0]=arg0.readDouble();
		vec[1]=arg0.readDouble();
		vec[2]=arg0.readDouble();
	}
	@Override
	public void write(DataOutput arg0) throws IOException {
		// TODO Auto-generated method stub
	arg0.writeDouble(vec[0]);
	arg0.writeDouble(vec[1]);
	arg0.writeDouble(vec[2]);
	
	}
	@Override
	public int compareTo(VectorWritable arg0) {
		// TODO Auto-generated method stub
		//1,2,3 2,3,4
		for(int i=0 ; i< vec.length ; i++)
		{
			if(vec[i] > arg0.vec[i] )
			{
				return 1;
			}
			else if (vec[i] < arg0.vec[i])
			{
				return -1;
			}
			
		}		return 0;
	}

	
}

