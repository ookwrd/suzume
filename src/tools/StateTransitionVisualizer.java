package tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;

import javax.swing.JFrame;

import Launcher.Launcher;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.controls.DragControl;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import prefuse.data.Graph;
import prefuse.data.io.DataIOException;
import prefuse.data.io.GraphMLReader;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.visual.VisualItem;

public class StateTransitionVisualizer {
	
	private static void storeNet(double[][] stateTransitionsNormalized) {
		FileWriter fWriter = null;
		BufferedWriter writer = null;
		try {
			fWriter = new FileWriter("net.xml");
			writer = new BufferedWriter(fWriter);
			
			// header
			writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?> " +
					//"<!--  An excerpt of an egocentric social network  --> " +
					"<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\"> \n" +
					"<graph edgedefault=\"directed\"> \n" +
					//"<!-- data schema --> " +
					"<key id=\"name\" for=\"node\" attr.name=\"name\" attr.type=\"string\"/> \n" +
					"<key id=\"gender\" for=\"node\" attr.name=\"gender\" attr.type=\"string\"/> \n" +
					"<key id=\"weight\" for=\"edge\" attr.name=\"weight\" attr.type=\"double\"/> \n");
			writer.newLine();
			
			// nodes
			for(int i=0; i<stateTransitionsNormalized.length; i++) {
				String name = "";
				for(int j=0; j<stateTransitionsNormalized[i].length; j++) {
					name+=" "+j+":"+stateTransitionsNormalized[i][j];
				}
				writer.write("<node id=\"" + i +"\"> <data key=\"name\">"+
						( "state " + i + "<" + name+" >") + "</data> <data key=\"gender\">M</data> </node>");
				writer.newLine();
			}
			
			// edges
			DecimalFormat df = new DecimalFormat("########.00");
			for(int i=0; i<stateTransitionsNormalized.length; i++) {
				//System.out.println("state "+i);
				for(int j=0; j<stateTransitionsNormalized[0].length; j++) {
					//if (i!=j) 
					{
					writer.write("<edge source=\"" + i + "\" target=\"" + j + "\"> " +
							"<data key=\"weight\">" + stateTransitionsNormalized[i][j] +" </data> " +  
									"</edge>");
					//System.out.println("node"+i+"->"+j);
					writer.newLine();
					}
				}
			}
			
			System.out.println("edges done");
			
			// footer
			writer.write("</graph>\n</graphml>");
			writer.newLine();
			
			//System.out.println("footer done too. now closing...");
			
			writer.close();
		} catch (Exception e) {
			
		}

	}
	
    public static void render(double[][] stateTransitionsNormalized) {
        
    	// -- 0. save the data ------------------------------------------------
    	if(stateTransitionsNormalized!=null)
    		storeNet(stateTransitionsNormalized);
    	
        // -- 1. load the data ------------------------------------------------
        
        // load the socialnet.xml file. it is assumed that the file can be
        // found at the root of the java classpath
        Graph graph = null;
        try {
            graph = new GraphMLReader().readGraph(new File("net2.xml"));
        } catch ( DataIOException e ) {
            e.printStackTrace();
            System.err.println("Error loading graph. Exiting...");
            System.exit(1);
        }
        
        //TODO remove file net.xml
        
        // -- 2. the visualization --------------------------------------------
        
        // add the graph to the visualization as the data group "graph"
        // nodes and edges are accessible as "graph.nodes" and "graph.edges"
        Visualization vis = new Visualization();
        vis.add("graph", graph);
        vis.setInteractive("graph.edges", null, false);
        
        // -- 3. the renderers and renderer factory ---------------------------
        
        // draw the "name" label for NodeItems
        LabelRenderer r = new LabelRenderer("name");
        r.setRoundedCorner(8, 8); // round the corners
        
        // create a new default renderer factory
        // return our name label renderer as the default for all non-EdgeItems
        // includes straight line edges for EdgeItems by default
        vis.setRendererFactory(new DefaultRendererFactory(r));
        
        
        // -- 4. the processing actions ---------------------------------------
        
        // create our nominal color palette
        // pink for females, baby blue for males
        int[] palette = new int[] {
            ColorLib.rgb(255,180,180), ColorLib.rgb(190,190,255)
        };
        // map nominal data values to colors using our provided palette
        DataColorAction fill = new DataColorAction("graph.nodes", "gender",
                Constants.NOMINAL, VisualItem.FILLCOLOR, palette);
        // use black for node text
        ColorAction text = new ColorAction("graph.nodes",
                VisualItem.TEXTCOLOR, ColorLib.gray(0));
        // use light grey for edges
        ColorAction edges = new ColorAction("graph.edges",
                VisualItem.STROKECOLOR, ColorLib.gray(200));
        
        // create an action list containing all color assignments
        ActionList color = new ActionList();
        color.add(fill);
        color.add(text);
        color.add(edges);
        
        // create an action list with an animated layout
        ActionList layout = new ActionList(Activity.INFINITY);
        layout.add(new ForceDirectedLayout("graph"));
        layout.add(new RepaintAction());
        
        // add the actions to the visualization
        vis.putAction("color", color);
        vis.putAction("layout", layout);
        
        
        // -- 5. the display and interactive controls -------------------------
        
        Display d = new Display(vis);
        d.setSize(300, 300); // set display size
        
        // drag individual items around
        d.addControlListener(new DragControl());
        // pan with left-click drag on background
        d.addControlListener(new PanControl()); 
        // zoom with right-click drag
        d.addControlListener(new ZoomControl());
        
        // -- 6. launch the visualization -------------------------------------
        
        // create a new window to hold the visualization
        JFrame frame = new JFrame("State Transitions");
        // ensure application exits when window is closed
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(d);
        frame.pack();           // layout components in window
        frame.setVisible(true); // show the window
        
        // assign the colors
        vis.run("color");
        // start up the animated layout
        vis.run("layout");
    }
    
    public static void main(String[] args) {
    	//double[][] s = {{1,2},{1,1}};
    	//saveNet(s);
    	
		//Launcher.main(null);
    	render(null);
    	System.out.println("rendered");
	}
}