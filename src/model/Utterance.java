package model;

import model.ModelController.Allele;


public class Utterance {

	int index;
	Allele value;
	
	public Utterance(int index, Allele value) {
		this.index = index;
		this.value = value;
	}
	
	public boolean isNull(){
		return value == Allele.NULL;
	}

}
