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

package com.hitorro.sql.latch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hitorro.sql.latch.latches.*;
import com.hitorro.util.core.iterator.JsonValueSource;
import com.hitorro.util.typesystem.TypeBaseIntf;
import com.hitorro.util.typesystem.TypeFieldDataType;
import com.hitorro.util.typesystem.FieldBaseIntf;
import com.hitorro.jsontypesystem.Type;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class JSONLatchSourceTest {
    private ObjectMapper mapper;
    private JSONLatchSource latchSource;
    private TypeBaseIntf typeBase;

    @Before
    public void setUp() {
        mapper = new ObjectMapper();
        typeBase = new Type();
        latchSource = new JSONLatchSource(typeBase);
    }

    @Test
    public void testGetInt() throws Exception {
        String json = "{\"age\": 25, \"score\": 100}";
        JsonNode node = mapper.readTree(json);
        
        JsonValueSource jvs = new JsonValueSource() {
            @Override
            public Object getValue(String field) {
                return node.get(field);
            }
        };
        
        latchSource.row = jvs;
        latchSource.currentRow = 1;
        
        IntLatch ageLatch = new IntLatch(latchSource, createField("age"));
        boolean success = latchSource.get("age", ageLatch);
        
        assertTrue("Should successfully get int value", success);
        assertEquals("Age should be 25", 25, ageLatch.value);
        assertEquals("Current row should be set", 1, ageLatch.currentRow);
    }

    @Test
    public void testGetString() throws Exception {
        String json = "{\"name\": \"John\", \"city\": \"New York\"}";
        JsonNode node = mapper.readTree(json);
        
        JsonValueSource jvs = new JsonValueSource() {
            @Override
            public Object getValue(String field) {
                return node.get(field);
            }
        };
        
        latchSource.row = jvs;
        latchSource.currentRow = 2;
        
        StringLatch nameLatch = new StringLatch(latchSource, createField("name"));
        boolean success = latchSource.get("name", nameLatch);
        
        assertTrue("Should successfully get string value", success);
        assertEquals("Name should be John", "John", nameLatch.value);
    }

    @Test
    public void testGetDouble() throws Exception {
        String json = "{\"price\": 19.99, \"tax\": 1.50}";
        JsonNode node = mapper.readTree(json);
        
        JsonValueSource jvs = new JsonValueSource() {
            @Override
            public Object getValue(String field) {
                return node.get(field);
            }
        };
        
        latchSource.row = jvs;
        
        DoubleLatch priceLatch = new DoubleLatch(latchSource, createField("price"));
        boolean success = latchSource.get("price", priceLatch);
        
        assertTrue("Should successfully get double value", success);
        assertEquals("Price should be 19.99", 19.99, priceLatch.value, 0.001);
    }

    @Test
    public void testGetMissingField() throws Exception {
        String json = "{\"name\": \"John\"}";
        JsonNode node = mapper.readTree(json);
        
        JsonValueSource jvs = new JsonValueSource() {
            @Override
            public Object getValue(String field) {
                return node.get(field);
            }
        };
        
        latchSource.row = jvs;
        
        IntLatch ageLatch = new IntLatch(latchSource, createField("age"));
        boolean success = latchSource.get("age", ageLatch);
        
        assertFalse("Should return false for missing field", success);
        assertFalse("Latch should not have valid row", ageLatch.validRow);
    }

    @Test
    public void testMultipleFieldsFromSameRow() throws Exception {
        String json = "{\"name\": \"Alice\", \"age\": 30, \"score\": 95.5}";
        JsonNode node = mapper.readTree(json);
        
        JsonValueSource jvs = new JsonValueSource() {
            @Override
            public Object getValue(String field) {
                return node.get(field);
            }
        };
        
        latchSource.row = jvs;
        latchSource.currentRow = 5;
        
        StringLatch nameLatch = new StringLatch(latchSource, createField("name"));
        IntLatch ageLatch = new IntLatch(latchSource, createField("age"));
        DoubleLatch scoreLatch = new DoubleLatch(latchSource, createField("score"));
        
        assertTrue("Should get name", latchSource.get("name", nameLatch));
        assertTrue("Should get age", latchSource.get("age", ageLatch));
        assertTrue("Should get score", latchSource.get("score", scoreLatch));
        
        assertEquals("Name should be Alice", "Alice", nameLatch.value);
        assertEquals("Age should be 30", 30, ageLatch.value);
        assertEquals("Score should be 95.5", 95.5, scoreLatch.value, 0.001);
        
        assertEquals(5, nameLatch.currentRow);
        assertEquals(5, ageLatch.currentRow);
        assertEquals(5, scoreLatch.currentRow);
    }
    
    private FieldBaseIntf createField(final String fieldName) {
        return new FieldBaseIntf() {
            @Override
            public String getName() {
                return fieldName;
            }
            
            @Override
            public Class getImplementingClass() {
                return String.class;
            }
        };
    }
}
