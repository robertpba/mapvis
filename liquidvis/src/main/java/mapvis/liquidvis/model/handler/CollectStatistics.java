package mapvis.liquidvis.model.handler;

import mapvis.liquidvis.model.MapModel;
import mapvis.liquidvis.model.Polygon;
import mapvis.liquidvis.model.event.CriticalPointArrived;
import mapvis.liquidvis.model.event.IterationFinished;
import mapvis.liquidvis.model.event.ModelEvent;
import mapvis.liquidvis.model.event.VertexMoved;

import java.util.ArrayList;
import java.util.List;

public class CollectStatistics implements ModelEventListener {

    private MapModel model;
    private int period;

    public static class Statistics{
        public int iteration;
        public double error;
    }

    public List<Statistics> ls = new ArrayList<>();

    public CollectStatistics(MapModel model, int period){

        this.model = model;
        this.period = period;
    }

    public void onEvent(ModelEvent event) {
        /*if (event instanceof VertexMoved) {
            onVertexMoved((VertexMoved) event);
        }*/
        if (event instanceof CriticalPointArrived) {
            onIterationFinished(event);
            onCriticalPointArrived((CriticalPointArrived) event);
        } else {

            if (event.iteration % period != 0)
                return;

            if (event instanceof IterationFinished
                    || event instanceof CriticalPointArrived) {
                onIterationFinished(event);
            }
        }

    }


    private void onVertexMoved(VertexMoved event)
    {

    }

    private void onIterationFinished(ModelEvent event)
    {
        System.out.println("------------------");

        double e = 0;
        int n = 0;

        for (Polygon polygon : model.getPolygons().values()) {
            System.out.printf("d:%5.0f a:%7.0f, m:%7.0f %%:%6.3f\n",
                    (polygon.mass - polygon.area), polygon.area, polygon.mass,
                    (polygon.mass - polygon.area)/ polygon.mass * 100);
            e += (polygon.mass - polygon.area)/ polygon.mass * 100;
            n++;
        }

        Statistics stat = new Statistics();
        stat.iteration = event.iteration;
        stat.error = e/n;
        ls.add(stat);

        System.out.println("-------" + event.iteration + "---------");


    }
    private void onCriticalPointArrived(CriticalPointArrived event)
    {
        System.out.println("###"+event.desc+"###");
    }


}
