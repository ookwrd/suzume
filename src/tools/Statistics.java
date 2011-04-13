package tools;

import java.util.ArrayList;
import java.util.Collections;
import tools.Pair;

public class Statistics {
	

	public static final int NUMBER_DENSITY_BUCKETS = 100;

	/**
	 * Convenience  method for printing an arraylist of pairs as a vertical list.
	 * 
	 * @param <E>
	 * @param <J>
	 * @param series
	 */
	public static <E extends Comparable<E>, J> void printPairList(ArrayList<Pair<E, J>> series){
		
		System.out.println("Printing Pair list:");		
		for(Pair<E, J> pair : series){
			System.out.println(pair);
		}
		
	}

	/**
	 * Find the set of keys that correspond to local minima in the input data 
	 * 
	 * @param series
	 * @return
	 */
	public static ArrayList<Double> findLocalMinima(ArrayList<Pair<Double, Integer>> series){
		
		ArrayList<Double> retVal = new ArrayList<Double>();
		Collections.sort(series);
		
		for(int i = 1; i < series.size()-1; i++){	
			Pair<Double, Integer> pair =series.get(i);
			if(pair.second < series.get(i-1).second && 
					pair.second < series.get(i+1).second){
				retVal.add(pair.first);
			}	
		}
		
		return retVal;
	}

	/**
	 * Calculates moving average of data set for all points extending to a distance of windowRadius around the centre point.
	 * 
	 * @param series
	 * @param windowRadius
	 * @return
	 */
	public static ArrayList<Pair<Double, Double>> movingAverageSmoothing(ArrayList<Pair<Double, Integer>> series, int windowRadius){
		
		ArrayList<Pair<Double, Double>> retVal = new ArrayList<Pair<Double,Double>>();
		Collections.sort(series);
		
		double count = 0;
	
		for(int i = windowRadius; i < series.size() - windowRadius; i++){
				
			for(int j = 0; j < windowRadius; j++){
				count += series.get(i-j).second;
				count += series.get(i+j).second;
			}
				
			count += series.get(i).second;
			
			retVal.add(new Pair<Double, Double>(series.get(i).first, (count/(windowRadius*2+1))));
		}
		
		return retVal;
		
	}

	/**
	 * Turn the discrete data points in inputArray into a set of value countPairs.
	 * 
	 * @param inputArray
	 */
	public static ArrayList<Pair<Double, Integer>> calculateDensity(ArrayList<Double> inputArray) {
	
		// find range
		Double min = Double.MAX_VALUE;
		Double max = Double.MIN_VALUE;
			for (int j = 0; j < inputArray.size(); j++) {
			Double value = inputArray.get(j);
			if (value > max) {
				max = value;
			}
			if(value < min){
				min = value;
			}
		}
			
		double range = max - min;
		double step = range/NUMBER_DENSITY_BUCKETS;
		ArrayList<Integer> numOccurrences = new ArrayList<Integer>();
	
		for(int i = 0; i < NUMBER_DENSITY_BUCKETS+1; i++){//TODO why the plus 1?
			numOccurrences.add(0);
		}
		
		for(Double value : inputArray){
			int index = (int)((value-min)/step);
			numOccurrences.set(index, numOccurrences.get(index)+1);
		}
		
		ArrayList<Pair<Double, Integer>> retVal = new ArrayList<Pair<Double,Integer>>();
		
		for(int i = 0; i < numOccurrences.size(); i++){
			double reconstruct = min + i * step;
			retVal.add(new Pair<Double, Integer>(reconstruct, numOccurrences.get(i)));
		}
		
		return retVal;
	}

	
	/**
	 * Internally statistics are handled as time ordered arraylists, this method allows the user to 
	 * take a subsection of an array to focus on a section of interest.
	 * 
	 * @param arrays
	 * @param start
	 * @param finish
	 * @return
	 */
	public static <E> ArrayList<E> trimArrayList(ArrayList<E> array, int start, int finish){
		ArrayList<E>[] wrapper = new ArrayList[1];
		wrapper[0] = array;
		return trimArrayLists(wrapper, start, finish)[0];
	}
		
	/**
	 * Internally statistics are handled as time ordered arraylists, this method allows the user to 
	 * take a subsection of these arrays to focus on a section of interest.
	 * 
	 * @param arrays
	 * @param start
	 * @param finish
	 * @return
	 */
	public static <E> ArrayList<E>[] trimArrayLists(ArrayList<E>[] arrays, int start, int finish){
	
		@SuppressWarnings("unchecked")
		ArrayList<E>[] outputArrays = new ArrayList[arrays.length];
	
		for(int i = 0; i < arrays.length && i < finish; i++){
			outputArrays[i] = new ArrayList<E>();
	
			for(int j = start; j < arrays[i].size(); j++){
				outputArrays[i].add(arrays[i].get(j));
			}
		}
	
		return outputArrays;
	}

	/**
	 * Helper method for aggregating together an array of arraylists into a single arraylist
	 * 
	 * @param <E>
	 * @param arrayLists
	 * @return
	 */
	public static <E> ArrayList<E> aggregateArrayLists(ArrayList<E>[] arrayLists){
		
		ArrayList<E> retVal = new ArrayList<E>();
		
		for(ArrayList<E> arrayList : arrayLists){
			retVal.addAll(arrayList);
		}
		
		return retVal;
	}
}
