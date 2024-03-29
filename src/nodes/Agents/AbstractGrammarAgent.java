package nodes.Agents;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import nodes.Node;
import nodes.Utterance;
import nodes.Agents.statisticaggregators.AbstractCountingAggregator;
import nodes.Agents.statisticaggregators.AbstractUniquenessAggregator;

import autoconfiguration.Configurable;

import simulation.RandomGenerator;

public abstract class AbstractGrammarAgent extends AbstractAgent {

	protected enum VisualizationTypes {NUMBER_NULLS, PHENOTYPE, SINGLE_WORD}
	protected enum StatisticsTypes {NUMBER_NULLS, NUMBER_PHENOTYPES}
	
	protected static final String SEMANTIC_SPACE_SIZE = "Semantic space size";
	protected static final String SYNTACTIC_SPACE_SIZE = "Syntactic space size";
	
	protected ArrayList<Integer> grammar;
	
	public AbstractGrammarAgent(){
		setDefaultParameter(VISUALIZATION_TYPE, VisualizationTypes.values(), new Object[]{VisualizationTypes.PHENOTYPE});
		setDefaultParameter(STATISTICS_TYPE, StatisticsTypes.values(), StatisticsTypes.values());
		setDefaultParameter(SEMANTIC_SPACE_SIZE, 12);
		setDefaultParameter(SYNTACTIC_SPACE_SIZE, 2);
	}

	public AbstractGrammarAgent(Configurable config, RandomGenerator randomGenerator){
		super(config, randomGenerator);

		grammar = new ArrayList<Integer>(getIntegerParameter(SEMANTIC_SPACE_SIZE));
		for (int j = 0; j < getIntegerParameter(SEMANTIC_SPACE_SIZE); j++){
			grammar.add(Utterance.SIGNAL_NULL_VALUE);
		}
	}
		
	@Override
	public void teach(Node learner) {
		learner.learnUtterance(getRandomUtterance());
	}

	@Override
	public void communicate(Node partner){
		Utterance utterance = ((AbstractGrammarAgent)partner).getRandomUtterance();

		//If agent and neighbour agree update fitnes.
		if(!utterance.isNull() && (grammar.get(utterance.meaning) == utterance.signal)){
			setFitness(getFitness()+1);
		}
	}

	public Utterance getRandomUtterance() {
		int index = randomGenerator.nextInt(grammar.size());
		Integer value = grammar.get(index);
		return new Utterance(index, value);
	}
	
	@Override
	public void draw(Dimension baseDimension, VisualizationStyle type, Object visualizationKey, Graphics g){
		if(!(visualizationKey instanceof VisualizationTypes)){
			super.draw(baseDimension, type, visualizationKey, g);
			return;
		}
		
		Color c;
		switch((VisualizationTypes)visualizationKey){
		case NUMBER_NULLS:
			c= mapValueToYellowRed(numberOfNullsInGrammar(), getIntegerParameter(SEMANTIC_SPACE_SIZE));
			break;

		case PHENOTYPE:
			double range = getIntegerParameter(SYNTACTIC_SPACE_SIZE) - 1;
			c = new Color(
					Math.abs((int)((grammar.get(0)*128+grammar.get(1)*64+grammar.get(2)*32+grammar.get(3)*16)/range)),
					Math.abs((int)((grammar.get(4)*128+grammar.get(5)*64+grammar.get(6)*32+grammar.get(7)*16)/range)),
					Math.abs((int)((grammar.get(8)*128+grammar.get(9)*64+grammar.get(10)*32+grammar.get(11)*16)/range))
			);
		break;
		
		case SINGLE_WORD:
			c = mapValueToRainbow(grammar.get(0), getIntegerParameter(SYNTACTIC_SPACE_SIZE));
			break;
			
		default:
			System.err.println("Unrecognized visualization type");
			return;
		}

		g.setColor(c);
		g.fillRect(0, 0, baseDimension.width, baseDimension.height);
	}
	
	@Override
	public StatisticsAggregator getStatisticsAggregator(Object statisticsKey){
		if(!(statisticsKey instanceof StatisticsTypes)){
			return super.getStatisticsAggregator(statisticsKey);
		}
		
		switch((StatisticsTypes)statisticsKey){
		case NUMBER_NULLS:
			return new AbstractCountingAggregator(StatisticsCollectionPoint.PostFinalizeFitness, "Number of Nulls") {	
				@Override
				protected double getValue(Node agent) {
					return ((AbstractGrammarAgent)agent).numberOfNullsInGrammar();
				}
			};
			
		case NUMBER_PHENOTYPES:
			return new AbstractUniquenessAggregator<Object>(StatisticsCollectionPoint.PostFinalizeFitness, "Number of Phenotypes") {
				@Override
				protected Object getItem(Node agent) {
					return ((AbstractGrammarAgent)agent).grammar;
				}
			};
		
		default:
			System.err.println(AbstractGrammarAgent.class.getName() + ": Unknown StatisticsType");
			return null;
		}
	}
	
	public Double numberOfNullsInGrammar() {
		double count = 0;
		for(int i = 0; i < getIntegerParameter(SEMANTIC_SPACE_SIZE); i++){
			if(grammar.get(i).equals(Utterance.SIGNAL_NULL_VALUE)){
				count++;
			}
		}
		return count;
	}
}
