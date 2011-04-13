package model;

import java.util.ArrayList;
import java.util.Hashtable;

import tools.Clustering;

public class SimpleClustering implements Clustering {

	//private final double[] DEFAULT_THRESHOLDS = { 12, 11.75, 10.5, 10.25, 9.5,
	//		8.5, 7.5 }; // must be in decreasing order
	private final double[] DEFAULT_CENTERS = { 12, 11, 10.5, 10, 9, 8 };
	private final double DEFAULT_SIGMA = 0.2;

	@Override
	public Hashtable<Double, Integer> cluster(ArrayList<Double>[] array) {
		return cluster(array, DEFAULT_CENTERS);
	}

	/**
	 * Cluster into predetermined classes using simple threshold values
	 * 
	 * @param array
	 * @param thresholds
	 * @return hashtable representing the binding between values and their
	 *         corresponding classes
	 */
	public Hashtable<Double, Integer> cluster(ArrayList<Double>[] array,
			double[] centers) {
		if (centers == null)
			centers = DEFAULT_CENTERS;
		Hashtable<Double, Integer> results = new Hashtable<Double, Integer>();
		for (ArrayList<Double> arrayList : array) {
			/*
			 * for (Double value : arrayList) { for (int i=1;
			 * i<thresholds.length; i++) { if (thresholds[i-1] > value && value
			 * >= thresholds[i]) results.put(value, i-1); } if
			 * (!results.containsKey(value))
			 * System.out.println("not classified: "+value); }
			 */
			
			int prevState = 0; 
			for (Double value : arrayList) {
				double[] c = DEFAULT_CENTERS;
				double s = DEFAULT_SIGMA;
				int state = prevState;
				
				if (c[0] + s > value && value > c[0] - s)
					state = 0;
				else if (c[1] + s > value && value > c[1] - s)
					state = 1;
				else if (c[2] + s > value && value > c[2] - s)
					state = 2;
				else if (c[3] + s > value && value > c[3] - s)
					state = 3;
				else if (c[4] + s > value && value > c[4] - s)
					state = 4;
				else if (c[5] + s > value && value > c[5] - s)
					state = 5;
				
				results.put(value, state);
			}
		}
		return results;
	}

	public static void main(String[] args) {
		int r = 47 / 10;
		System.out.println("result=" + r);
	}
}
