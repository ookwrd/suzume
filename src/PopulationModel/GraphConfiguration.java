package PopulationModel;

import AutoConfiguration.AbstractConfigurable;

public class GraphConfiguration extends AbstractConfigurable {

	public enum GraphType {FullyConnected, CyclicGraph}
	
	public static final GraphType DEFAULT_TYPE = GraphType.FullyConnected;
	
	public GraphType type = DEFAULT_TYPE;
	
	public GraphConfiguration(){}
	
	public GraphConfiguration(GraphType type, AbstractConfigurable baseConfig){
		super(baseConfig);
		this.type = type;
	}
	
}
