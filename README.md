# Hitorro JSON SQL

SQL-like query engine for JSON documents with type-safe expression evaluation.

## Overview

Hitorro JSON SQL provides a powerful SQL query engine that operates on JSON documents and Java objects. It combines SQL syntax with JSON data structures, enabling SQL-like queries on in-memory data without requiring a traditional database.

## Key Features

- **SQL Query Parsing**: Uses JSQLParser to parse standard SQL queries
- **Type-Safe Expression Evaluation**: Strongly-typed "latch" system for safe expression evaluation
- **JSON Data Support**: Query JSON documents using SQL syntax
- **Aggregations**: Support for COUNT, SUM, AVG, MIN, MAX
- **Mathematical Operations**: Full arithmetic expression support
- **String Functions**: TRIM, LENGTH, REPLACE, etc.
- **Type Coercion**: Automatic type conversions between compatible types
- **Logical Operators**: AND, OR, NOT, comparison operators
- **No Database Required**: Operates entirely in-memory

## Architecture

### Core Components

#### 1. Latch System
The "latch" is a type-safe value container that ensures correct type handling during expression evaluation:

```
Latch (Abstract base)
├── IntLatch
├── LongLatch
├── DoubleLatch
├── StringLatch
├── DateLatch
├── FloatLatch
├── ShortLatch
└── ObjectLatch
```

#### 2. Expression Evaluation
- **Variables**: Access JSON fields and object properties
- **Functions**: Built-in string, math, and aggregate functions
- **Operators**: Arithmetic, comparison, and logical operators
- **Type Coercion**: Automatic conversions (e.g., String → Int, Int → Double)

#### 3. SQL Parser Integration
- Leverages JSQLParser for SQL syntax parsing
- Custom expression evaluator for JSON-specific operations
- Support for SELECT, WHERE, GROUP BY, ORDER BY clauses

### Package Structure

```
ht.util.sql/
└── latch/
    ├── latches/          # Type-specific latch implementations
    ├── var/              # Variable access (VarInt, VarString, etc.)
    ├── math/             # Mathematical functions (Sin, Cos, Sqrt, etc.)
    ├── string/           # String functions (Trim, Length, Replace)
    ├── aggregate/        # Aggregation functions (Count, Sum, Max, etc.)
    ├── coercion/         # Type conversion logic
    ├── comparators/      # Comparison operators
    ├── logicalopers/     # Logical operators (And, Or, Not)
    └── exceptions/       # Custom exceptions

com.hitorro.sql/
└── latch/
    ├── iterators/        # Iterator support for SQL queries
    └── [additional components]
```

## Usage Example

```java
// Create a latch source for JSON data
JSONLatchSource source = new JSONLatchSource(jsonMap);

// Parse SQL expression
SelectItem selectItem = parseSelectExpression("field1 + field2 * 2");

// Evaluate the expression
Latch result = selectItem.evaluate(source);

// Get typed result
int value = ((IntLatch) result).getValue();
```

### Working with Aggregations

```java
// COUNT aggregation
Count countFunc = new Count();
// ... configure and execute

// SUM aggregation
SumLong sumFunc = new SumLong();
// ... configure and execute
```

### Type Coercion Example

```java
// Automatic conversion from String to Integer
StringToInt converter = new StringToInt();
IntLatch result = converter.coerce(stringLatch);

// Conversion from Double to Long
DoubleToLong converter = new DoubleToLong();
LongLatch result = converter.coerce(doubleLatch);
```

## Dependencies

### Required Hitorro Modules (Not in Maven Central)

This module depends on the following Hitorro libraries that must be built and installed locally first:

- **hitorro-util** (3.0.0+) - Core utilities and iteration framework
- **hitorro-base** (3.0.0+) - Base abstractions for data access

**⚠️ IMPORTANT**: These dependencies are **NOT available in Maven Central**. You must:
1. Build and install `hitorro-util` to your local Maven repository first
2. Build and install `hitorro-base` to your local Maven repository first
3. Then build this module

### External Dependencies (Available in Maven Central)

