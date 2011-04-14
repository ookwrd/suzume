package tools;

import java.util.ArrayList;
import java.util.Hashtable;

public interface Clustering {
	
	public static final int MAX_NUM_CLUSTERS = 10;

	public static final int MIN_NUM_CLUSTERS = 4;

	public Hashtable<Double, Integer> cluster(ArrayList<Double>[] array);

}
