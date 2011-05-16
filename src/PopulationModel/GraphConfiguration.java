package PopulationModel;

import AutoConfiguration.BasicConfiguration;

public class GraphConfiguration extends BasicConfiguration {

	public enum GraphType {FullyConnected, CyclicGraph}
	
	public static final GraphType DEFAULT_TYPE = GraphType.FullyConnected;
	
	public GraphType type = DEFAULT_TYPE;
	
	public GraphConfiguration(){}
	
	public GraphConfiguration(GraphType type, BasicConfiguration baseConfig){
		super(baseConfig.parameters);
		this.type = type;
	}
	
}
