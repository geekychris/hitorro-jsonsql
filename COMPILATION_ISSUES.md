# Compilation Issues and Solutions

## ✅ RESOLVED - Project Now Compiles Successfully

The `hitorro-jsonsql` module now compiles successfully after fixing all dependency issues.

## Fixed Issues ✅

### 1. Wrong JSQLParser GroupId
**Problem**: The POM originally specified `net.sf.jsqlparser:jsqlparser:4.7`, but version 4.7 doesn't exist under that groupId.

**Solution**: Changed to `com.github.jsqlparser:jsqlparser` (the correct modern groupId).

### 2. Missing Jackson Dependency
**Problem**: The code uses `com.fasterxml.jackson` classes but Jackson was not declared in the POM.

**Solution**: Added Jackson databind dependency (version 2.15.2).

### 3. Hitorro Dependencies Available
**Status**: Both `hitorro-util:3.0.0` and `hitorro-base:3.0.0` are properly installed in the local Maven repository.

## Remaining Issues ❌

### JSQLParser API Incompatibility

The code was written for an older/different version of JSQLParser, but the exact compatible version is unclear. Multiple versions have been tested:

#### Version 4.7 (Latest in README)
- ❌ Missing classes: `SubSelect`, `AllComparisonExpression`, `InverseExpression`, `Union`
- ❌ Missing methods: `isNot()`, `getItemsList()`, `getWholeTableName()`
- ❌ Package `net.sf.jsqlparser.statement.replace` doesn't exist

#### Version 4.5
- ❌ Same issues as 4.7
- ❌ Additional API incompatibilities with method signatures

#### Version 1.4 (Tested - Closest Match)
- ❌ Method name clashes: Multiple `visit()` methods with same erasure
- ❌ Missing methods: `getItemsList()`, `getWholeTableName()`, `getWholeColumnName()`
- ❌ Missing classes: `InverseExpression`, `Union`, `AllComparisonExpression`
- ⚠️ Some methods return different types (e.g., `Alias` object vs `String`)

### Specific Compilation Errors (Version 1.4)

1. **ExpressionDeParser.java**:
   - Line 75: `inExpression.getItemsList()` - method doesn't exist
   - Line 224: `InverseExpression` class not found
   - Multiple method signature conflicts for `visit()` methods

2. **SelectDeParser.java**:
   - Line 108: `Union` class not found
   - Line 153: `getWholeTableName()` - method doesn't exist
   - Line 165: `Alias` type mismatch (object vs String)
   - Line 201, 215: Limit API changes

3. **StatementDeParser.java**:
   - Package `net.sf.jsqlparser.statement.replace` doesn't exist
   - `Replace` class not found

## Possible Solutions

### Option 1: Find the Correct JSQLParser Version (Recommended)
The code likely works with a specific JSQLParser version between 0.9 and 1.4. Candidates to test:
- `1.3`
- `1.2`
- `1.1`
- `1.0`
- `0.9.x` series

### Option 2: Refactor for Modern JSQLParser API
Update the parser code to work with JSQLParser 4.7+:
- Replace missing classes with modern equivalents
- Update method calls to match new API
- Handle type changes (e.g., `Alias.getName()` instead of direct String)
- Remove references to deprecated classes

### Option 3: Extract JSQLParser Version from Original Project
If this was extracted from a working project, check that project's POM to find the exact JSQLParser version that was being used.

### Option 4: Make JSQLParser Optional
Refactor the architecture to make the parser components optional, allowing the core latch system to work independently.

## Testing Different Versions

To test a specific JSQLParser version, modify `pom.xml`:

```xml
<dependency>
    <groupId>com.github.jsqlparser</groupId>
    <artifactId>jsqlparser</artifactId>
    <version>VERSION_HERE</version>
</dependency>
```

Then run:
```bash
mvn clean compile
```

## Final Solution

The correct JSQLParser version was found by checking the original Hitorro project:
- **Correct version**: `net.sf.jsqlparser:jsqlparser:0.8.0`
- **Original location**: `/hitorro/hitorro-parent/hitorro-jsonsql/pom.xml`

## Current Status

- ✅ All external dependencies are correctly specified
- ✅ Hitorro dependencies are available
- ✅ Jackson dependency added
- ✅ JSQLParser version corrected to 0.8.0
- ✅ **Project compiles successfully**

## Build Command

```bash
mvn clean compile
```

**Result**: BUILD SUCCESS

## Warnings (Non-Critical)

The build produces some warnings but completes successfully:
- Deprecated API usage in `StringToDate.java`
- Unchecked operations in `IntegerToXXX.java`
- System modules path warning with Java 19

These warnings do not prevent compilation and can be addressed in future refactoring.
