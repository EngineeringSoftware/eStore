# In-memory Object Graph Stores

Implementation of an in-memory object graph store, dubbed ϵStore. Our
key innovation is a storage model -- epsilon store -- that equates an
object on the heap to a node in a graph store. Thus any object on the
heap (without changes) can be a part of one, or multiple, graph
stores, and vice versa, any node in a graph store can be accessed like
any other object on the heap. ϵStore uses a subset of the Cypher query
language to query the graph store. By design, the result of any query
is a table of references to objects on the heap, which users can
manipulate the same way as any other object on the heap in their
programs.

## Examples

1. Capturing a Java object graph and querying it with Cypher-like syntax.

   ```java
   Person charlie = new Person("Charlie", 25);
   Person bob = new Person("Bob", 30, charlie);
   Person alice = new Person("Alice", 28, bob);

   Estore db = new Estore("exampleDb", new EstoreOptions().useUnsafe(false));
   db.captureAll(alice);
   Table result = db.query("MATCH (p:`org.estore.example.Person`) RETURN p");
   ```

   `alice` is an ordinary Java `Person` object (name `"Alice"`, age 28). Its
   `friend` field points to Bob, and Bob's `friend` field points to Charlie, so
   the in-memory graph is Alice → Bob → Charlie. `captureAll(alice)` walks that
   graph from Alice and stores every reachable object; the query then returns
   the captured `Person` nodes.

2. Querying object relationships.

   ```java
   Table friends =
       db.query("MATCH (a:`org.estore.example.Person`)-[:friend]->(b:`org.estore.example.Person`) RETURN a, b");
   ```

   This query follows `friend` references between captured `Person` objects and
   returns each matched pair.

## Using ϵStore in a Maven Project

After packaging (see the next section), ϵStore can be used in a Maven
project.

The client jar can be added as a dependency to a third-party project
by adding the following to its pom.

```xml
  <dependency>
    <groupId>org.estore</groupId>
    <artifactId>estore</artifactId>
    <version>1.0.0</version>
    <scope>system</scope>
    <systemPath><!-- ENTER full path to client jar including jar name --></systemPath>
  </dependency>
```

## Getting Started (with Development)

### Prerequisites

The project requires the following dependencies:

- **Java 8**
- **Maven**
- **wget**
- **zstd**
- **tar**
- **gzip**
- **nc** for the quick query test

### Installation

#### 1. Install Dependencies

Run the installation script to automatically install all required dependencies:

```bash
./s install_deps
```

This will check for and install any missing dependencies on your system.

Alternatively, verify your dependencies are correctly installed:

```bash
./s check_deps
```

On Debian/Ubuntu, run `install_deps` with `sudo`. On macOS, use Homebrew
instead (`brew install openjdk@8 maven wget zstd`).

#### 2. Build the Project

Compile the estore project:

```bash
./s compile_estore
```

#### 3. Full Installation

To compile and install the complete project:

```bash
./s install_estore
```

### Running the Project

#### Run Tests

Execute the test suite:

```bash
mvn -pl estore test verify
```

The JaCoCo code coverage report is generated at `estore/target/site/jacoco/index.html`.

#### Run the Application

Start the estore server and query it over the network:

```bash
./s exec_estore
```

In another terminal, send a Cypher-like query over TCP (default port 1234):

```bash
echo 'MATCH (n) RETURN n' | nc localhost 1234
```

Example output:

```
╔═════════╗
║ n       ║
╠═════════╣
║ (empty) ║
╚═════════╝
```

Send `q` to stop the server.

#### Format Code

Auto-format Java code according to project standards:

```bash
mvn spotless:apply
mvn verify
```

#### End-to-End Setup

Perform a complete setup with dependency checks and full installation:

```bash
./s end_to_end
```

## Citation

This repository contains code related to the following publication:

```bibtex
@inproceedings{ThimmaiahETAL25eStore,
  author = {Thimmaiah, Aditya and Yi, Zijian and Kenis, Joseph and Rossbach, Christopher J. and Gligoric, Milos},
  title = {In-memory Object Graph Stores},
  booktitle = {European Conference on Object-Oriented Programming},
  pages = {30:1--30:30},
  year = {2025},
}
```
