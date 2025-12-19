#!/bin/bash

# MIT License header for Java files
read -r -d '' MIT_HEADER << 'EOF'
/*
 * MIT License
 *
 * Copyright (c) 2006-2025 Chris Collins
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
EOF

# Counter for processed files
count=0

# Find all Java files
find src -name "*.java" -type f | while read file; do
    echo "Processing: $file" >&2
    
    # Create temp file
    temp_file=$(mktemp)
    
    # Remove ALL comment blocks (both before and after package statement)
    # Keep only actual code (package, imports, class definitions, etc.)
    awk '
    BEGIN { 
        in_comment=0
        found_package=0
        skip_next_empty=0
    }
    
    # Skip leading empty lines before package
    /^[[:space:]]*$/ && found_package==0 { next }
    
    # Detect start of multi-line comment (anywhere in file)
    /^[[:space:]]*\/\*/ { 
        in_comment=1
        # Check if comment ends on same line
        if (/\*\//) {
            in_comment=0
            skip_next_empty=1
        }
        next
    }
    
    # Detect end of multi-line comment
    /\*\// && in_comment==1 { 
        in_comment=0
        skip_next_empty=1
        next
    }
    
    # Skip lines inside comments
    in_comment==1 { next }
    
    # Skip single-line comments that are alone on a line
    /^[[:space:]]*\/\/.*$/ { next }
    
    # Skip empty lines after comments
    /^[[:space:]]*$/ && skip_next_empty==1 { 
        skip_next_empty=0
        next 
    }
    
    # Reset skip flag on non-empty lines
    /[^[:space:]]/ { skip_next_empty=0 }
    
    # Found package statement - start printing from here
    /^package / { found_package=1 }
    
    # Print everything after finding package (except comments which we already filtered)
    found_package==1 { print }
    ' "$file" > "$temp_file"
    
    # Add new MIT header before the package statement
    {
        echo "$MIT_HEADER"
        echo ""
        cat "$temp_file"
    } > "$file"
    
    rm -f "$temp_file"
    count=$((count + 1))
done

echo "Header update complete! Processed files." >&2
