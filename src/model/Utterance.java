package model;

public class Utterance {

	public static final int NULL_VALUE = -1;
	
	int index;
	int value;
	
	public Utterance(int meaningIndex, int signalValue) {
		this.index = meaningIndex;
		this.value = signalValue;
	}
	
	public boolean isNull(){
		return value == NULL_VALUE;
	}

}
