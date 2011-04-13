package tools;

import java.util.ArrayList;
import java.util.Hashtable;

public interface Clustering {
	
	public Hashtable<Double, Integer> cluster(ArrayList<Double>[] array);

}
