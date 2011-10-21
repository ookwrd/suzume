package autoconfiguration;

import java.util.ArrayList;
import java.util.HashMap;

public interface Configurable {

	public void initialize(HashMap<String, ConfigurationParameter> parameters);
	
	public void setParameter(String key, ConfigurationParameter parameter);
	public void setFixedParameter(String key, ConfigurationParameter parameter);
	public void fixParameter(String key);
	
	public ConfigurationParameter getParameter(String key);
	public HashMap<String, ConfigurationParameter> getParameters();
	public ArrayList<String> getFixedParameters();
	public HashMap<String, ConfigurationParameter> getEditableParameters();
	
	public ConfigurationPanel getConfigurationPanel();
	
	//Special Subclass of Configurable objects
	public interface Describable{
		public String getDescription();
	}
	
}
