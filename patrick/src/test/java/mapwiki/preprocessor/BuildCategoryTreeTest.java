package mapwiki.preprocessor;

import mapwiki.common.Database;
import mapwiki.layout.Category;
import mapwiki.layout.HexagonLayout;
import org.testng.annotations.Test;

import java.util.Map;


public class BuildCategoryTreeTest {

    // 1.	Create a full tree from the database dump:
    @Test(enabled = false) public void testMain() throws Exception {
        Database db = new Database("jdbc:mysql://localhost:3306/simplewiki","root","root");

        int rootID = 137597;
        String rootTitle = "Articles";


        BuildCategoryTree bct = new BuildCategoryTree(rootID, rootTitle, db,
                BuildCategoryTree.PruneMode.BY_PAGE_ID, "");

        bct.outputFile = "full.txt";
        bct.run();

    }
    // 2. Generate a list of unwanted categories:
    @Test(enabled = false) public void testGenUselessList() throws Exception
    {
        GenUselessList.main("--tree full.txt -o blacklist.txt -k Stub Year List --sl 0.75".split(" "));
    }

    // 3.	Re-run the build tree routine again with category blacklist information:
    @Test(enabled = false) public void testBuildAgain() throws Exception
    {
        Database db = new Database("jdbc:mysql://localhost:3306/simplewiki","root","root");

        int rootID = 137597;
        String rootTitle = "Articles";


        BuildCategoryTree bct = new BuildCategoryTree(rootID, rootTitle, db,
                BuildCategoryTree.PruneMode.BY_PAGE_ID, "");
        bct.initBlacklist("blacklist.txt");
        bct.outputFile = "simple.txt";
        bct.run();
    }

    // 4.	Compute cosine similarity:
    @Test(enabled = false) public void ComputeCosineSimilarity() throws Exception
    {
        CalcCosSim2.main("--conn jdbc:mysql://localhost:3306/simplewiki -f simple.txt -u root -p root -o cossim.txt".split(" "));
    }

    // 5.	Aggregate similarity:
    @Test(enabled = false) public void Aggregatesimilarity() throws Exception
    {
        AggSimilarity.main("-t 1 -l 6 --tree simple.txt --sim cossim.txt -o aggsim.txt".split(" "));
    }

    // 6.	Generate page data (shown as cities in the visualization):
    @Test(enabled = false) public void GeneratePageData() throws Exception
    {
        BuildPageData.main("--tree simple.txt -o page.txt --conn jdbc:mysql://localhost:3306/simplewiki -u root -p root".split(" "));
    }

    // 7.	Execute the visualization algorithm:
    @Test(enabled = true) public void layoutGraphic() throws Exception
    {
        mapwiki.layout.LayoutGraphic
                .main(("--as data\\aggsim.txt  -l 3 -o simple1024.png" +
                        " -p data\\defaults.properties --sim data\\cossim.txt --tree data\\simple.txt" +
                        " --cr").split(" "));

        int one=0;
        int inf=0;
        int o = 0;

        double sum=0;
        for (Map.Entry<Category, Integer> entry : HexagonLayout.size.entrySet()) {

            Category c = entry.getKey();
            double size = HexagonLayout.computeCategorySize(c.getArticleCount());
            if (entry.getValue() == c.getArea()) {
                one++;

                sum += entry.getValue() / size;
                continue;
            }
            if (c.getArea() == 0) {
                inf++;
                continue;
            }

            o++;
            System.out.printf("%s, %d/%f, %f\n", c.getPageTitle(),
                    entry.getValue(), size, (double)entry.getValue() / size);
            sum += (double)entry.getValue() / size;
        }

        System.out.printf("1:%d, inf:%d, o:%d, a:%f", one, inf, o, (1-sum/(one+o))*100);

        //Thread.sleep(100000);
    }


}
