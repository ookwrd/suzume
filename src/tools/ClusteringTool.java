package tools;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

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
	public static Hashtable<Double, Integer> kmeans(
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
		for (int i = 0; i < v.length; i++) { // i = cluster index
			Vector tempV = v[i];
			System.out.print("cluster " + i + ": ");
			Iterator iter = tempV.iterator();
			int clusterSize = 0;
			// double sum = 0;
			double diffSum = 0;
			double centroidMean = 0;
			while (iter.hasNext()) {
				DataPoint dpTemp = (DataPoint) iter.next();

				if (centroidMean == 0) {
					centroidMean = dpTemp.getCluster().getCentroid().getCy();
				}
				
				double y = dpTemp.getY();
				clusters.put(y, i); //TODO uncomment : this being commented is just a test
				diffSum += y - centroidMean;
				clusterSize++;
				
				// System.out.print(//dpTemp.getObjName() + "[" + dpTemp.getY()
				// + "] ");

				// sum += dpTemp.getY();
				
			}
			double sigma = Math.abs(diffSum/clusterSize);
			System.out.println("mean = " + centroidMean + " / std deviation = " + sigma);
		}

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

	public static void main(String args[]) {
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
}
