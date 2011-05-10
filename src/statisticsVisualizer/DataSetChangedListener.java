package statisticsVisualizer;

import java.util.ArrayList;

import tools.Pair;

public interface DataSetChangedListener {

	public void dataSetChangedListener(ArrayList<Pair<Double, Double>>[] dataSet);
	
}
