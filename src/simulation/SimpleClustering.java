package simulation;

import java.util.ArrayList;
import java.util.Hashtable;

import tools.Clustering;

public class SimpleClustering extends Clustering {

	//public static final double[] GAP_CENTERS = { 11.5, 10.75, 10.25, 9.5, 8.5, 7.5};
	//private final double[] DEFAULT_THRESHOLDS = { 12, 11.75, 10.5, 10.25, 9.5,
	//		8.5, 7.5 }; // must be in decreasing order
	public static final double[] DEFAULT_CENTERS = { 12, 11, /*10.5, */10, 9, 8 }; //TODO check state transition diagram : est-ce que tout va bien apres avoir supprime le 10.5
	//public static final double[] DEFAULT_CENTERS = { 11.5, 10.5, 9.5, 8.5 }; // "gap centers"
	public static final double DEFAULT_SIGMA = 0.1;
	
	public double[] centers;
	
	public SimpleClustering(ArrayList<Double>[] array) {
		super(array);
		this.centers = DEFAULT_CENTERS;
		this.array = array; // TODO
	}
	
	// index 0 corresponds to a gap 
	public static double getCenter(int index) {
		if (index==0) return -1.0;
		else return DEFAULT_CENTERS[index-1];
	}
	
	public void findMarkov() {
		super.findMarkov();
		
		appendConsole("Clustering centers: ");
		for(int i = 0; i < SimpleClustering.DEFAULT_CENTERS.length; i++) {
			appendConsole((i+1)+":"+SimpleClustering.DEFAULT_CENTERS[i]+" ");
		}
		appendConsole("\n");
	}
	
	/**
	 * Cluster into predetermined classes using simple threshold values
	 * 
	 * @param array
	 * @return hashtable representing the binding between values and their
	 *         corresponding classes
	 */
	public Hashtable<Double, Integer> cluster(ArrayList<Double>[] array) {
		
		Hashtable<Double, Integer> results = new Hashtable<Double, Integer>();
		for (ArrayList<Double> arrayList : array) {
			/*
			 * for (Double value : arrayList) { for (int i=1;
			 * i<thresholds.length; i++) { if (thresholds[i-1] > value && value
			 * >= thresholds[i]) clusteringConsole.put(value, i-1); } if
			 * (!clusteringConsole.containsKey(value))
			 * System.out.println("not classified: "+value); }
			 */
			
			int prevStateIndex = 0; 
			//double prevStateCenter = centers[0];
			
			for (Double value : arrayList) {
				 
				double s = DEFAULT_SIGMA;
				//int stateIndex = prevStateIndex; // if distance not enough from previous center, the previous state will be copied
				//if ( Math.abs(value-prevStateCenter) > 0.25 ) { // if large distance from the previous center, look up where we are
					
					if (centers[0] + s > value && value > centers[0] - s)
						results.put(value, 1);
					else if (centers[1] + s > value && value > centers[1] - s)
						results.put(value, 2);
					else if (centers[2] + s > value && value > centers[2] - s)
						results.put(value, 3);
					else if (centers[3] + s > value && value > centers[3] - s)
						results.put(value, 4);
					else if (centers[4] + s > value && value > centers[4] - s)
						results.put(value, 5);
					else results.put(value, 0);
				//}
				
				//prevStateCenter = centers[stateIndex];
				//prevStateIndex = stateIndex;
				
			}
		}
		return results;
	}
}
