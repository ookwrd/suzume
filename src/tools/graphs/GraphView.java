package tools.graphs;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Stroke;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JFrame;
import edu.uci.ics.jung.algorithms.layout.*;

import model.ModelController;
import model.SimpleClustering;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.GradientEdgePaintTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public class GraphView {

	public DirectedGraph<Integer, ClusterLink> g;
	protected BasicVisualizationServer<Integer, String> vv;
	
	public GraphView(double[][] adjacencyMatrix, String name) {
		
		g = getSimpleGraph(adjacencyMatrix);
		
		// Layout<V, E>, BasicVisualizationServer<V,E>
		Layout<Integer, String> layout = new CircleLayout(g);
		//ISOMLayout layout = new edu.uci.ics.jung.algorithms.layout.ISOMLayout(g);
		
		layout.setSize(new Dimension(300, 300));
		vv = new BasicVisualizationServer<Integer, String>(
				layout);
		vv.setPreferredSize(new Dimension(350, 350));
		
        //vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        
		// Set up a new vertex to paint transformer...
		Transformer<Integer, Paint> vertexPaint = new Transformer<Integer, Paint>() {
			public Paint transform(Integer i) {
				return Color.WHITE;
			}
		};
		
		/*Transformer labelTransformer = new ChainedTransformer<String,String>(new Transformer[]{
	            new ToStringLabeller<String>(),
	            new Transformer<String,String>() {
	            public String transform(String input) {
	                return "<html><font color=\"yellow\">"+input;
	            }}});

		context.setVertexLabelTransformer(labelTransformer);*/
		
		// Set up coloring for edges... uh no it looks ugly
		//vv.getRenderContext().setEdgeFillPaintTransformer(new GradientEdgePaintTransformer(Color.blue, Color.red, new VisualizationViewer(new CircleLayout(g))));
		//vv.getRenderContext().setEdgeFontTransformer();
		
		vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
		//vv.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer);
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<Integer>());
		vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<String>());
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
		
		vv.setBackground(Color.white);
	}
	
	public BasicVisualizationServer<Integer, String> getVV() {
		return this.vv;
	}
	
	/**
	 * Create simple graph
	 * @param adjacencyMatrix 
	 * 
	 * @return
	 */
	public DirectedGraph<Integer, ClusterLink> getSimpleGraph(double[][] adjacencyMatrix) {
		DirectedGraph<Integer, ClusterLink> g = new DirectedOrderedSparseMultigraph<Integer, ClusterLink>();
		
		// add vertices
		ArrayList<Integer> nodes = new ArrayList<Integer>();
		nodes.add(0, new Integer(0));
		for (int i=1; i<adjacencyMatrix.length; i++) {
			Integer vertex = (Integer) i;
			System.out.println("Node "+i+": "+SimpleClustering.getCenter(i));
			g.addVertex(vertex);
			nodes.add(i, vertex);
		}
		nodes.remove(0);
		
		// add edges
		for (int i=1; i<adjacencyMatrix.length; i++) {
			for (int j=1; j<adjacencyMatrix[i].length; j++) {
				DecimalFormat df = new DecimalFormat("########.00");
				double p = adjacencyMatrix[i][j];
				if (p>=0.001) {
					String w = df.format(p);
					g.addEdge(new ClusterLink(w), nodes.get(i-1), nodes.get(j-1), EdgeType.DIRECTED);
				}
			}
		}
		
		System.out.println("The graph g = " + g.toString());
		
		return g;
	}
	
	public static void main(String[] args) {
		//ModelController.main(null);
		double[][] am = {
				{	3000.0,			4000.0,		4000.0,		4000.0},
				{	3000.1,		300.1,		2.1,	4.1},
				{	3000.2,		5.2,		2.2,	4.2},
				{	3000.3,		5.3,		2.3,	2.3}};
		GraphView gv = new GraphView(am, "Test");
		JFrame frame = new JFrame();
		frame.getContentPane().add(gv.getVV());

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		
		/*for(int i=0; i<101; i++) {
			System.out.println("{"+i+"000,"+(i+1)+"000},");
		}*/
	}

}
