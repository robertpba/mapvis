package mapwiki.layout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Point;
import java.awt.geom.Point2D;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout2;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;

public class TestSpring {
	public static final int LOOP_TIMES = 3000;
	
	private static final Node[] nodes = new Node[] {
		new Node("C", Color.BLACK, 200, 200),
		new Node("1", Color.RED, 150, 50),
		new Node("2", Color.GREEN, 50, 150),
		new Node("3", Color.BLUE, 250, 200),
		new Node("4", Color.YELLOW, 150, 150),
		new Node("5", Color.PINK, 180, 180),
	};
	
	private static final Edge[] edges = new Edge[] {
		new Edge(nodes[1], nodes[2], 50),
		new Edge(nodes[1], nodes[3], 25),
		new Edge(nodes[2], nodes[3], 90),
		new Edge(nodes[4], nodes[5], 60),
		new Edge(nodes[5], nodes[3], 120),
	};
	
	public static void main(String[] args) {
		Graph<Node, Edge> g = new UndirectedSparseGraph<Node, Edge>();
		SpringLayout<Node, Edge> sl = new SpringLayout2<Node, Edge>(g, new MyLengthClass());
		sl.setSize(new Dimension(400, 400));
		circleArrange();
		for (int i = 0; i < nodes.length; i++) {
			g.addVertex(nodes[i]);
			if (i == 0)
				sl.lock(nodes[i], true);
			sl.setLocation(nodes[i], nodes[i].pt);
		}
		for (Edge entry: edges)
			g.addEdge(entry, entry.node1, entry.node2);
		for (int i = 1; i < nodes.length; i++)
			g.addEdge(new Edge(nodes[0], nodes[i], 50), nodes[0], nodes[1]);
		
		BasicVisualizationServer<Node, Edge> vs =
			new BasicVisualizationServer<Node, Edge>(sl);
		vs.setPreferredSize(new Dimension(400, 400));
		vs.getRenderContext().setVertexLabelTransformer(new Transformer<Node, String>() {
			@Override
			public String transform(Node arg0) {
				return arg0.name;
			}
		});
		vs.getRenderContext().setVertexFillPaintTransformer(new Transformer<Node, Paint>() {
			@Override
			public Paint transform(Node arg0) {
				return arg0.colour;
			}
		});
		
		JFrame frame = new JFrame("Spring Algorithm - Test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(vs);
		frame.pack();
		frame.setVisible(true);
		frame.setSize(450, 450);
		
		sl.setRepulsionRange(5);
		int loopCount = 0;
		while (!sl.done() && loopCount++ < LOOP_TIMES) {
			sl.step();
		}
		
		for (Node n: nodes) {
			System.out.println(n.name + "\t" + n.pt + "\t" +
					new Point2D.Double(sl.getX(n), sl.getY(n)));
		}
	}
	
	private static void circleArrange() {
		Point center = new Point(200, 200);
		int r = 100;
		int a = 360 / 5;
		
		for (int i = 1; i <= 5; i++) {
			double x = center.x + Math.cos(Math.toRadians(a * i)) * r;
			double y = center.y + Math.sin(Math.toRadians(a * i)) * r;
			nodes[i].pt.x = (int)x;
			nodes[i].pt.y = (int)y;
		}
	}

	static class Node {
		String name;
		Color colour;
		Point pt;
		
		public Node(String name, Color c, int x, int y) {
			this.name = name;
			this.colour = c;
			this.pt = new Point(x, y);
		}
	}
	
	static class Edge {
		Node node1;
		Node node2;
		Integer length;
		
		public Edge(Node node1, Node node2, Integer length) {
			this.node1 = node1;
			this.node2 = node2;
			this.length = length;
		}
	}
	
	static class MyLengthClass implements Transformer<Edge, Integer> {
		@Override
		public Integer transform(Edge arg0) {
			return arg0.length;
		}
	}
}
