# Assignment 6 – Visitor Pattern

## About the Project

This project demonstrates the Visitor Design Pattern using an in-memory hierarchical file system.

The file system contains two element types:

- `FileNode` – stores a file name and size
- `DirectoryNode` – stores files and other directories

The main idea of the project is to keep additional operations outside of the element classes.

Instead of adding search, size calculation, or statistics directly into `FileNode` and `DirectoryNode`, these operations are implemented in separate Visitor classes.

## Visitors

The project contains three visitors:

- `TotalSizeVisitor` – calculates the total size of all files in a subtree
- `SearchVisitor` – searches for files by filename criterion and minimum size
- `StatisticsVisitor` – counts files, directories, and maximum tree depth

Each element implements:

```java
accept(Visitor visitor)
```

and passes itself to the visitor:

```java
visitor.visit(this);
```

This is the main double dispatch mechanism used in the Visitor Pattern.

## Project Structure

```text
src
├── main
│   └── java
│       └── filesystem
│           ├── Element.java
│           ├── Visitor.java
│           ├── FileNode.java
│           ├── DirectoryNode.java
│           ├── TotalSizeVisitor.java
│           ├── SearchVisitor.java
│           ├── StatisticsVisitor.java
│           └── Demo.java
│
└── test
    └── java
        └── filesystem
            └── FileSystemTest.java
```

## Example File System

The demo uses a nested structure like this:

```text
root
├── README.md
├── docs
│   ├── report.txt
│   ├── notes.txt
│   └── archive
│       └── report-old.txt
└── images
    └── photo.png
```

The same object structure is used by all three visitors.

## Example Output

```text
Total size: 1900 bytes
Search results:
docs/archive/report-old.txt
docs/report.txt
Files: 5
Directories: 4
Max depth: 3
```

## Testing

The project includes eight JUnit 5 tests.

They check:

- double dispatch
- total size calculation
- search conditions
- root-relative and sorted paths
- statistics
- empty structures
- repeated visitor execution
- invalid tree changes

All tests pass successfully.

```text
Tests run: 8
Failures: 0
Errors: 0
Skipped: 0
```

## How to Run Tests

From the project root, run:

```bash
mvn test
```

## How to Run the Application

Open `Demo.java` and run the `main()` method in IntelliJ IDEA.

## Main Design Idea

The element classes only store the file system structure and accept visitors.

The operation logic is kept inside the visitor classes.

Because of this, new operations can be added by creating new Visitor classes without changing `FileNode` or `DirectoryNode`.
