package mapvis.liquidvis.method.method2;

import mapvis.liquidvis.model.MapModel;
import mapvis.liquidvis.model.Polygon;
import mapvis.liquidvis.model.Vertex;
import mapvis.liquidvis.model.event.IterationFinished;
import mapvis.liquidvis.model.Node;
import mapvis.liquidvis.model.Vector2D;
import mapvis.liquidvis.model.event.VertexMoved;

import java.util.*;
import java.util.stream.Collectors;

public class Method2 {
    public MapModel model;
    public Map<Vector2D, ArrayList<Vertex>> joints = new HashMap<>();
    private List<PolygonDescriptor> descriptors;
    private Map<Polygon, PolygonDescriptor> descriptorMap;

    public class PolygonDescriptor {
        public Node      node;
        public Polygon   polygon;

        public int       iteration;
        public int       elevation;
        public boolean[] done;

        public PolygonDescriptor(Polygon polygon, int iteration) {
            this.node = polygon.node;
            this.polygon = polygon;
            this.iteration = iteration;
            this.done = new boolean[polygon.npoints];
        }
    }



    public Method2(MapModel model){
        this.model = model;

        descriptors = model.getPolygons().values().stream()
                .map(polygon -> new PolygonDescriptor(polygon, (int) polygon.figure * 3))
                .collect(Collectors.toList());

        descriptorMap = descriptors.stream()
                .collect(Collectors.toMap(x -> x.polygon, x -> x));
    }

    public void IterateUntilStable(int maxIteration) {
        maxIteration += model.iteration;
        while (IterateOnce() && model.iteration < maxIteration)
        {
        }
    }

    public boolean IterateOnce() {
        boolean stable = true;

        for (PolygonDescriptor descriptor : descriptors) {
            Polygon polygon = descriptor.polygon;

            if (descriptor.iteration <= 0)
                continue;
            descriptor.iteration -= 2;

            stable=true;
            for (int iPoint=0; iPoint<polygon.npoints; iPoint++) {
                if (descriptor.done[iPoint])
                    continue;

                stable=false;

                Vector2D srcPos  = polygon.getVertex(iPoint).getPoint();
                Vector2D unit    = Vector2D.subtract( srcPos, polygon.getOrigin()).unit();
                Vector2D dstPos = Vector2D.add(srcPos, unit);

                Polygon dstPolygon = model.findSurroundingRegion(dstPos, polygon.node.parent.parent, polygon.node);

                if (dstPolygon == null){
                    Vertex vertex = polygon.getVertex(iPoint);
                    model.fireModelEvent(new VertexMoved(model.iteration, vertex, srcPos, dstPos));

                    continue;
                }

                double minDistance = srcPos.distance(dstPolygon.getVertex(0).getPoint());
                int minPosition=0;

                for (int f=1; f<dstPolygon.npoints; f++){
                    double d = srcPos.distance(dstPolygon.getVertex(f).getPoint());
                    if (d<minDistance){
                        minDistance = d;
                        minPosition = f;
                    }
                }

                Vector2D matchedPoint = dstPolygon.getVertex(minPosition).getPoint();
                dstPos = Vector2D.average(srcPos, matchedPoint);

                ArrayList<Vertex> joint = joints.remove(matchedPoint);

                if(! descriptorMap.get(dstPolygon).done[minPosition]){
                    descriptorMap.get(dstPolygon).done[minPosition]=true;
                    joint = new ArrayList<>();
                    joint.add(dstPolygon.getVertex(minPosition));
                }

                joint.add(new Vertex(polygon, iPoint));

                descriptor.done[iPoint]=true;
                joints.put(dstPos, joint);

                for(Vertex vertex : joint){
                    model.fireModelEvent(new VertexMoved(model.iteration, vertex, srcPos, dstPos));
                }
            }

            if (stable)
                descriptor.elevation++;
        }

        IterationFinished event = new IterationFinished(model.iteration);
        model.fireModelEvent(event);

        return  !stable;
    }
}