package mapvis.fileSystemTree;


import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by dacc on 10/8/2015.
 */
public class FileSystemNode extends File implements ITreeNode {

    public FileSystemNode(String filePath) {
        super(filePath);
    }

    @Override
    public List<ITreeNode> getChildren() {
        List<ITreeNode> result = Arrays.asList(this.listFiles())
                .stream()
                .filter(file -> file.isDirectory())
                .map(file1 -> new FileSystemNode(file1.getPath()))
                .collect(Collectors.<ITreeNode>toList());
        if(result.size() > 0){
            System.out.print("Children: ");
            result.stream().forEach(iTreeNode -> System.out.print(iTreeNode.getName() + ","));
            System.out.println();
        }

        return result;
    }

    @Override
    public String getName() {
        return super.getName();
    }
}
