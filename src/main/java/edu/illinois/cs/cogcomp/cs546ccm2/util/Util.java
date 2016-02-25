package edu.illinois.cs.cogcomp.cs546ccm2.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.commons.lang.NullArgumentException;

/**
 * @author shashank
 */

public class Util {

	public static ArrayList<String> readLines(String fileName) {
		BufferedReader reader;
		try {
			reader = openReader(fileName);
		} catch (IOException e1) {
			System.out.println("Couldn't read file "+fileName);
			return null;
			//e1.printStackTrace();
		}
		String line;
		ArrayList<String> content = new ArrayList<String>();
		try {
			while ((line = reader.readLine()) != null) {
				content.add(line);
			}

			reader.close();

			return content;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static BufferedReader openReader(String fname) throws IOException {
		BufferedReader reader;


		InputStream is = ClassLoader.getSystemResourceAsStream( fname );

		if (is == null) {
			// try with a leading slash
			is = ClassLoader.getSystemResourceAsStream("/" + fname );

			if (is == null)
				is = new FileInputStream(fname);

		}

		reader = new BufferedReader(new InputStreamReader( is, "UTF-8" ));
		return reader;

	}
	
	public static float getAvg(float[] vals) {
		float avg = 0;
		if(vals==null) {
			throw new NullArgumentException("null argument is not supported");
		}
		
		if(vals.length == 0) {
			throw new ArrayIndexOutOfBoundsException("Array size is 0");
		}
		
		for (int i = 0; i < vals.length; i++) {
			avg += vals[i];
		}
		avg /= vals.length;
		return avg;
	}
	
	public static float getMax(float[] vals) {
		float max = Float.MIN_VALUE;
		if(vals==null) {
			throw new NullArgumentException("null argument is not supported");
		}
		
		if(vals.length == 0) {
			throw new ArrayIndexOutOfBoundsException("Array size is 0");
		}
		
		for (int i = 0; i < vals.length; i++) {
			if(vals[i] > max) {
				max = vals[i];
			}
		}
		return max;
	}
	
	public static float getMin(float[] vals) {
		float min = Float.MAX_VALUE;
		if(vals==null) {
			throw new NullArgumentException("null argument is not supported");
		}
		
		if(vals.length == 0) {
			throw new ArrayIndexOutOfBoundsException("Array size is 0");
		}
		
		for (int i = 0; i < vals.length; i++) {
			if(vals[i] < min) {
				min = vals[i];
			}
		}
		return min;
	}
	
	public static float getSD(float[] vals) {
		if(vals==null) {
			throw new NullArgumentException("null argument is not supported");
		}
		
		if(vals.length == 0) {
			throw new ArrayIndexOutOfBoundsException("Array size is 0");
		}
				
		float avg = getAvg(vals);
		float squaredDeviation = 0;
		for (int i = 0; i < vals.length; i++) {
			squaredDeviation += Math.pow((vals[i]-avg), 2);
		}
		
		float sd = ((float) Math.sqrt(squaredDeviation/(float)vals.length)); 
		return sd;
	}
	
}
