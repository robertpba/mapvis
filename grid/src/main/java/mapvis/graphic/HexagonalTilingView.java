package mapvis.graphic;

import com.sun.javafx.geom.AreaOp;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Path;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import mapvis.common.datatype.INode;
import mapvis.common.datatype.Tree2;
import mapvis.graphic.HexagonRendering.HexagonTreeRender;
import mapvis.graphic.HexagonRendering.TileStyler;
import mapvis.graphic.RegionRendering.IRegionAreaStyler;
import mapvis.graphic.RegionRendering.IRegionStyler;
import mapvis.graphic.RegionRendering.ITreeVisualizationRenderer;
import mapvis.graphic.RegionRendering.RegionRenderer;
import mapvis.models.*;

import java.io.IOException;

public class HexagonalTilingView extends Pane {

    protected static final double COS30 = Math.cos(Math.toRadians(30));
    public static final double SideLength = 10;

    private ITreeVisualizationRenderer renderer;

    private Canvas canvas;

    private ObjectProperty<Grid<INode>> grid = new SimpleObjectProperty<>();
    private ObjectProperty<Tree2<INode>> tree = new SimpleObjectProperty<>();
    private ObjectProperty<TileStyler<INode>> tileStyler = new SimpleObjectProperty<>();
    private ObjectProperty<IRegionStyler<INode>> regionStyler = new SimpleObjectProperty<>();

    private ObjectProperty<ConfigurationConstants.RenderingMethod> renderingMethod = new SimpleObjectProperty<>();
    private ObjectProperty<ConfigurationConstants.SimplificationMethod> simplificationMethod = new SimpleObjectProperty<>();
    private DoubleProperty simplificationTolerance = new SimpleDoubleProperty(ConfigurationConstants.SIMPLIFICATION_TOLERANCE);
    private BooleanProperty useHQDouglasSimplification = new SimpleBooleanProperty(ConfigurationConstants.USE_HIGH_QUALITY_SIMPLIFICATION);


    private IntegerProperty maxLevelOfBordersToShow = new SimpleIntegerProperty(Integer.MAX_VALUE);
    private IntegerProperty maxLevelOfLabelsToShow = new SimpleIntegerProperty(Integer.MAX_VALUE);
    private IntegerProperty maxLevelOfRegionsToShow = new SimpleIntegerProperty(Integer.MAX_VALUE);

    private BooleanProperty areLabelsShown = new SimpleBooleanProperty(true);
    private DoubleProperty zoom = new SimpleDoubleProperty(1);
    private DoubleProperty originX = new SimpleDoubleProperty(0);
    private DoubleProperty originY = new SimpleDoubleProperty(0);
    private Group panel;


    public Group getPanel() {
        return panel;
    }

    public HexagonalTilingView(){
        System.out.println("Creating: " + this.getClass().getName());

        initHexagonTilingView();

        this.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::dragEntered);
        this.addEventHandler(MouseEvent.MOUSE_PRESSED, this::mousePressed);
        this.addEventHandler(MouseEvent.MOUSE_RELEASED, this::mouseReleased);
        this.addEventHandler(ScrollEvent.SCROLL, this::onScroll);

        originX.addListener(this::onOriginXChange);
        originY.addListener(this::onOriginYChange);
        zoom.addListener(this::onZoomChange);

        regionStyler.addListener(this::onRegionAreaStyler);

