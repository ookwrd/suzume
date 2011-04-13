package model;

import java.util.ArrayList;
import java.util.Hashtable;

import tools.Clustering;

public class SimpleClustering implements Clustering {

	private static final double[] NML_CENTERS = { 11.5, 10.75, 10.25, 9.5, 8.5, 7.5};
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
			
			int prevStateIndex = 0; 
			double prevStateCenter = centers[0];
				
			for (Double value : arrayList) {
				 
				double s = DEFAULT_SIGMA;
				int stateIndex = prevStateIndex; // if distance not enough from previous center, the previous state will be copied
				
				if ( Math.abs(value-prevStateCenter) > 0.25 ) { // if large distance from the previous center, look up where we are
					
					if (centers[0] + s > value && value > centers[0] - s)
						stateIndex = 0;
					else if (centers[1] + s > value && value > centers[1] - s)
						stateIndex = 1;
					else if (centers[2] + s > value && value > centers[2] - s)
						stateIndex = 2;
					else if (centers[3] + s > value && value > centers[3] - s)
						stateIndex = 3;
					else if (centers[4] + s > value && value > centers[4] - s)
						stateIndex = 4;
					else if (centers[5] + s > value && value > centers[5] - s)
						stateIndex = 5;
				}
				
				prevStateCenter = centers[stateIndex];
				prevStateIndex = stateIndex;
				results.put(value, stateIndex);
			}
		}
		return results;
	}

	public static void main(String[] args) {
		//int r = 47 / 10;
		//System.out.println("result=" + r);
		ModelController.main(null);
	}
}
