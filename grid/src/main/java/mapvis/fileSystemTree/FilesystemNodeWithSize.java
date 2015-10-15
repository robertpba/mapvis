package mapvis.fileSystemTree;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by dacc on 10/14/2015.
 */
public class FilesystemNodeWithSize extends File implements ITreeNodeWithCustomSize
{
    public FilesystemNodeWithSize(String filePath) {
        super(filePath);
    }

    @Override
    public List<ITreeNodeWithCustomSize> getChildren() {
        List<ITreeNodeWithCustomSize> result = Arrays.asList(this.listFiles())
                .stream()
                .map(file -> new FilesystemNodeWithSize(file.getPath()))
                .collect(Collectors.<ITreeNodeWithCustomSize>toList());
        return result;
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public double getNodeSize() {
        return super.length();
    }
}
