package tools;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import Launcher.Launcher;

import model.ModelController;

/**
 * This class is the entry point for constructing Cluster Analysis objects. Each
 * instance of JCA object is associated with one or more clusters, and a Vector
 * of DataPoint objects. The JCA and DataPoint classes are the only classes
 * available from other packages.
 * 
 * @see DataPoint
 * @author Olaf
 */

public class ClusteringTool {

	private static final int KMEANS_NUM_ITERATIONS = 1000;

	private static final int KMEANS_MAX_OUTER_ITERATIONS = 10000;
	
	private static double deviation = 0;

	/**
	 * Computes a k-means clustering on a one-dimensional set of points
	 * 
	 * @param arraylists
	 *            the input set of points
	 * @param numClusters
	 *            number of clusters
	 * @return the global standard deviation
	 */
	public static double sigmaKmeans(ArrayList<Double>[] arraylists, int numClusters) {

		Hashtable<Double, Integer> clusters = new Hashtable<Double, Integer>();
		Vector<DataPoint> dataPoints = new Vector<DataPoint>();
		
		for (ArrayList<Double> array : arraylists) {
			for (Double value : array) {
				dataPoints.add(new DataPoint(0, value, "" + value));
			}
		}

		JCA jca = new JCA(numClusters, KMEANS_NUM_ITERATIONS, dataPoints);
		jca.startAnalysis();

		System.out.println("\n*** " + numClusters + "-means partition ***");
		
		Vector[] v = jca.getClusterOutput();
		double totalDeviation = 0;
		
		for (int i = 0; i < v.length; i++) { // i = cluster index
			Vector tempV = v[i];
			System.out.print("cluster " + i + ": ");
			Iterator iter = tempV.iterator();
			int clusterSize = 0;
			// double sum = 0;
			double centroidMean = 0.0;
			double sumOfSquaredDifferences = 0.0;
			
			while (iter.hasNext()) {
				DataPoint dpTemp = (DataPoint) iter.next();

				if (centroidMean == 0) {
					centroidMean = dpTemp.getCluster().getCentroid().getCy();
				}
				
				double y = dpTemp.getY();
				clusters.put(y, i); //TODO uncomment : this being commented is just a test
				sumOfSquaredDifferences += Math.pow(y - centroidMean, 2); // update the sum of variances for this cluster
				clusterSize++;
				
				// System.out.print(//dpTemp.getObjName() + "[" + dpTemp.getY()
				// + "] ");

				// sum += dpTemp.getY();
				
			}
			double sigmaCluster = sumOfSquaredDifferences/clusterSize; // cluster deviation
			totalDeviation += sigmaCluster;
			System.out.println("mean = " + centroidMean + " / std deviation = " + sigmaCluster);
		}
		totalDeviation = Math.sqrt(totalDeviation/numClusters);
		return totalDeviation;
	}

	
	/**
	 * Computes a k-means clustering on a one-dimensional set of points
	 * 
	 * @param arraylists
	 *            the input set of points
	 * @param numClusters
	 *            number of clusters
	 * @return hashtable mapping between the points and their cluster index
	 */
	public static Hashtable<Double, Integer> kmeansFixedClusters(
			ArrayList<Double>[] arraylists, int numClusters) {

		Hashtable<Double, Integer> clusters = new Hashtable<Double, Integer>();
		Vector<DataPoint> dataPoints = new Vector<DataPoint>();

		for (ArrayList<Double> array : arraylists) {
			for (Double value : array) {
				dataPoints.add(new DataPoint(0, value, "" + value));
			}
		}

		JCA jca = new JCA(numClusters, KMEANS_NUM_ITERATIONS, dataPoints);
		jca.startAnalysis();

		System.out.println("\n*** " + numClusters + "-means partition ***");

		Vector[] v = jca.getClusterOutput();
		double totalDeviation = 0;
		for (int i = 0; i < v.length; i++) { // i = cluster index
			Vector tempV = v[i];
			System.out.print("cluster " + i + ": ");
			Iterator iter = tempV.iterator();
			int clusterSize = 0;
			double sumOfSquaredDifferences = 0.0;
			double centroidMean = 0;
			while (iter.hasNext()) {
				DataPoint dpTemp = (DataPoint) iter.next();

				if (centroidMean == 0) {
					centroidMean = dpTemp.getCluster().getCentroid().getCy();
				}
				
				double y = dpTemp.getY();
				clusters.put(y, i); //TODO uncomment : this being commented is just a test
				sumOfSquaredDifferences += Math.pow(y - centroidMean, 2); // update the sum of variances for this cluster
				clusterSize++;
			}
			//double sigma = Math.abs(diffSum/clusterSize);
			double sigmaCluster = sumOfSquaredDifferences/clusterSize; // cluster deviation
			totalDeviation += sigmaCluster;
			System.out.println("mean = " + centroidMean + " / std deviation = " + sigmaCluster);
		}
		deviation = totalDeviation;
		return clusters;
	}

	// deprecated
	public static void kmeans(Vector<DataPoint> dataPoints, int numClusters) {

		JCA jca = new JCA(numClusters, KMEANS_NUM_ITERATIONS, dataPoints);
		jca.startAnalysis();

		Vector[] v = jca.getClusterOutput();
		for (int i = 0; i < v.length; i++) {
			Vector tempV = v[i];
			System.out.println("-----------Cluster" + i + "---------");
			Iterator iter = tempV.iterator();
			while (iter.hasNext()) {
				DataPoint dpTemp = (DataPoint) iter.next();
				// System.out.println(dpTemp.getObjName() + "[" + dpTemp.getX()+
				// "," + dpTemp.getY() + "]");
				System.out.println(dpTemp.getObjName() + "[" + dpTemp.getY()
						+ "]");
			}
		}

	}
	
