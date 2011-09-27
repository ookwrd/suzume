package populationNodes;
 


import AutoConfiguration.BasicConfigurable;

public class NodeConfiguration extends BasicConfigurable {
	
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

