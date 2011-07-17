package AutoConfiguration;

import java.util.HashMap;


public interface Configurable {

	public HashMap<String, ConfigurationParameter> getParameters();

	public ConfigurationParameter getParameter(String key);

	public void setParameter(String key, ConfigurationParameter parameter);

	public void setDefaultParameter(String key, ConfigurationParameter parameter);
	
	public BasicConfigurationPanel getConfigurationPanel();
	
}
