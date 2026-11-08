package filesystem;

public class Demo {
    public static void main(String[] args) {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode docs = new DirectoryNode("docs");
        DirectoryNode archive = new DirectoryNode("archive");
        DirectoryNode images = new DirectoryNode("images");

        FileNode readme = new FileNode("README.md", 120);
        FileNode report = new FileNode("report.txt", 450);
        FileNode notes = new FileNode("notes.txt", 80);
        FileNode oldReport = new FileNode("report-old.txt", 250);
        FileNode photo = new FileNode("photo.png", 1000);

        root.addChild(readme);
        root.addChild(docs);
        root.addChild(images);

        docs.addChild(report);
        docs.addChild(notes);
        docs.addChild(archive);

        archive.addChild(oldReport);
        images.addChild(photo);

        TotalSizeVisitor sizeVisitor = new TotalSizeVisitor();
        System.out.println("Total size: " + sizeVisitor.calculate(root) + " bytes");

        SearchVisitor searchVisitor = new SearchVisitor("report", 200);
        System.out.println("Search results:");
        for (String path : searchVisitor.search(root)) {
            System.out.println(path);
        }

        StatisticsVisitor statisticsVisitor = new StatisticsVisitor();
        statisticsVisitor.calculate(root);

        System.out.println("Files: " + statisticsVisitor.getFileCount());
        System.out.println("Directories: " + statisticsVisitor.getDirectoryCount());
        System.out.println("Max depth: " + statisticsVisitor.getMaxDepth());
    }
}
