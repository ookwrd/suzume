package populationNodes;

import java.awt.Color;

import javax.swing.JLabel;

import populationNodes.AbstractNode.NodeType;
import AutoConfiguration.BasicConfigurable;
import AutoConfiguration.ConfigurationPanel;
import AutoConfiguration.ConfigurationParameter;

public class NodeConfiguration extends BasicConfigurable {

	/*public static final String NODE_TYPE = "Node type";
	
	{
		setDefaultParameter(NODE_TYPE, new ConfigurationParameter(NodeType.values()));
	}*/
	
	public NodeConfiguration(){	
	}
	
	public NodeConfiguration(BasicConfigurable baseConfig){
		super(baseConfig);
	}

	/*@Override
	public ConfigurationPanel getConfigurationPanel(){
		ConfigurationPanel ret = super.getConfigurationPanel();
		ret.setBackground(Color.pink);
		ret.add(new JLabel("test"),0);
		
		ConfigurationPanel ret1 = new ConfigurationPanel(){{addComboBox("Commbo", new Integer[]{1,2,3}, new Integer(1));}};
		ret1.initializeParameters(this);
		return ret1;
	}*/

}

