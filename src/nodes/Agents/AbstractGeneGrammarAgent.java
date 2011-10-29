package nodes.Agents;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import nodes.Node;
import nodes.Agents.statisticaggregators.AbstractCountingAggregator;
import nodes.Agents.statisticaggregators.AbstractUniquenessAggregator;

import autoconfiguration.Configurable;
import autoconfiguration.ConfigurationParameter;

import simulation.RandomGenerator;

public abstract class AbstractGeneGrammarAgent extends AbstractGrammarAgent {

	protected enum VisualizationTypes {GENOTYPE, SINGLE_GENE, GENE_GRAMMAR_MATCH}
	protected enum StatisticsTypes {NUMBER_GENOTYPES, GENE_GRAMMAR_MATCH}
	protected static final String SYNTACTIC_SPACE_SIZE = "Syntactic space size";
	
	protected ArrayList<Integer> chromosome;

	public AbstractGeneGrammarAgent(){
		setDefaultParameter(STATISTICS_TYPE, new ConfigurationParameter(StatisticsTypes.values(),StatisticsTypes.values()));
		setDefaultParameter(VISUALIZATION_TYPE, new ConfigurationParameter(VisualizationTypes.values(), new Object[]{VisualizationTypes.GENOTYPE}));
		setDefaultParameter(SYNTACTIC_SPACE_SIZE, new ConfigurationParameter(2));
	}
	
	@Override
	public void initialize(Configurable config, int id, RandomGenerator randomGenerator) {
		super.initialize(config, id, randomGenerator);
		
		chromosome = new ArrayList<Integer>(getIntegerParameter(NUMBER_OF_MEANINGS));
		for (int i = 0; i < getIntegerParameter(NUMBER_OF_MEANINGS); i++) { // all alleles are initially set to a random value initially
			chromosome.add(randomGenerator.nextInt(getIntegerParameter(SYNTACTIC_SPACE_SIZE)));
		}
	}
	
	@Override
	public void draw(Dimension baseDimension, VisualizationStyle type, Object visualizationKey, Graphics g){
		if(!(visualizationKey instanceof VisualizationTypes)){
			super.draw(baseDimension, type, visualizationKey, g);
			return;
		}
		
		Color c;
		switch((VisualizationTypes)visualizationKey){
		case GENOTYPE:
			c = new Color(
					Math.abs(chromosome.get(0)*128+chromosome.get(1)*64+chromosome.get(2)*32+chromosome.get(3)*16),
					Math.abs(chromosome.get(4)*128+chromosome.get(5)*64+chromosome.get(6)*32+chromosome.get(7)*16),
					Math.abs(chromosome.get(8)*128+chromosome.get(9)*64+chromosome.get(10)*32+chromosome.get(11)*16)
			);
		break;
		
		case SINGLE_GENE:
			int value;
			if(visualizationKey.equals("singleWord")){
				value = grammar.get(0);
			}else{
				value = chromosome.get(0);
			}		
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
			
		case GENE_GRAMMAR_MATCH:
			int geneGrammarMatch = new Double(geneGrammarMatch()).intValue();
			c = new Color(255, 255-geneGrammarMatch*16, 255-geneGrammarMatch);
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
	
		switch ((StatisticsTypes)statisticsKey) {
		case NUMBER_GENOTYPES:
			return new AbstractUniquenessAggregator<Object>(StatisticsCollectionPoint.PostFinalizeFitness,"Number of Genotypes") {
				@Override
				protected Object getItem(Node agent) {
					return ((AbstractGeneGrammarAgent)agent).chromosome;
				}
			};
			
		case GENE_GRAMMAR_MATCH:
			return new AbstractCountingAggregator(StatisticsCollectionPoint.PostFinalizeFitness,"Gene Grammar Match") {
				@Override
				protected double getValue(Node agent) {
					return ((AbstractGeneGrammarAgent)agent).geneGrammarMatch();
				}
			};

		default:
			System.err.println(AbstractGeneGrammarAgent.class.getName() + ": Unknown StatisticsType");
			return null;
		}
	}
	
	public int geneGrammarMatch(){
		int count = 0;
		for(int i = 0; i < getIntegerParameter(NUMBER_OF_MEANINGS); i++){
			if(chromosome.get(i).equals(grammar.get(i))){
				count++;
			}
		}
		return count;
	}
}
