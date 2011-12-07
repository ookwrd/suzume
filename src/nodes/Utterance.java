package nodes;

public class Utterance {

	public static final int SIGNAL_NULL_VALUE = -1;
	
	public int meaning;
	public int signal;
	
	public Utterance(int meaningIndex, int signalValue) {
		this.meaning = meaningIndex;
		this.signal = signalValue;
	}
	
	public boolean isNull(){
		return signal == SIGNAL_NULL_VALUE;
	}
	
	public String toString(){
		return "<Utterance:"+meaning+","+signal+">";
	}

}
