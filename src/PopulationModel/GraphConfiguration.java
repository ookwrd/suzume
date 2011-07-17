package PopulationModel;

import AutoConfiguration.BasicConfigurable;

public class GraphConfiguration extends BasicConfigurable {

	public enum GraphType {FullyConnected, CyclicGraph}
	
	public static final GraphType DEFAULT_TYPE = GraphType.FullyConnected;
	
	public GraphType type = DEFAULT_TYPE;
	
	public GraphConfiguration(){}
	
	public GraphConfiguration(GraphType type, BasicConfigurable baseConfig){
		super(baseConfig);
		this.type = type;
	}
	
}
