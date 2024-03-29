package tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

import org.w3c.dom.CDATASection;

import tools.Pair;

public class Statistics {
	

	public static final int NUMBER_DENSITY_BUCKETS = 1000;

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
	public static ArrayList<Pair<Double, Double>>[] calculateDensity(ArrayList<Pair<Double, Double>> inputArray) {
	
		// find range
		Double min = Double.MAX_VALUE;
		Double max = Double.MIN_VALUE;
			for (int j = 0; j < inputArray.size(); j++) {
			Double value = inputArray.get(j).second;
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
		
		for(Pair<Double, Double> value : inputArray){
			int index = (int)((value.second-min)/step);
			numOccurrences.set(index, numOccurrences.get(index)+1);
		}
		
		ArrayList<Pair<Double, Double>>[] retVal = new ArrayList[1];
		retVal[0] = new ArrayList<Pair<Double,Double>>();
		
		for(int i = 0; i < numOccurrences.size(); i++){
			double reconstruct = min + i * step;
			retVal[0].add(new Pair<Double, Double>(reconstruct, numOccurrences.get(i).doubleValue()));
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
	
		for(int i = 0; i < arrays.length; i++){
			outputArrays[i] = new ArrayList<E>();
	
			for(int j = start; j < arrays[i].size() && j < finish; j++){
				outputArrays[i].add(arrays[i].get(j));
			}
		}
	
		return outputArrays;
	}

	
	public static ArrayList<Pair<Double,Double>>[] averageArrayLists(ArrayList<Pair<Double,Double>>[] arrayLists){

		ArrayList<Pair<Double,Double>>[] retVal = new ArrayList[1];
		retVal[0] = new ArrayList<Pair<Double,Double>>();
		
		for(int i = 0; i < arrayLists[0].size(); i++){
			
			double count = 0;//TODO this shouldn't be a double
			
			for(int j = 0; j < arrayLists.length; j++){
				count += arrayLists[j].get(i).second;
			}
			
			retVal[0].add(new Pair<Double, Double>(new Double((double)i), count/arrayLists.length));
		}
		
		
		return retVal;
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
	
	public static double[] stripIndexValues(ArrayList<Pair<Double, Double>>[] series){
		
		ArrayList<Double> dataset = new ArrayList<Double>();
		
		for(ArrayList<Pair<Double, Double>> list : series){
			for(Pair<Double, Double> pair : list){
				dataset.add(pair.second);
			}
		}
		
		//Convert to doubles
		double[] retVal = new double[dataset.size()];
		for(int i =0; i < dataset.size(); i++){
			retVal[i] = dataset.get(i);
		}
		
		return retVal;
	}


	
	private static <E> ArrayList<E>[] wrapArrayList(ArrayList<E> input){
		@SuppressWarnings("unchecked")
		ArrayList<E>[] wrapper = new ArrayList[1];
		wrapper[0] = input;
		return wrapper;
	}
	
	public static ArrayList<Pair<Double, Double>> readFromFile(BufferedReader reader){
		
		ArrayList<Pair<Double, Double>> retVal = new ArrayList<Pair<Double,Double>>();
		
		try {
			while(true){
				
				StringTokenizer tokenizer = new StringTokenizer(reader.readLine());
				
				if(!tokenizer.hasMoreElements()){
					break;
				}
				
				Double first = Double.valueOf(tokenizer.nextToken());
				Double second = Double.valueOf(tokenizer.nextToken());
				
				retVal.add(new Pair<Double, Double>(first, second));
				
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return retVal;
	}
	
	public static void writeToFile(BufferedWriter writer, ArrayList<Pair<Double, Double>> data){
		try{
			for(Pair<Double, Double> dataPair : data){
				writer.write(dataPair.first + " " + dataPair.second+"\n");
			}
			
			writer.write("\n");
		} catch (Exception e) {
			System.out.println("Something went wrong when writing");
		}
	}
	
	public static void main(String[] args) {
		
		ArrayList<Pair<Double, Double>> dataArrayList = new ArrayList<Pair<Double,Double>>();
		dataArrayList.add(new Pair<Double, Double>(new Double(1), new Double(2)));
		dataArrayList.add(new Pair<Double, Double>(new Double(1), new Double(2)));
		dataArrayList.add(new Pair<Double, Double>(new Double(3), new Double(4)));
		dataArrayList.add(new Pair<Double, Double>(new Double(5), new Double(6)));
		
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("/suzume-charts/Test.dat"));
			writeToFile(writer, dataArrayList);
			
			writer.flush();
			writer.close();
			
			BufferedReader reader = new BufferedReader(new FileReader("/suzume-charts/Test.dat"));
			ArrayList<Pair<Double, Double>> outputs = readFromFile(reader);
			
			printPairList(outputs);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
