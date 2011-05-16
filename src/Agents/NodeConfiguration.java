package Agents;

import java.util.HashMap;
import java.util.StringTokenizer;

public class NodeConfiguration {

	public enum NodeType { YamauchiHashimoto2010, BiasAgent/*, SynonymAgent*/, AlteredAgent, FixedProbabilityAgent }

	public static final NodeType DEFAULT_AGENT_TYPE = NodeType.YamauchiHashimoto2010;//See *#* below if you change this.
	
	public NodeType type = DEFAULT_AGENT_TYPE;//Extract to Agent Configuration class.
	
	public HashMap<String, ConfigurationParameter> parameters = new HashMap<String, ConfigurationParameter>();
	
	public NodeConfiguration(){
		this.type = DEFAULT_AGENT_TYPE;
		//*#*
		//TODO maybe I need to get rid of this method?
	}
	
	public NodeConfiguration(NodeType type, HashMap<String, ConfigurationParameter> parameters){
		this.type = type;
		this.parameters = parameters;
	}

	public NodeConfiguration(StringTokenizer tokenizer) {
		// TODO Auto-generated constructor stub
	}

	public String toString(){
		return "Agent Type: " + type;
	}
	
	public String saveString() {
		// TODO Auto-generated method stub
		return null;
	}
}

