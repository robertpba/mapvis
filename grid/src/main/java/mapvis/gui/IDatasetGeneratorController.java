package mapvis.gui;

import javafx.event.ActionEvent;
import mapvis.common.datatype.MPTreeImp;
import mapvis.common.datatype.Node;

/**
 * Created by dacc on 10/12/2015.
 */
public interface IDatasetGeneratorController {
    void setVisible(boolean isVisible);
    MPTreeImp<Node> generateTree(ActionEvent event);
    String toString();
}
