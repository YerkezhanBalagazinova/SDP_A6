package filesystem;

import java.util.ArrayList;
import java.util.List;

public class DirectoryNode implements Element {
    private final String name;
    private final List<Element> children = new ArrayList<>();
    DirectoryNode parent;

    public DirectoryNode(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Directory name cannot be empty.");
        }
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public List<Element> getChildren() {
        return List.copyOf(children);
    }

    public void addChild(FileNode file) {
        validateChild(file);

        if (file.parent != null) {
            throw new IllegalArgumentException("Element already has a parent.");
        }

        children.add(file);
        file.parent = this;
    }

    public void addChild(DirectoryNode directory) {
        validateChild(directory);

        if (directory.parent != null) {
            throw new IllegalArgumentException("Element already has a parent.");
        }

        DirectoryNode current = this;

        while (current != null) {
            if (current == directory) {
                throw new IllegalArgumentException("Cycle is not allowed.");
            }
            current = current.parent;
        }

        children.add(directory);
        directory.parent = this;
    }

    private void validateChild(Element child) {
        if (child == null) {
            throw new IllegalArgumentException("Child cannot be null.");
        }

        for (Element existing : children) {
            if (existing.getName().equals(child.getName())) {
                throw new IllegalArgumentException("Duplicate sibling name.");
            }
        }
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}