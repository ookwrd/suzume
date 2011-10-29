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
import autoconfiguration.ConfigurationParameter;

import simulation.RandomGenerator;

public abstract class AbstractGrammarAgent extends AbstractAgent {

	protected enum VisualizationTypes {PHENOTYPE, SINGLE_WORD, NUMBER_NULLS}
	protected enum StatisticsTypes {NUMBER_NULLS, NUMBER_PHENOTYPES}
	
	protected static final String NUMBER_OF_MEANINGS = "Meaning space size";
	
	protected ArrayList<Integer> grammar;
	
	public AbstractGrammarAgent(){
		setDefaultParameter(VISUALIZATION_TYPE, new ConfigurationParameter(VisualizationTypes.values(), false));
		setDefaultParameter(Node.STATISTICS_TYPE, new ConfigurationParameter(StatisticsTypes.values(),StatisticsTypes.values()));
		setDefaultParameter(NUMBER_OF_MEANINGS, new ConfigurationParameter(12));
	}

	@Override
	public void initialize(Configurable config, int id, RandomGenerator randomGenerator){
		super.initialize(config, id, randomGenerator);

		grammar = new ArrayList<Integer>(getIntegerParameter(NUMBER_OF_MEANINGS));
		for (int j = 0; j < getIntegerParameter(NUMBER_OF_MEANINGS); j++){
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
			int numberOfNulls = new Double(numberOfNullsInGrammar()).intValue();
			c = new Color(255, 255-numberOfNulls*16, 255-numberOfNulls*16);
			break;

		case PHENOTYPE:
			c = new Color(
					Math.abs(grammar.get(0)*128+grammar.get(1)*64+grammar.get(2)*32+grammar.get(3)*16),
					Math.abs(grammar.get(4)*128+grammar.get(5)*64+grammar.get(6)*32+grammar.get(7)*16),
					Math.abs(grammar.get(8)*128+grammar.get(9)*64+grammar.get(10)*32+grammar.get(11)*16)
			);
		break;
		
		case SINGLE_WORD:
			int value;
			value = grammar.get(0);		
			if(value == 0){
				c = Color.WHITE;
			} else if (value == 1){
				c = Color.BLACK;
			} else if (value == 2){
				c = Color.BLUE;
			}else if (value == 3){
				c = Color.GREEN;
			}else if (value == 4){
				c = Color.YELLOW;
			}else if (value == 5){
				c = Color.ORANGE;
			}else if (value == 6){
				c = Color.CYAN;
			}else if (value == 7){
				c = Color.DARK_GRAY;
			}else if (value == 8){
				c = Color.GRAY;
			}else if (value == 9){
				c = Color.MAGENTA;
			}else{
				c = Color.RED;
			}
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
		for(int i = 0; i < getIntegerParameter(NUMBER_OF_MEANINGS); i++){
			if(grammar.get(i).equals(Utterance.SIGNAL_NULL_VALUE)){
				count++;
			}
		}
		return count;
	}
}
