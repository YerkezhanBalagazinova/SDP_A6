package filesystem;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileSystemTest {
    @Test
    void doubleDispatchCallsCorrectVisitMethods() {
        FileNode file = new FileNode("test.txt", 100);
        DirectoryNode directory = new DirectoryNode("docs");
        RecordingVisitor visitor = new RecordingVisitor();

        file.accept(visitor);
        directory.accept(visitor);

        assertEquals(1, visitor.fileVisits);
        assertEquals(1, visitor.directoryVisits);
    }

    @Test
    void totalSizeAcrossNestedDirectoriesIsCorrect() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode docs = new DirectoryNode("docs");
        DirectoryNode archive = new DirectoryNode("archive");

        root.addChild(new FileNode("a.txt", 100));
        root.addChild(docs);
        docs.addChild(new FileNode("b.txt", 200));
        docs.addChild(archive);
        archive.addChild(new FileNode("c.txt", 300));

        TotalSizeVisitor visitor = new TotalSizeVisitor();

        assertEquals(600, visitor.calculate(root));
    }

    @Test
    void searchCombinesNameAndMinimumSize() {
        DirectoryNode root = new DirectoryNode("root");

        root.addChild(new FileNode("report-big.txt", 500));
        root.addChild(new FileNode("report-small.txt", 100));
        root.addChild(new FileNode("notes.txt", 800));

        SearchVisitor visitor = new SearchVisitor("report", 200);

        assertEquals(
                List.of("report-big.txt"),
                visitor.search(root)
        );
    }

    @Test
    void searchPathsAreRelativeAndSorted() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode z = new DirectoryNode("z");
        DirectoryNode a = new DirectoryNode("a");

        root.addChild(z);
        root.addChild(a);

        z.addChild(new FileNode("report-z.txt", 300));
        a.addChild(new FileNode("report-a.txt", 300));

        SearchVisitor visitor = new SearchVisitor("report", 200);

        assertEquals(
                List.of(
                        "a/report-a.txt",
                        "z/report-z.txt"
                ),
                visitor.search(root)
        );
    }

    @Test
    void statisticsForNestedTreeAreCorrect() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode docs = new DirectoryNode("docs");
        DirectoryNode archive = new DirectoryNode("archive");

        root.addChild(new FileNode("top.txt", 100));
        root.addChild(docs);
        docs.addChild(archive);
        archive.addChild(new FileNode("deep.txt", 200));

        StatisticsVisitor visitor = new StatisticsVisitor();
        visitor.calculate(root);

        assertEquals(2, visitor.getFileCount());
        assertEquals(3, visitor.getDirectoryCount());
        assertEquals(3, visitor.getMaxDepth());
    }

    @Test
    void emptyDirectoryAndNoMatchAreHandled() {
        DirectoryNode root = new DirectoryNode("root");

        TotalSizeVisitor sizeVisitor = new TotalSizeVisitor();
        assertEquals(0, sizeVisitor.calculate(root));

        SearchVisitor searchVisitor = new SearchVisitor("missing", 0);
        assertTrue(searchVisitor.search(root).isEmpty());

        StatisticsVisitor statisticsVisitor = new StatisticsVisitor();
        statisticsVisitor.calculate(root);

        assertEquals(0, statisticsVisitor.getFileCount());
        assertEquals(1, statisticsVisitor.getDirectoryCount());
        assertEquals(0, statisticsVisitor.getMaxDepth());
    }

    @Test
    void repeatedExecutionsDoNotKeepOldState() {
        DirectoryNode root = new DirectoryNode("root");
        root.addChild(new FileNode("report.txt", 100));

        TotalSizeVisitor sizeVisitor = new TotalSizeVisitor();
        assertEquals(100, sizeVisitor.calculate(root));
        assertEquals(100, sizeVisitor.calculate(root));

        SearchVisitor searchVisitor = new SearchVisitor("report", 0);
        assertEquals(1, searchVisitor.search(root).size());
        assertEquals(1, searchVisitor.search(root).size());

        StatisticsVisitor statisticsVisitor = new StatisticsVisitor();

        statisticsVisitor.calculate(root);
        assertEquals(1, statisticsVisitor.getFileCount());

        statisticsVisitor.calculate(root);
        assertEquals(1, statisticsVisitor.getFileCount());
    }

    @Test
    void invalidTreeChangesAreRejected() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new FileNode("bad.txt", -1)
        );

        DirectoryNode duplicateRoot = new DirectoryNode("root");
        duplicateRoot.addChild(new FileNode("same", 10));

        assertThrows(
                IllegalArgumentException.class,
                () -> duplicateRoot.addChild(new DirectoryNode("same"))
        );

        DirectoryNode first = new DirectoryNode("first");
        DirectoryNode second = new DirectoryNode("second");
        FileNode shared = new FileNode("shared.txt", 20);

        first.addChild(shared);

        assertThrows(
                IllegalArgumentException.class,
                () -> second.addChild(shared)
        );

        DirectoryNode parent = new DirectoryNode("parent");
        DirectoryNode child = new DirectoryNode("child");

        parent.addChild(child);

        assertThrows(
                IllegalArgumentException.class,
                () -> child.addChild(parent)
        );

        DirectoryNode validRoot = new DirectoryNode("valid");
        DirectoryNode nested = new DirectoryNode("nested");
        FileNode validFile = new FileNode("valid.txt", 50);

        validRoot.addChild(nested);
        nested.addChild(validFile);

        assertEquals(1, nested.getChildren().size());
    }

    private static class RecordingVisitor implements Visitor {
        int fileVisits;
        int directoryVisits;

        @Override
        public void visit(FileNode file) {
            fileVisits++;
        }

        @Override
        public void visit(DirectoryNode directory) {
            directoryVisits++;
        }
    }
}
