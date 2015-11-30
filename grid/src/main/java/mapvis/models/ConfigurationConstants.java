package mapvis.models;

/**
 * Created by dacc on 11/19/2015.
 */
public class ConfigurationConstants {

    //Rendering Method
    public static final boolean USE_REGION_RENDERING = true;

    //Region Rendering Smoothing
    public static final boolean USE_HIGH_QUALITY_SIMPLIFICATION = true;
    public static final float SIMPLIFICATION_TOLERANCE = 4.55f;
    public static final float BEZIER_CURVE_SMOOTHNESS = 0.3f;

    public enum RenderingMethod{
        Bezier,
        Quadric,
        Direct
    };

    public enum BoundaryShapeSmoothingMethod {
        DouglasPeucker,
        Average,
        None
    };

    public static final RenderingMethod RENDERING_METHOD_DEFAULT = RenderingMethod.Direct;
    public static final BoundaryShapeSmoothingMethod SIMPLIFICATION_METHOD_DEFAULT = BoundaryShapeSmoothingMethod.None;


    //UDC Tree: downloadable at http://www.udcdata.info/
    public static final String UDC_FILE_PATH = "D:/downloads/datasets/Libraries/UDC/udcsummary-skos.rdf";

    //Random Tree
    public static final int DEFAULT_RANDOM_TREE_SEED = 1;
    public static final int DEFAULT_RANDOM_TREE_WEIGHT = 100;
    public static final int DEFAULT_RANDOM_TREE_DEPTH = 3;
    public static final int DEFAULT_RANDOM_TREE_SPAN = 10;
}
