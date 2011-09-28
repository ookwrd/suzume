package populationNodes.Agents;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;

import populationNodes.AbstractNode;
import populationNodes.NodeConfiguration;
import populationNodes.Utterance;

import AutoConfiguration.ConfigurationParameter;
import PopulationModel.Node;

import simulation.RandomGenerator;

public abstract class AbstractAgent extends AbstractNode implements Agent {
	
	protected static final String VISUALIZATION_TYPE = "Visualization Type";
	
	private enum VisualizationTypes {FITNESS}
	
	public static final String FITNESS_STATISTICS = "Fitness";
	public static final String BASE_FITNESS = "Base fitness value:";
	
	protected static final String NUMBER_OF_MEANINGS = "Meaning space size";

	private double fitness;
	protected ArrayList<Integer> grammar;
	
	public AbstractAgent(){	
		setDefaultParameter(NUMBER_OF_MEANINGS, new ConfigurationParameter(12));
		setDefaultParameter(BASE_FITNESS, new ConfigurationParameter(1));
		setDefaultParameter(VISUALIZATION_TYPE, new ConfigurationParameter(VisualizationTypes.values(), false));
	}
	
	public void initializeAgent(NodeConfiguration config, int id, RandomGenerator randomGenerator){
		super.initializeAgent(config, id, randomGenerator);

		setFitness(getIntegerParameter(BASE_FITNESS));;
		
		grammar = new ArrayList<Integer>(getIntegerParameter(NUMBER_OF_MEANINGS));
		for (int j = 0; j < getIntegerParameter(NUMBER_OF_MEANINGS); j++){
			grammar.add(Utterance.SIGNAL_NULL_VALUE);
		}
	}

	public ArrayList<Integer> getGrammar() {
		return grammar;
	}
	
	public Double numberOfNulls() {
		double count = 0;
		for(int i = 0; i < getIntegerParameter(NUMBER_OF_MEANINGS); i++){
			if(grammar.get(i).equals(Utterance.SIGNAL_NULL_VALUE)){
				count++;
			}
		}
		return count;
	}
	
	@Override
	public double getFitness() {
		return fitness;
	}
	
	@Override
	public void setFitness(double fitness){
		this.fitness = fitness;
	}
	
	@Override
	public boolean canStillLearn(){
		return true;
	}
	
	@Override
	public void invent(){
	}
	
	@Override
	public void teach(Node learner) {
		learner.learnUtterance(getRandomUtterance());
	}
	
	@Override
	public void communicate(Node partner){
		
		Utterance utterance = partner.getRandomUtterance();

		//If agent and neighbour agree update fitnes.
		if(!utterance.isNull() && (getGrammar().get(utterance.meaning) == utterance.signal)){
			setFitness(getFitness()+1);
		}
	}
	
	@Override
	public void adjustFinalFitnessValue(){
		//Do nothing.
	}
	
	public ArrayList getPhenotype(){
		return grammar;
	}
	
	@Override
	public ArrayList<Agent> getBaseAgents(){
		ArrayList<Agent> retAgents = new ArrayList<Agent>();
		retAgents.add(this);
		return retAgents;
	}
	
	@Override
	public void draw(Dimension baseDimension, VisualizationStyle type, Object visualizationKey, Graphics g){
		
		if(!(visualizationKey instanceof VisualizationTypes)){
			super.draw(baseDimension, type, visualizationKey, g);
			return;
		}
		
		Color c;
		
		switch ((VisualizationTypes)visualizationKey) {
		case FITNESS:
			int fitness = new Double(getFitness()).intValue();
			c = new Color(fitness*8, fitness*8, 0);
			g.setColor(c);
			g.fillRect(0, 0, baseDimension.width, baseDimension.height);
			break;

		default:
			System.err.println("Unrecognized Visualization type in AbstractAgent");
		}
	}
	
	@Override
	public ArrayList<StatisticsAggregator> getStatisticsAggregators(){
		ArrayList<StatisticsAggregator> retVal = new ArrayList<StatisticsAggregator>();
		
		retVal.add(new AbstractCountingAggregator("Gene Grammar Match") {
			@Override
			protected void updateCount(Node agent) {
				addToCount(((AbstractAgent)agent).geneGrammarMatch());
			}
		});
		
		retVal.add(new AbstractCountingAggregator("Learning Intensity") {
			@Override
			protected void updateCount(Node agent) {
				addToCount(((AbstractAgent)agent).learningIntensity());
			}
		});
		
		retVal.add(new AbstractCountingAggregator("Number of Nulls") {	
			@Override
			protected void updateCount(Node agent) {
				addToCount(((AbstractAgent)agent).numberOfNulls());
			}
		});
		
		retVal.add(new AbstractCountingAggregator("Fitness") {
			@Override
			public void updateCount(Node agent) {	
				addToCount(((Agent)agent).getFitness());
			}
		});
		
		retVal.add(new AbstractUniguenessAggregator<Object>("Number of Genotypes") {
			@Override
			protected void checkUniqueness(Node agent) {
				addItem(((AbstractAgent)agent).getGenotype());
			}
		});
		
		retVal.add(new AbstractUniguenessAggregator<Object>("Number of Phenotypes") {
			@Override
			protected void checkUniqueness(Node agent) {
				addItem(((AbstractAgent)agent).getPhenotype());
			}
		});
	
		return retVal;
	}
	
	//Statistics
	public abstract Double geneGrammarMatch();
	public abstract  Double learningIntensity(); //TODO how do i make this more general??
	public abstract Object getGenotype();//TODO get rid of this

	@Override
	public ArrayList<Object> getVisualizationKeys() {
		return new ArrayList<Object>(Arrays.asList(getParameter(VISUALIZATION_TYPE).getSelectedValues()));
	}	
	
}
