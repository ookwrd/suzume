package autoconfiguration;

import java.util.HashMap;

public interface Configurable {

	public void setParameter(String key, ConfigurationParameter parameter);
	public void setFixedParameter(String key, ConfigurationParameter parameter);
	public void fixParameter(String key);
	
	public ConfigurationParameter getParameter(String key);
	public HashMap<String, ConfigurationParameter> getParameters();
	public HashMap<String, ConfigurationParameter> getEditableParameters();
	
	public ConfigurationPanel getConfigurationPanel();
	
	//Special Subclass of Configurable objects
	public interface Describable{
		public String getDescription();
	}
	
}
