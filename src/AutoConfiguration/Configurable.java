package AutoConfiguration;

import java.util.HashMap;

public interface Configurable {

	public void setParameter(String key, ConfigurationParameter parameter);
	public ConfigurationParameter getParameter(String key);
	public HashMap<String, ConfigurationParameter> getParameters();
	public ConfigurationPanel getConfigurationPanel();
	
	//Special Subclass of Configurable objects
	public interface Describable{
		public String getDescription();
	}
	
}
