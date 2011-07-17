package populationNodes;

public class Utterance {

	public static final int SIGNAL_NULL_VALUE = -1;
	
	int meaning;
	int signal;
	
	public Utterance(int meaningIndex, int signalValue) {
		this.meaning = meaningIndex;
		this.signal = signalValue;
	}
	
	public boolean isNull(){
		return signal == SIGNAL_NULL_VALUE;
	}

}
