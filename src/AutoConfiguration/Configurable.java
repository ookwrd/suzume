package AutoConfiguration;

import java.util.HashMap;


public interface Configurable {

	public HashMap<String, ConfigurationParameter> getDefaultParameters();
	
}
