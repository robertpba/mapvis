package mapvis.gui;

import javafx.event.ActionEvent;
import mapvis.common.datatype.INode;
import mapvis.common.datatype.MPTreeImp;

import java.io.FileNotFoundException;

/**
 * Created by dacc on 10/12/2015.
 */
public interface IDatasetGeneratorController {

    void setVisible(boolean isVisible);

    MPTreeImp<INode> generateTree(ActionEvent event) throws Exception;

    /**
     * @return the name of the generated Tree type
     */
    String toString();
}
