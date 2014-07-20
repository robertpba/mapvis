package mapvis.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class AppController implements Initializable {

    @FXML
    public GridPanel gridPanel;

    @FXML
    public Slider zoomSlider;

    @FXML
    public Text panFactorX;

    @FXML
    public Text panFactorY;

    public void test()
    {
        //zoomSlider.valueProperty().
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        zoomSlider.valueProperty()
                .bindBidirectional(gridPanel.zoomFactorProperty());
        panFactorX.textProperty()
                .bind(gridPanel.panFactorXProperty().asString());
        panFactorY.textProperty()
                .bind(gridPanel.panFactorYProperty().asString());
    }
}
