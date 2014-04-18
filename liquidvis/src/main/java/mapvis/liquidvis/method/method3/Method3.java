package mapvis.liquidvis.method.method3;

import mapvis.liquidvis.model.MapModel;
import mapvis.liquidvis.model.Polygon;
import mapvis.liquidvis.model.Vertex;
import mapvis.liquidvis.model.event.IterationFinished;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Method3 {

    public final Estimator estimator;
    public final Manipulator manipulator;
    public MapModel model;

    public Method3(MapModel model){
        this.model = model;
        model.listeners.add(new DriveAwayInsideVertices(this, 100));
        model.listeners.add(new MovePivot(this, 100));

        estimator = new Estimator(model);
        manipulator = new Manipulator(model);
    }


    public void IterateUntilStable(int maxIteration) {
        maxIteration += model.iteration;
        while (IterateOnce() && model.iteration < maxIteration)
        {
        }
    }

    public boolean IterateOnce() {
        boolean stable = true;

        for (Polygon polygon : model.getPolygons().values()) {
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

}