	/**
	 * Makes the function pair by mirroring with respect to the x-axis, at the end of the data
	 * 
	 * @param function
	 * @return mirrored function
	 */
	public static void symmetry(Hashtable<Double, Integer> function) {
		//TODO
	}

	public static void main(String args[]) {
		Launcher.main(null);
	}
	
	public static void main2(String args[]) {
		Vector<DataPoint> dataPoints = new Vector<DataPoint>();
		dataPoints.add(new DataPoint(0, 1, ""));
		dataPoints.add(new DataPoint(0, 1.1, ""));
		dataPoints.add(new DataPoint(0, 1.5, ""));
		dataPoints.add(new DataPoint(0, 3.5, ""));
		dataPoints.add(new DataPoint(0, 4, ""));
		dataPoints.add(new DataPoint(0, 7, ""));
		// dataPoints.add(new DataPoint(0, 6, "qdfzer"));
		for (int i = 0; i < 6; i++) {
			kmeans(dataPoints, 3);
			System.out.println("**************************************");
		}

	}

	/**
	 * Performs a k-means stopping whenever the threshold decrease is below a certain threshold
	 * 
	 * @param array
	 * @param threshold
	 * @return
	 */
	public static Hashtable<Double, Integer> kmeansLimDevDecrease(ArrayList<Double>[] array, double threshold) {
		Hashtable<Double, Integer> results = new Hashtable<Double, Integer>();
		Hashtable<Double, Integer>  prevClusters = new Hashtable<Double, Integer>();
		double prevDecreaseFactor = 0;
		int numClusters = 0;
		double curDeviation = 100000000;
		double prevDeviation;
		for(int i=2; i<=KMEANS_MAX_OUTER_ITERATIONS; i++) {
			Hashtable<Double, Integer> clusters = kmeansFixedClusters(array, i);
			prevDeviation = curDeviation;
			curDeviation = deviation;
			System.out.println("sigma("+i+" clusters): "+deviation);
			
			if (i>2 && prevDeviation/curDeviation > prevDecreaseFactor) { // if the decrease factor is at its min
				System.out.println("minimum geometric decrease factor found: "+prevDeviation/curDeviation+" > "+prevDecreaseFactor);
				return prevClusters; 
			}
			
			prevDecreaseFactor = prevDeviation/curDeviation;
			prevClusters = clusters;
			
			if (prevDeviation/curDeviation < threshold) {
				System.out.println(prevDeviation/curDeviation+" < "+threshold);
				return clusters;
			}
			else System.out.println(prevDeviation/curDeviation+" > "+threshold);
			
			// quick fix in case there would be many clusters
			if (i>=100) i+=100-1;
			else if (i>=20) i+=20-1;
			else if (i>=10) i+=10-1;
		}
		System.out.println("Found "+numClusters+" clusters");
		return results;
	}
	
	/**
	 * 
	 * @param arraylists density
	 * @return number of peaks maxima
	 */
	/*public static int findPeaks(ArrayList<Double> array) {
		
		//Hashtable<Double, Double> centredMeans = new Hashtable<Double, Double>();
		Hashtable<Double, Integer> density = ModelController.calculateDensity(array);
		
		//first find the maximal density
		Enumeration<Integer> en = density.elements();
		int max = 0;
		while (en.hasMoreElements()) {
			int tmp = en.nextElement();
			if(tmp > max) max = tmp; 
		}
		double threshold = max/5;
		
		System.out.println("Max values: ");
		
		Hashtable<Double, Integer> smoothDensity = smooth(density, 10);
		Enumeration<Double> e = smoothDensity.keys();
		
		double x1, x2 = 0, y1, y2;
		int count = 0;
		if (e.hasMoreElements()) x2 = e.nextElement();
		while(e.hasMoreElements()) {
			x1 = x2;
			x2 = e.nextElement();
			y1 = smoothDensity.get(x1); //TODO replace by hashtable
			y2 = smoothDensity.get(x2);
			double d = (y2-y1) / (x2-x1);
			//centredMeans.put( x1, (y2-y1)/(x2-x1) ); //estimate derivative
			if (d > threshold) {
				count++;
				System.out.println(" "+x1);
			}
		}
		System.out.println("Found "+count+" peaks, with threshold "+threshold);
		return count;
	}*/
	
	/**
	 * Smoothen a function by averaging with a sliding window moving through the coordinate vector 
	 * 
	 * @param e
	 * @param slidingWindowSize size of the window
	 * @return the smoothened list
	 */
	public static Hashtable<Double,Integer> smooth(Hashtable<Double,Integer> function, int slidingWindowSize){
		ArrayList<Double> window = new ArrayList<Double>(slidingWindowSize);
		Hashtable<Double,Integer> result = new Hashtable<Double,Integer>();
		Enumeration<Double> e = function.keys();
		
		for(int i=0;i<slidingWindowSize;i++){
			if (e.hasMoreElements()) {
				Double val = e.nextElement();
				window.add(val);
				result.put(val, function.get(val));
			}
		}
		while (e.hasMoreElements()) {
			double windowAverage = 0;
			double tmp = e.nextElement();
			window.add(tmp);
			for(int i=0;i<window.size();i++) {
				windowAverage += window.get(i);
			}
			result.put(windowAverage, function.get(tmp));
			window.remove(0);
		}
		return result;
	}
}
