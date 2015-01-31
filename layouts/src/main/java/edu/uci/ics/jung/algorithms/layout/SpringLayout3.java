package edu.uci.ics.jung.algorithms.layout;

import edu.uci.ics.jung.algorithms.layout.SpringLayout2;
import edu.uci.ics.jung.graph.Graph;
import org.apache.commons.collections15.Transformer;

import java.awt.geom.Point2D;
import java.util.ConcurrentModificationException;
import java.util.Random;

public class SpringLayout3<V, E> extends SpringLayout2<V, E> {
	private Random rand;
	
	protected double random() {
		return rand == null ? Math.random() : rand.nextDouble();
	}

	public SpringLayout3(Graph<V, E> g, Transformer<E, Integer> lengthFunction, Random rand) {
		super(g, lengthFunction);
		this.setInitializer(v-> new Point2D.Double(random(),random()));
		this.rand = rand;
	}

	public SpringLayout3(Graph<V, E> g, Random rand) {
		super(g);
		this.rand = rand;
	}

	/**
	 * This following code is copied from the original JUNG source code, with the change on
	 * the random method call. Here should be updated when a new version is released.
	 */
	@Override
	protected void calculateRepulsion() {
		try {
	        for (V v : getGraph().getVertices()) {
	            if (isLocked(v)) continue;

	            SpringVertexData svd = springVertexData.get(v);
	            if(svd == null) continue;
	            double dx = 0, dy = 0;

	            for (V v2 : getGraph().getVertices()) {
	                if (v == v2) continue;
	                Point2D p = transform(v);
	                Point2D p2 = transform(v2);
	                if(p == null || p2 == null) continue;
	                double vx = p.getX() - p2.getX();
	                double vy = p.getY() - p2.getY();
	                double distanceSq = p.distanceSq(p2);
	                if (distanceSq == 0) {
	                    dx += random();
	                    dy += random();
	                } else if (distanceSq < repulsion_range_sq) {
	                    double factor = 1;
	                    dx += factor * vx / distanceSq;
	                    dy += factor * vy / distanceSq;
	                }
	            }
	            double dlen = dx * dx + dy * dy;
	            if (dlen > 0) {
	                dlen = Math.sqrt(dlen) / 2;
	                svd.repulsiondx += dx / dlen;
	                svd.repulsiondy += dy / dlen;
	            }
	        }
	        } catch(ConcurrentModificationException cme) {
	            calculateRepulsion();
	        }
	}
}
