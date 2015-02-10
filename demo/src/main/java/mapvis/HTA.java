package mapvis;


import mapwiki.layout.Category;
import mapwiki.layout.CategoryProvider;
import mapwiki.layout.LayoutGraphic;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class HTA {



    public static void main(String[] args) throws IOException {
        LayoutGraphic.props.load(new FileInputStream("patrick/data/defaults.properties"));

        //layoutDepth = (Integer)options.valueOf("l");
        //outputFile = (String)options.valueOf("o");
        //LayoutGraphic.tmpDir = (String)options.valueOf("tmp");
        //LayoutGraphic.exportFile = options.has("exp") ? (String)options.valueOf("exp") : null;


        LayoutGraphic.affectRange = LayoutGraphic.propertyAsInteger("AFFECT_RANGE");
        LayoutGraphic.areaFrac = Double.parseDouble(LayoutGraphic.propertyAsString("AREA_FRACTION"));





    }
}
