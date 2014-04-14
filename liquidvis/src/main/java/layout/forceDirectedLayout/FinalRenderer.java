package layout.forceDirectedLayout;
import java.awt.Shape;
 import java.awt.geom.Ellipse2D;
 import prefuse.render.AbstractShapeRenderer;
 import prefuse.visual.VisualItem;
 


public class FinalRenderer extends AbstractShapeRenderer
  {
    protected Ellipse2D m_box = new Ellipse2D.Double();

    protected Shape getRawShape(VisualItem item)
    {
      m_box.setFrame(item.getX(), item.getY(),(Integer) item.get("size"),(Integer) item.get("size"));
      //m_box.setFrame(item.getX(), item.getY(),(Integer) 30,(Integer) 30);
      return m_box;
    }
  }