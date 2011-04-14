package tools;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;

import model.SimpleClustering;

public abstract class Clustering {
	
	public static final int MAX_NUM_CLUSTERS = 10;
	public static final int MIN_NUM_CLUSTERS = 4;
	
	public ArrayList<Double>[] array;
	
	public String clusteringConsole;

	/**
	 * Constructor 
	 * 
	 * @param array
	 */
	public Clustering(ArrayList<Double>[] array) {
		this.array = array;
		clusteringConsole = "";
	}
	
	/*
	public Hashtable<Double, Integer> cluster(ArrayList<Double>[] array) {
		return null;
	}*/
	
	/**
	 * Compute the Markov probabilistic model for the data
	 */
	public void findMarkov() {
		
		Hashtable<Double, Integer> clustering = cluster(array);
		//System.out.print("OH data size "+data[0].size());
		//System.out.println("OH clustering size : "+clustering.size());
		// render diagram
		// stateTransitionsNormalized(stateSequence, DEFAULT_STATE_TRANSITION_STEP);
		stateTransitionsNormalized(array, clustering, 1);
		stateTransitionsNormalized(array, clustering, 10);
		stateTransitionsNormalized(array, clustering, 50);
		stateTransitionsNormalized(array, clustering, 100);
		stateTransitionsNormalized(array, clustering, 200);
		
		// plot the sequence 
		/*ArrayList<Double>[] clusteringVal = new ArrayList[config.numberRuns];
		
		for(int i = 0; i < data.length; i++){
			clusteringVal[i] = new ArrayList<Double>();
			for(int j =0; j < data[0].size(); j++) {
				clusteringVal[i].add((double) clustering.get(data[i].get(j)));
			}
		}
		statisticsWindow.plot(clusteringVal, "Clustering 200-trimmed Gene Grammar Matches", "State", "", "States sequence");
		*/
		//StateTransitionVisualizer.render(stateTransitionsNormalized(stateSequence, DEFAULT_STATE_TRANSITION_STEP));
		
	}
	
	/**
     * Cluster points from an array
     * 
     * @param an array of values to cluster, each entry represents one
     *            occurrence of the value
     * @return a hashtable representing the mapping between each point (double
     *         key) and its cluster index (integer value)
     */
    public abstract Hashtable<Double, Integer> cluster(ArrayList<Double>[] data);
	
	/**
	 * Normalize the probability matrix so that every column sums up to 1
	 * @param i 
	 * @param clustering 
	 * 
	 * @param matrix
	 * @return
	 */
	private double[][] stateTransitionsNormalized(ArrayList<Double>[] ar, Hashtable<Double, Integer> clustering, int step) {
		Short[] stateSequence = stateSequence(ar, clustering, false);
		double[][] matrix = stateTransitions(stateSequence, step);
		double[][] result = new double[matrix.length][matrix[0].length]; 
		for (int i = 0; i < matrix.length; i++) {
			int colSum = 0;
			for (int j = 0; j < matrix[i].length; j++) {
				colSum += matrix[i][j];
			}
			for (int j = 0; j < matrix[i].length; j++) {
				if (colSum == 0) colSum = 1;
				result[i][j] = matrix[i][j]/colSum;
			}
		}
		DecimalFormat df = new DecimalFormat("########.00"); 
		appendConsole("\nTransition probabilities (single step: "+step+", X:gap)");
		for (int i = 0; i < result.length; i++) {
			for (int j = 0; j < result[0].length; j++) {
				appendConsole("("+(i==0?"X":i)+"->"+(j==0?"X":j)+"): "+df.format(result[i][j])+" ");
			}
			System.out.println();
		}
		return result;
	}
	
	private void appendConsole(String string) {
		clusteringConsole += string+("\n");
		System.out.println(string);
	}

	/**
	 * Compute the state transition probability matrix 
	 * 
	 * @param stateSequence
	 * @return
	 */
	private double[][] stateTransitions(Short[] stateSequence, int step) {
		double[][] transitions = new double[MAX_NUM_CLUSTERS][MAX_NUM_CLUSTERS];
		/*for (int i=0; i<MAX_NUM_STATES; i++) {
			for (int j=0; j<MAX_NUM_STATES; j++) {
				transitions[i][j] = 0.0;
			}
		}// init not indispensable */
		int from, to = 0;
		if(stateSequence.length>1)
			from=stateSequence[0];
		else { 
			System.out.println("Empty state sequence !");
			return transitions;
		}
		int fromMax = 0;
		int toMax = 0;
		if (step<1) step = 1;
		int start = 1;
		System.out.print("\nState transitions: "+from);
		for (int i = start; i < stateSequence.length; i+=step) {
			from = to;
			to = stateSequence[i];
			System.out.print(", "+to);
			int gen = this.array[0].size();
			if ((i-step)/gen == (i)/gen) // if jumped far enough to reach another run : don't update
				transitions[from][to] +=1; 
			
			if (from > fromMax) fromMax = from; //TODO get it directly from the clustering
			if (to > toMax) toMax = to;
		}
		fromMax++; // matrix length
		toMax++; // matrix width
		
		//return a smaller matrix
		double[][] result = new double[fromMax][toMax];
		
		//System.out.println("\nTransition count (single step: "+step+", X:gap)");
		for (int i = 0; i < fromMax; i++) {
			for (int j = 0; j < toMax; j++) {
				result[i][j] = transitions[i][j];
				//System.out.print("("+(i==0?"X":i)+"->"+(j==0?"X":j)+"):"+transitions[i][j]+" ");
			}
			System.out.println();
		}
		return result;
	}
	
	 /**
     * Extract state transitions
     * 
     * @param data array of the original data
     * @param clustering result of the classification (values-to-classes table)
     * @param noRepeat if true, don't take any repeated state into account
     * @return state sequence
     */
    public static Short[] stateSequence(ArrayList<Double>[] data, Hashtable<Double, Integer> clustering, boolean noRepeat) {
    	ArrayList<Integer> stateSequence = new ArrayList<Integer>(0);
		
		short previousState = 0;
		for (int i = 0; i < data.length; i += 1) { // for every run
			for (int j = 0; j < data[0].size(); j++) { // for every generation
				double elt = data[i].get(j);
				int tmp = clustering.get(elt);
				//System.out.print(elt + ":" + tmp + " ");
				previousState = (short) tmp;

				if (!noRepeat)
					stateSequence.add(tmp); // if repeating states : add
											// anyway

				if (previousState != tmp) {
					if (noRepeat)
						stateSequence.add(tmp); // if not repeating states :
												// add
												// only if different from
												// previous state
				}
			}
		}

    	/*System.out.print("\nState transitions ("+stateSequence.size()+" in total) : ");
    	for (int i = 0; i < stateSequence.size(); i++) {
			System.out.print(stateSequence.get(i)+"->"); //showing every single transition
		}*/
    	//System.out.print("\n#data = "+count+" ");
    	System.out.println("Number of states: "+stateSequence.size());
    	
    	int clusterNum = stateSequence.size();
    	Short[] states = new Short[clusterNum];
    	for (int i = 0; i < clusterNum; i++) {
			states[i] = stateSequence.get(i).shortValue();
		}
		return states;
    }
    


}
