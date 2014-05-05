package mapvis.liquidvis.method.method3;

import mapvis.liquidvis.model.MapModel;
import mapvis.liquidvis.model.Polygon;
import mapvis.liquidvis.model.Vertex;
import mapvis.liquidvis.model.event.CriticalPointArrived;
import mapvis.liquidvis.model.event.IterationFinished;
import mapvis.liquidvis.model.event.VertexMoved;

import java.awt.geom.Point2D;
import java.util.*;

import static mapvis.common.PointExtension.*;

public class Method3<T> {

    public final Estimator estimator;
    public final Manipulator manipulator;
    public MapModel<T> model;

    DriveAwayInsideVertices driveAwayInsideVertices;

    public Method3(MapModel<T> model){
        this.model = model;

        driveAwayInsideVertices = new DriveAwayInsideVertices(this, 100);
        model.listeners.add(driveAwayInsideVertices);
        //model.listeners.add(new MovePivot(this, 100));

        estimator = new Estimator(model);
        manipulator = new Manipulator(model);
    }


    public void IterateUntilStable(int maxIteration) {
        maxIteration += model.iteration;
        while (IterateOnce() && model.iteration < maxIteration)
        {
        }
        model.fireModelEvent(new CriticalPointArrived(model.iteration, "iterations finished"));
    }

    public boolean IterateOnce() {
        boolean stable = true;

        for (T leaf : model.getLeaves()) {
            Polygon polygon = model.getPolygon(leaf);
            for (Vertex vertex : polygon.vertices) {
                vertex.momentum = vertex.momentum * 99 / 100;
            }

            if (polygon.area >= polygon.mass)
                continue;

            List<Pair<Vertex, Integer>> scores = getScoresOfAllMove(polygon);

            for(int i =0; i<1;i++){
                Vertex vertex = scores.get(0).x;
                manipulator.moveForth(vertex);
            }

            stable = false;
        }

        model.fireModelEvent(new IterationFinished(model.iteration));

        return  !stable;
    }

    public static class Pair<X, Y> {
        public X x;
        public Y y;
        public Pair(X x, Y y) {
            this.x = x;
            this.y = y;
        }

    }

    public List<Pair<Vertex, Integer>> getScoresOfAllMove(Polygon polygon) {
        List<Pair<Vertex, Integer>> scores = new ArrayList<>();

        for (int iPoint = 0; iPoint < polygon.npoints; iPoint++) {
            Vertex vtx = polygon.getVertex(iPoint);
            int score = estimator.estimate(vtx);
            scores.add(new Pair<>(vtx, score));
        }

        Collections.sort(scores,
                (o1, o2) -> o1.y - o2.y);
        return scores;
    }

    public void growPolygons() {
        model.listeners.remove(driveAwayInsideVertices);

        for (int i = 0; i < 1; i++) {
            _growPolygons();
        }
        model.fireModelEvent(new CriticalPointArrived(model.iteration, "growing finished"));
        //restorePos();
        model.fireModelEvent(new CriticalPointArrived(model.iteration, "shrinking finished"));
    }

    private void restorePos() {
        for (Map.Entry<Vertex, Point2D> entry : origPos.entrySet()) {
            model.fireModelEvent(new VertexMoved(model.iteration,
                    entry.getKey(), entry.getKey().getPoint(), entry.getValue()));
        }
    }

    public Map<Vertex, Point2D> origPos = new HashMap<>();
    public Map<Point2D, ArrayList<Vertex>> joints = new HashMap<>();


    private void _growPolygons(){
        for (T leaf : model.getLeaves()) {
            Polygon polygon = model.getPolygon(leaf);
            for (Vertex vertex : polygon.vertices) {

                Point2D srcPos = vertex.getPoint();
                Point2D unit = unit(subtract(srcPos, polygon.getPivot()));
                Point2D dstPos = add(srcPos, unit);

                Polygon dstRegion = model.findSurroundingRegion(dstPos, (T)polygon.node);

                if (dstRegion == null) {
                    vertex.moveCount++;
                    vertex.momentum = 0;

                    if (!origPos.containsKey(vertex))
                        origPos.put(vertex, vertex.getPoint());

                    model.fireModelEvent(new VertexMoved(model.iteration, vertex, srcPos, dstPos));
                } else {
                    origPos.remove(vertex);

                    if (joints.containsValue(vertex.getPoint())
                            && joints.get(vertex.getPoint()).contains(vertex))
                        continue;

                    Vertex nearestVertex = model.findNearestVertex(srcPos, dstRegion);
                    Point2D matchedPoint = nearestVertex.getPoint();

                    ArrayList<Vertex> joint = joints.remove(matchedPoint);

                    if(joint == null){
                        joint = new ArrayList<>();
                        joint.add(nearestVertex);
                    }

                    joint.add(vertex);
                    dstPos = midpoint(srcPos, matchedPoint);
                    joints.put(dstPos, joint);

                    for (Vertex vertex1 : joint) {
                        model.fireModelEvent(new VertexMoved(model.iteration,
                                vertex1, vertex1.getPoint(), dstPos));
                    }

                }
            }
        }





    }


}