        areLabelsShown.addListener(this::onShowLabelsChanged);
        maxLevelOfBordersToShow.addListener(this::onBorderLevelsToShowChanged);
        maxLevelOfLabelsToShow.addListener(this::onLabelLevelsToShowChanged);
        maxLevelOfRegionsToShow.addListener(this::onRegionLevelsToShowChanged);
        renderingMethod.addListener(this::onRenderingMethodChanged);
        simplificationMethod.addListener(this::onBoundarySimplificationMethodChanged);
        useHQDouglasSimplification.addListener(this::onUseHQDouglasSimplificationChanged);
        simplificationTolerance.addListener(this::onSimplificationToleranceChanged);
    }

    private void initHexagonTilingView(){
        setPrefHeight(1000);
        setPrefWidth(1000);
//        this.setMaxWidth(500);
//        this.setMaxHeight(50);
        canvas = new Canvas();
        canvas.widthProperty().bind(this.widthProperty());
        canvas.heightProperty().bind(this.heightProperty());

        if(ConfigurationConstants.USE_REGION_RENDERING){
            renderer = new RegionRenderer(this, canvas);
        }else {
            renderer = new HexagonTreeRender(this, tileStyler, grid, tree);
        }
//        panel = new Group();
//        panel.prefHeight(1000);
//        panel.prefWidth(1000);
//
//        double width = getWidth();
//        double width1 = getLayoutBounds().getWidth();
//
//
//        Scene scene = new Scene(panel, 1000, 1000);

//
//        Path path = new Path();
//
//        getChildren().addAll(panel);
        getChildren().addAll(canvas);


    }

    public static Point2D hexagonalToPlain(int x, int y){
        double cx = x * 3 * SideLength / 2;
        double cy;
        cy = 2 * COS30 * SideLength * y;

        if (x % 2 != 0) {
            cy += COS30 * SideLength;
        }

        return new Point2D(cx, cy);
    }

    public static Point2D planeToHexagonal(double x, double y){
        double cx = x / 3 * 2 / SideLength;
        int nx = (int) Math.round(cx);
        int ny;

        if (nx%2 == 0) {
            ny = (int)Math.round(y / 2 / COS30 / SideLength);
        }else {
            ny = (int)Math.round((y - COS30 * SideLength) / 2 / COS30 / SideLength);
        }

        return new Point2D(nx, ny);
    }

    public Point2D localToPlane(double x, double y){
        double x1 = (x - originXProperty().get())/zoomProperty().get();
        double y1 = (y - originYProperty().get())/zoomProperty().get();
        return new Point2D(x1, y1);
    }

    public void updateHexagons() {

        GraphicsContext g = canvas.getGraphicsContext2D();

        if(ConfigurationConstants.USE_REGION_RENDERING){
            if(getRegionStyler() == null)
                return;
            g.setFill(getRegionStyler().getBackground());
        }else{
            if(getTileStyler() == null)
                return;
            g.setFill(getTileStyler().getBackground());
        }

        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
//
        Bounds rect = getLayoutBounds();
        double x0 = - originXProperty().get()/zoomProperty().get();
        double y0 = - originYProperty().get()/zoomProperty().get();
        double x1 = (getWidth() - originXProperty().get())/zoomProperty().get();
        double y1 = (getHeight()- originYProperty().get())/zoomProperty().get();

        Point2D tl = planeToHexagonal(x0, y0);
        Point2D br = planeToHexagonal(x1, y1);
        g.save();
//
        g.translate(originXProperty().get(), originYProperty().get());
        g.scale(zoomProperty().get(), zoomProperty().get());
//        panel.getChildren().clear();
//        panel.setTranslateX(originXProperty().get());
//        panel.setTranslateY(originYProperty().get());
//        panel.setScaleX(zoomProperty().get());
//        panel.setScaleY(zoomProperty().get());
        renderer.renderScene(tl, br);
//        renderer.renderScene(new Point2D(3, 3), new Point2D(3, 3));

        g.restore();
    }

    @FXML
    public void save(String filename) throws IOException {
//        renderer.save(filename);
    }


    public ObjectProperty<Grid<INode>> gridProperty() { return this.grid; }
    public final Grid<INode> getGrid() { return this.gridProperty().get(); }
    public final void setGrid(Grid<INode> grid) { this.gridProperty().set(grid); }

    public ObjectProperty<Tree2<INode>> treeProperty() { return this.tree; }
    public final Tree2<INode> getTree() { return this.treeProperty().get(); }
    public final void setTree(Tree2<INode> tree) { this.treeProperty().set(tree); }

    public ObjectProperty<TileStyler<INode>> tileStylerProperty() { return this.tileStyler; }
    public final TileStyler<INode> getTileStyler() { return this.tileStylerProperty().get(); }
    public final void setTileStyler(TileStyler<INode> tileStyler) { this.tileStylerProperty().set(tileStyler); }

    public IRegionStyler<INode> getRegionStyler() {
        return regionStyler.get();
    }
    public ObjectProperty<IRegionStyler<INode>> regionStylerProperty() {
        return regionStyler;
    }
    public void setRegionStyler(IRegionStyler<INode> regionStyler) {
        this.regionStyler.set(regionStyler);
    }

    public DoubleProperty zoomProperty() { return this.zoom; }
    public final double getZoom() { return this.zoomProperty().get(); }
    public final void setZoom(double zoom) { this.zoomProperty().set(zoom); }

    public DoubleProperty originXProperty() { return this.originX; }
    public final double getOriginX() { return this.originXProperty().get(); }
    public final void setOriginX(double x) { this.originXProperty().set(x); }

    public DoubleProperty originYProperty() { return this.originY; }
    public final double getOriginY() { return this.originYProperty().get(); }
    public final void setOriginY(double y) { this.originYProperty().set(y); }

    public BooleanProperty areLabelsShownProperty() { return this.areLabelsShown; }
    public final boolean getAreLabelsShownProperty() { return this.areLabelsShownProperty().get(); }
    public final void setAreLabelsShownProperty(boolean y) { this.areLabelsShownProperty().set(y); }

    public int getMaxLevelOfRegionsToShow() {
        return maxLevelOfRegionsToShow.get();
    }
    public void setMaxLevelOfRegionsToShow(int maxLevelOfRegionsToShow) {
        this.maxLevelOfRegionsToShow.set(maxLevelOfRegionsToShow);
    }
    public IntegerProperty maxLevelOfRegionsToShowProperty() {
        return maxLevelOfRegionsToShow;
    }

    public int getMaxLevelOfBordersToShow() {return maxLevelOfBordersToShow.get(); }
    public IntegerProperty maxLevelOfBordersToShowProperty() { return maxLevelOfBordersToShow;}
    public void setMaxLevelOfBordersToShow(int maxLevelOfBordersToShow) { this.maxLevelOfBordersToShow.set(maxLevelOfBordersToShow );    }

    public int getMaxLevelOfLabelsToShow() {return maxLevelOfLabelsToShow.get(); }
    public IntegerProperty maxLevelOfLabelsToShowProperty() {return maxLevelOfLabelsToShow; }
    public void setMaxLevelOfLabelsToShow(int maxLevelOfLabelsToShow) {this.maxLevelOfLabelsToShow.set(maxLevelOfLabelsToShow );    }

    public ConfigurationConstants.RenderingMethod getRenderingMethod() {
        return renderingMethod.get();
    }
    public ObjectProperty<ConfigurationConstants.RenderingMethod> renderingMethodProperty() {
        return renderingMethod;
    }
    public void setRenderingMethod(ConfigurationConstants.RenderingMethod renderingMethod) {
        this.renderingMethod.set(renderingMethod);
    }

    public ObjectProperty<ConfigurationConstants.SimplificationMethod> simplificationMethodProperty() {
        return simplificationMethod;
    }
    public ConfigurationConstants.SimplificationMethod getSimplificationMethod() {
        return simplificationMethod.get();
    }
    public void setSimplificationMethod(ConfigurationConstants.SimplificationMethod simplificationMethod) {
        this.simplificationMethod.set(simplificationMethod);
    }

    public DoubleProperty simplificationToleranceProperty() {
        return simplificationTolerance;
    }
    public void setSimplificationTolerance(double simplificationTolerance) {
        this.simplificationTolerance.set(simplificationTolerance);
    }

    public BooleanProperty useHQDouglasSimplificationProperty() {
        return useHQDouglasSimplification;
    }
    public void setUseHQDouglasSimplification(boolean useHQDouglasSimplification) {
        this.useHQDouglasSimplification.set(useHQDouglasSimplification);
    }

    public void zoom(double scale){
        Point2D center = new Point2D(getWidth() / 2, getHeight() / 2);
        zoom(center, scale);
    }
    public void zoomTo(double scale){
        Point2D center = new Point2D(getWidth() / 2, getHeight() / 2);
        zoom(center, scale/this.zoom.get());
    }
    public void pan(double dx, double dy){
        Point2D o0 = new Point2D(originX.get(), originY.get());
        Point2D o1 = new Point2D(originX.get()+dx, originY.get()+dy);
        pan(o0, o0, o1);
    }
    public void scrollTo(double x, double y){
        Point2D o0 = new Point2D(originX.get(), originY.get());
        Point2D o1 = new Point2D(x+getWidth()/2, y+getHeight()/2);
        pan(o0, o0, o1);
    }

    public void zoom(Point2D pivot, double scale){
        double dx  = (pivot.getX() - originX.get()) * (scale - 1);
        double dy  = (pivot.getY() - originY.get()) * (scale - 1);
        double x1  = originX.get() - dx;
        double y1  = originY.get() - dy;

        originX.set(x1);
        originY.set(y1);

        double s0 = zoom.get();
        double s1 = s0 * scale;
        zoom.set(s1);
    }
    public void pan(Point2D orig, Point2D from, Point2D to){
        double x = orig.getX();
        double y = orig.getY();
        double x1 = x + to.getX() - from.getX();
        double y1 = y + to.getY() - from.getY();
        originX.set(x1);
        originY.set(y1);
    }

    Transform translateTransform = Affine.translate(0, 0);
    Transform scaleTransform = Affine.scale(1, 1);

    //////////////////////////
    private void onZoomChange(ObservableValue<? extends Number> observable, Number oldValue, Number newValue){
        scaleTransform = Affine.scale(newValue.doubleValue(), newValue.doubleValue());
        updateHexagons();

    }
    private void onOriginXChange(ObservableValue<? extends Number> observable, Number oldValue, Number newValue){
        translateTransform = Affine.translate(newValue.doubleValue(), originY.get());
        updateHexagons();
    }
    private void onOriginYChange(ObservableValue<? extends Number> observable, Number oldValue, Number newValue){
        translateTransform = Affine.translate(originX.get(), newValue.doubleValue());
        updateHexagons();
    }
    private void onStyler(ObservableValue<? extends TileStyler<INode>> observable,
                                    TileStyler<INode> oldValue, TileStyler<INode> newValue){
        updateHexagons();
    }
    private void onRegionAreaStyler(ObservableValue<? extends IRegionAreaStyler<INode>> observable,
                                    IRegionAreaStyler<INode> oldValue, IRegionAreaStyler<INode> newValue){
        updateHexagons();
    }
    private void onShowLabelsChanged(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue){
        updateHexagons();
    }
    private void onBorderLevelsToShowChanged(ObservableValue<? extends Number> observable, Number oldValue, Number newValue){
        updateHexagons();
    }

    private void onLabelLevelsToShowChanged(ObservableValue<? extends Number> observable, Number oldValue, Number newValue){
        updateHexagons();
    }
    private void onRegionLevelsToShowChanged(ObservableValue<? extends Number> observable, Number oldValue, Number newValue){
        updateHexagons();
    }

    private void onRenderingMethodChanged(ObservableValue<? extends ConfigurationConstants.RenderingMethod> observable,
                                             ConfigurationConstants.RenderingMethod oldValue, ConfigurationConstants.RenderingMethod newValue){
        if(ConfigurationConstants.USE_REGION_RENDERING){
            RegionRenderer regionRenderer = (RegionRenderer) this.renderer;
            regionRenderer.setRenderingMethod(newValue);
            updateHexagons();
        }
    }
    private void onBoundarySimplificationMethodChanged(ObservableValue<? extends ConfigurationConstants.SimplificationMethod> observable,
                                          ConfigurationConstants.SimplificationMethod oldValue, ConfigurationConstants.SimplificationMethod newValue){
        if(ConfigurationConstants.USE_REGION_RENDERING){
            RegionRenderer regionRenderer = (RegionRenderer) this.renderer;
            regionRenderer.setBoundarySimplificationMethod(newValue);
            updateHexagons();
        }
    }
    private void onUseHQDouglasSimplificationChanged(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue){
        if(ConfigurationConstants.USE_REGION_RENDERING){
            RegionRenderer regionRenderer = (RegionRenderer) this.renderer;
            regionRenderer.setBoundarySimplificationAlgorithmSettings(this.simplificationTolerance.floatValue(), newValue);
            updateHexagons();
        }
    }

    private void onSimplificationToleranceChanged(ObservableValue<? extends Number> observable, Number oldValue, Number newValue){
        if(ConfigurationConstants.USE_REGION_RENDERING){
            RegionRenderer regionRenderer = (RegionRenderer) this.renderer;
            regionRenderer.setBoundarySimplificationAlgorithmSettings(newValue.floatValue(), useHQDouglasSimplification.get());
            updateHexagons();
        }
    }


    //////////////////////////

    double startX = -1, startY = -1;
    double saveOriginX, saveOriginY;
    double saveOrigScale = 1;

    boolean inDrag;
    boolean panButton;

    private void mousePressed(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();

        startX = x;
        startY = y;

        saveOriginX = originX.get();
        saveOriginY = originY.get();
        saveOrigScale = zoom.get();

        panButton = e.isPrimaryButtonDown();

        inDrag = true;
    }

    private void mouseReleased(MouseEvent e) {
        inDrag = false;
    }
    private void dragEntered(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();

        pan(new Point2D(saveOriginX, saveOriginY),
                new Point2D(startX, startY),
                new Point2D(x, y));

    }
    private void onScroll(ScrollEvent event) {
        double scale =  1 + event.getDeltaY() / 40/10;

        Point2D pivot = new Point2D(event.getX(), event.getY());

        zoom(pivot, scale);
    }

    public void setRootRegion(Region<INode> rootRegion) {
        this.renderer.configure(rootRegion);
    }

    public Canvas getCanvas() {
        return canvas;
    }
}
