package autoconfiguration;

import java.util.ArrayList;
import java.util.HashMap;

public interface Configurable {

	public void initialize(HashMap<String, ConfigurationParameter> parameters);
	
	public void overrideParameter(String key, ConfigurationParameter parameter);//TODO these should be moved into BasicConfigurable
	public void setFixedParameter(String key, ConfigurationParameter parameter);
	
	public ConfigurationParameter getParameter(String key);
	public HashMap<String, ConfigurationParameter> getParameters();
	public ArrayList<String> getFixedParameters();
	
	//Special Subclass of Configurable objects
	public interface Describable{
		public String getDescription();
	}
	
}
