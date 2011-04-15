package tools.graphs;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Stroke;
import java.text.DecimalFormat;

import javax.swing.JFrame;

import model.ModelController;

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

	public DirectedGraph<Integer, String> g;
	
	public GraphView(double[][] adjacencyMatrix, String name) {
		
		g = getSimpleGraph(adjacencyMatrix);
		
		// Layout<V, E>, BasicVisualizationServer<V,E>
		Layout<Integer, String> layout = new CircleLayout(g);
		
		layout.setSize(new Dimension(300, 300));
		BasicVisualizationServer<Integer, String> vv = new BasicVisualizationServer<Integer, String>(
				layout);
		vv.setPreferredSize(new Dimension(350, 350));
		
		// Set up a new vertex to paint transformer...
		Transformer<Integer, Paint> vertexPaint = new Transformer<Integer, Paint>() {
			public Paint transform(Integer i) {
				return Color.WHITE;
			}
		};
		
		// Set up coloring for edges 
		//vv.getRenderContext().setEdgeFillPaintTransformer(new GradientEdgePaintTransformer(Color.blue, Color.red, new VisualizationViewer(new CircleLayout(g))));
		//vv.getRenderContext().setEdgeFontTransformer();
		
		vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
		//vv.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer);
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<Integer>());
		vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<String>());
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);

		JFrame frame = new JFrame(name);
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(vv);
		frame.pack();
		frame.setVisible(true);
	}
	
	/**
	 * Create simple graph
	 * @param adjacencyMatrix 
	 * 
	 * @return
	 */
	public DirectedGraph<Integer, String> getSimpleGraph(double[][] adjacencyMatrix) {
		DirectedGraph<Integer, String> g = new DirectedOrderedSparseMultigraph<Integer, String>();
		
		// add vertices
		for (int i=1; i<adjacencyMatrix.length; i++) {
			g.addVertex((Integer) i);
		}
		
		// add edges
		for (int i=1; i<adjacencyMatrix.length; i++) {
			for (int j=1; j<adjacencyMatrix[i].length; j++) {
				
				DecimalFormat df = new DecimalFormat("########.00");
				double p = adjacencyMatrix[i][j];
				if (p>=0.1) {
					String w = df.format(p);
					g.addEdge("("+i+","+j+"): "+w, i, j, EdgeType.DIRECTED);
				}
			}
		}
		
		System.out.println("The graph g = " + g.toString());
		
		return g;
	}
	
	public static void main(String[] args) {
		ModelController.main(null);
		/*double[][] am = {{1.0,2.0},{3.0,5.0}};
		GraphView gv = new GraphView(am, "Test");*/
	}

}
