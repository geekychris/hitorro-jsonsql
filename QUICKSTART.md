# Quick Start Guide - Hitorro JSON SQL

## Prerequisites

1. **Java 19+**
   ```bash
   java -version
   ```

2. **Maven 3.6+**
   ```bash
   mvn -version
   ```

3. **Required Dependencies**
   - `hitorro-util` (3.0.0)
   - `hitorro-base` (3.0.0)

## Step-by-Step Setup

### Step 1: Build Dependencies

```bash
# Build hitorro-util
cd /Users/chris/hitorro/hitorro-util
mvn clean install

# Build hitorro-base
cd /Users/chris/hitorro/hitorro-base
mvn clean install
```

### Step 2: Build Hitorro JSON SQL

```bash
cd /Users/chris/hitorro/hitorro-jsonsql

# Use the build script (recommended)
./build.sh

# Or use Maven directly
mvn clean install
```

### Step 3: Verify the Build

```bash
# Check the target directory
ls -lh target/

# You should see:
# - hitorro-jsonsql-3.0.0.jar
# - hitorro-jsonsql-3.0.0-jar-with-dependencies.jar
# - hitorro-jsonsql-3.0.0-sources.jar
# - hitorro-jsonsql-3.0.0-javadoc.jar
```

## Using in Your Project

### Add to Your POM

```xml
<dependencies>
    <dependency>
        <groupId>com.hitorro</groupId>
        <artifactId>hitorro-jsonsql</artifactId>
        <version>3.0.0</version>
    </dependency>
</dependencies>
```

## Basic Usage

### Example 1: Simple Expression Evaluation

```java
import com.hitorro.util.sql.latch.*;
import com.hitorro.util.sql.latch.latches.*;
import com.hitorro.util.sql.latch.var.*;

// Create variables
VarInt x = new VarInt("x");
VarInt y = new VarInt("y");

// Create a latch source with values
Map<String, Object> data = new HashMap<>();
data.put("x", 10);
data.put("y", 20);
JSONLatchSource source = new JSONLatchSource(data);

// Evaluate: x + y
// (Actual implementation depends on expression builder)
```

### Example 2: Type Coercion

```java
import com.hitorro.util.sql.latch.coercion.stringto.*;
import com.hitorro.util.sql.latch.latches.*;

// Convert string to integer
StringToInt converter = new StringToInt();
StringLatch stringValue = new StringLatch("42");
IntLatch intValue = converter.coerce(stringValue);

System.out.println(intValue.getValue()); // Outputs: 42
```

### Example 3: Aggregations

```java
import com.hitorro.util.sql.latch.aggregate.*;

// Count aggregation
Count counter = new Count();
// Add values to counter
// ... get result

// Sum aggregation
SumLong summer = new SumLong();
// Add values to summer
// ... get result
```

### Example 4: String Functions

```java
import com.hitorro.util.sql.latch.string.*;
import com.hitorro.util.sql.latch.latches.*;

// Trim a string
Trim trimmer = new Trim();
StringLatch input = new StringLatch("  hello  ");
StringLatch result = trimmer.apply(input);

// Get length
Length lengthFunc = new Length();
IntLatch length = lengthFunc.apply(input);
```

### Example 5: Mathematical Functions

```java
import com.hitorro.util.sql.latch.math.*;
import com.hitorro.util.sql.latch.latches.*;

// Calculate square root
Sqrt sqrtFunc = new Sqrt();
DoubleLatch input = new DoubleLatch(16.0);
DoubleLatch result = sqrtFunc.apply(input);

System.out.println(result.getValue()); // Outputs: 4.0
```

## Common Patterns

### Working with Latches

Latches are type-safe value containers:

```java
// Integer latch
IntLatch intLatch = new IntLatch(42);
int value = intLatch.getValue();

// String latch
StringLatch strLatch = new StringLatch("hello");
String str = strLatch.getValue();

// Double latch
DoubleLatch dblLatch = new DoubleLatch(3.14);
double d = dblLatch.getValue();
```

### Type Conversions

```java
// String to various types
StringToInt s2i = new StringToInt();
StringToLong s2l = new StringToLong();
StringToDouble s2d = new StringToDouble();
StringToBoolean s2b = new StringToBoolean();

// Integer to other types
IntegerToLong i2l = new IntegerToLong();
IntegerToDouble i2d = new IntegerToDouble();
IntegerToString i2s = new IntegerToString();

// And many more combinations...
```

## Common Issues

### Issue: Cannot find hitorro-util or hitorro-base

**Solution**: Build and install dependencies first:

```bash
cd /Users/chris/hitorro/hitorro-util && mvn install
cd /Users/chris/hitorro/hitorro-base && mvn install
```

### Issue: JSQLParser version conflicts

**Solution**: This module uses JSQLParser 4.7. If you have version conflicts, check your dependency tree:

```bash
mvn dependency:tree
```

### Issue: Java version mismatch

**Solution**: Requires Java 19+:

```bash
java -version
export JAVA_HOME=/path/to/java19
```

## What Gets Built

| Artifact | Description |
|----------|-------------|
| `hitorro-jsonsql-3.0.0.jar` | Main JAR with compiled classes |
| `hitorro-jsonsql-3.0.0-jar-with-dependencies.jar` | Fat JAR with all dependencies |
| `hitorro-jsonsql-3.0.0-sources.jar` | Source code JAR |
| `hitorro-jsonsql-3.0.0-javadoc.jar` | JavaDoc documentation |

## Development Workflow

```bash
# Make code changes
vim src/main/java/ht/util/sql/...

# Quick compile check
mvn compile

# Run tests
mvn test

# Full build
mvn clean install

# Skip tests if needed
mvn install -DskipTests
```

## Testing

```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=LatchTest

# Run with debugging
mvn test -X
```

## Package Structure Overview

```
com.hitorro.util.sql.latch/
├── latches/          # IntLatch, StringLatch, etc.
├── var/              # Variable access (VarInt, VarString)
├── aggregate/        # COUNT, SUM, AVG, MAX, MIN
├── math/             # SIN, COS, SQRT, etc.
├── string/           # TRIM, LENGTH, REPLACE
├── coercion/         # Type conversions
├── logicalopers/     # AND, OR, NOT
└── comparators/      # =, <, >, <=, >=, !=

com.hitorro.sql.latch/
├── iterators/        # SQL result iteration
└── [additional]
```

## Next Steps

1. ✅ Build the project successfully
2. 📖 Read the [README.md](README.md) for architecture details
3. 📚 Check [EXTRACTION_SUMMARY.md](EXTRACTION_SUMMARY.md) for complete info
4. 🔨 Start using SQL queries on your JSON data

## Example Use Cases

1. **Query JSON API response**
   ```java
   // Parse JSON from API
   // Query with SQL: SELECT * WHERE status = 'active'
   ```

2. **Filter collections**
   ```java
   // Query Java collections using SQL syntax
   ```

3. **Configuration validation**
   ```java
   // Validate config files with SQL expressions
   ```

## Getting Help

- Check the [README.md](README.md) for detailed architecture
- Review the [EXTRACTION_SUMMARY.md](EXTRACTION_SUMMARY.md) for statistics
- Run `mvn help:effective-pom` to see full POM configuration

---

**Happy querying!** 🚀
