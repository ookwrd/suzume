package AutoConfiguration;

public interface Configurable {

	public ConfigurationParameter getParameter(String key);
	public void setParameter(String key, ConfigurationParameter parameter);
	public ConfigurationPanel getConfigurationPanel();
	
	//Special Subclass of Configurable objects
	public interface Describable{
		public String getDescription();
	}
	
}