- **JSQLParser** (0.8.0) - SQL parsing library (legacy version)
- **Jackson Databind** (2.15.2) - JSON processing
- **SLF4J** (2.0.9) - Logging API
- **Logback** (1.4.11) - Logging implementation

**Note**: This project uses JSQLParser 0.8.0, an older version from the `net.sf.jsqlparser` groupId. Newer versions have breaking API changes and will not work without code modifications.

## Building

### Prerequisites

Before building this module, you must have `hitorro-util` and `hitorro-base` installed in your local Maven repository:

```bash
# First, build and install hitorro-util (from its directory)
cd ../hitorro-util
mvn clean install

# Then, build and install hitorro-base (from its directory)
cd ../hitorro-base
mvn clean install

# Finally, build this module
cd ../hitorro-jsonsql
mvn clean install
```

### Quick Build Commands

**Compile only:**
```bash
mvn clean compile
```

**Package (creates JAR files):**
```bash
mvn clean package -Dmaven.javadoc.skip=true
```

**Install to local repository:**
```bash
mvn clean install -Dmaven.javadoc.skip=true
```

**Note**: The `-Dmaven.javadoc.skip=true` flag is recommended to avoid non-critical Javadoc generation issues.

### Build Output

After a successful build, you'll find:
- `target/hitorro-jsonsql-3.0.0.jar` - Main library JAR (160KB)
- `target/hitorro-jsonsql-3.0.0-jar-with-dependencies.jar` - Fat JAR with all dependencies (153MB)
- `target/hitorro-jsonsql-3.0.0-sources.jar` - Source code JAR (107KB)

### Troubleshooting

If you encounter compilation errors about missing Hitorro classes, ensure that:
1. Both `hitorro-util` and `hitorro-base` are built with version 3.0.0
2. They are properly installed in your local Maven repository (`~/.m2/repository/com/hitorro/`)
3. The versions in this module's `pom.xml` match the installed versions

**Build Status**: ✅ Successfully compiles with Java 19

## Testing

The project includes comprehensive test coverage:

```bash
# Run all tests
mvn test
```

**Test Results:**
- ✅ **43 tests passing** (100% pass rate)
- **FunctionFactoryTest**: 18 tests covering function registry
- **VarTest**: 25 tests covering variable types
- **0 failures, 0 errors**

See [TEST_SUMMARY.md](TEST_SUMMARY.md) for detailed test documentation.

## Design Principles

1. **Type Safety**: Strong typing prevents runtime type errors
2. **Immutability**: Latches are immutable value containers
3. **Composability**: Expressions can be composed and nested
4. **Performance**: In-memory evaluation with minimal overhead
5. **Extensibility**: Easy to add custom functions and operators

## Advanced Features

### Custom Functions

The system supports adding custom functions:
- Implement the appropriate function interface
- Register with the function factory
- Use in SQL expressions

### Iterators

SQL query results can be iterated:
- `SQLJVSIterator` - Iterate over JSON value sources
- `SqlIterator` - Generic SQL result iteration

### Frame Support

Support for windowing and frame-based operations:
- `JSONMapWithSortFrame` - Sorted frames for ORDER BY
- Frame-based aggregations

## Limitations

- No table joins (operates on single data sources)
- Limited subquery support
- No transaction support (in-memory only)
- No persistence (data must be loaded into memory)

## Performance Considerations

- **In-Memory**: All data must fit in memory
- **Type Checking**: Type validation at evaluation time
- **Parsing Overhead**: SQL parsing happens once per query
- **Aggregations**: May require full dataset scan

## History

Extracted from the main Hitorro project (December 2025) as a standalone module.
Originally designed to provide SQL query capabilities over JSON documents and in-memory data structures without requiring a traditional database.

## Use Cases

- Query JSON API responses using SQL
- In-memory data filtering and aggregation
- Configuration file querying
- Testing data validation
- Ad-hoc data analysis

## License

[Original Hitorro License]

## Notes

- This module operates independently of database systems
- SQL syntax support is based on JSQLParser capabilities
- The "latch" terminology is internal - represents type-safe value containers
- Well-suited for scenarios where SQL expressiveness is needed without database overhead
