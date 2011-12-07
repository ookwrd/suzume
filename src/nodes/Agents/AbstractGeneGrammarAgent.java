package nodes.Agents;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import nodes.Node;
import nodes.Agents.statisticaggregators.AbstractCountingAggregator;
import nodes.Agents.statisticaggregators.AbstractUniquenessAggregator;

import autoconfiguration.Configurable;

import simulation.RandomGenerator;

public abstract class AbstractGeneGrammarAgent extends AbstractGrammarAgent {

	protected enum VisualizationTypes {GENOTYPE, SINGLE_GENE, GENE_GRAMMAR_MATCH}
	protected enum StatisticsTypes {NUMBER_GENOTYPES, GENE_GRAMMAR_MATCH}
	
	protected ArrayList<Integer> chromosome;

	public AbstractGeneGrammarAgent(){
		setDefaultParameter(STATISTICS_TYPE, StatisticsTypes.values(), StatisticsTypes.values());
		setDefaultParameter(VISUALIZATION_TYPE, VisualizationTypes.values(), new Object[]{VisualizationTypes.GENOTYPE});
	}
	
	public AbstractGeneGrammarAgent(Configurable config, RandomGenerator randomGenerator) {
		super(config, randomGenerator);
		
		chromosome = new ArrayList<Integer>(getIntegerParameter(SEMANTIC_SPACE_SIZE));
		for (int i = 0; i < getIntegerParameter(SEMANTIC_SPACE_SIZE); i++) { // all alleles are initially set to a random value initially
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
			double range = getIntegerParameter(SYNTACTIC_SPACE_SIZE) - 1;
			c = new Color(
					Math.abs((int)((chromosome.get(0)*128+chromosome.get(1)*64+chromosome.get(2)*32+chromosome.get(3)*16)/range)),
					Math.abs((int)((chromosome.get(4)*128+chromosome.get(5)*64+chromosome.get(6)*32+chromosome.get(7)*16)/range)),
					Math.abs((int)((chromosome.get(8)*128+chromosome.get(9)*64+chromosome.get(10)*32+chromosome.get(11)*16)/range))
			);
		break;
		
		case SINGLE_GENE:
			c = mapValueToRainbow(chromosome.get(0), getIntegerParameter(SYNTACTIC_SPACE_SIZE));
			break;
			
		case GENE_GRAMMAR_MATCH:
			c = mapValueToWhiteGreen(geneGrammarMatch(), getIntegerParameter(SEMANTIC_SPACE_SIZE));
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
		for(int i = 0; i < getIntegerParameter(SEMANTIC_SPACE_SIZE); i++){
			if(chromosome.get(i).equals(grammar.get(i))){
				count++;
			}
		}
		return count;
	}
}
