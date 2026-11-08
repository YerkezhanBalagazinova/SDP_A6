package filesystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchVisitor implements Visitor {
    private final String namePart;
    private final long minimumSize;
    private final List<String> matches = new ArrayList<>();
    private Element root;

    public SearchVisitor(String namePart, long minimumSize) {
        if (namePart == null) {
            throw new IllegalArgumentException("Name criterion cannot be null.");
        }
        if (minimumSize < 0) {
            throw new IllegalArgumentException("Minimum size cannot be negative.");
        }
        this.namePart = namePart.toLowerCase();
        this.minimumSize = minimumSize;
    }

    public List<String> search(Element root) {
        matches.clear();
        this.root = root;
        root.accept(this);
        Collections.sort(matches);
        return List.copyOf(matches);
    }

    @Override
    public void visit(FileNode file) {
        boolean nameMatches = file.getName().toLowerCase().contains(namePart);
        boolean sizeMatches = file.getSize() >= minimumSize;

        if (nameMatches && sizeMatches) {
            matches.add(buildPath(file));
        }
    }

    @Override
    public void visit(DirectoryNode directory) {
        for (Element child : directory.getChildren()) {
            child.accept(this);
        }
    }

    private String buildPath(FileNode file) {
        List<String> parts = new ArrayList<>();
        parts.add(file.getName());

        DirectoryNode current = file.parent;

        while (current != null && current != root) {
            parts.add(current.getName());
            current = current.parent;
        }

        Collections.reverse(parts);
        return String.join("/", parts);
    }
}
