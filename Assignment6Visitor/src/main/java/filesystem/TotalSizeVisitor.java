package filesystem;

public class TotalSizeVisitor implements Visitor {
    private long totalSize;

    public long calculate(Element root) {
        totalSize = 0;
        root.accept(this);
        return totalSize;
    }

    @Override
    public void visit(FileNode file) {
        totalSize += file.getSize();
    }

    @Override
    public void visit(DirectoryNode directory) {
        for (Element child : directory.getChildren()) {
            child.accept(this);
        }
    }
}
