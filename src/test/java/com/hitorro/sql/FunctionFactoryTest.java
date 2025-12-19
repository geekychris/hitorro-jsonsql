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

package com.hitorro.sql;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class FunctionFactoryTest {

    @Test
    public void testGetFactory() {
        FunctionFactory factory1 = FunctionFactory.get();
        FunctionFactory factory2 = FunctionFactory.get();
        
        assertNotNull("FunctionFactory instance should not be null", factory1);
        assertSame("FunctionFactory should be a singleton", factory1, factory2);
    }

    @Test
    public void testGetSinFunction() {
        FunctionFactory factory = FunctionFactory.get();
        List<FunctionEntry> entries = factory.get("sin");
        
        assertNotNull("Sin function should be found", entries);
        assertFalse("Sin function list should not be empty", entries.isEmpty());
        
        FunctionEntry entry = entries.get(0);
        assertEquals("Function name should be sin", "sin", entry.name);
        assertFalse("Sin should not be aggregate", entry.aggregate);
    }

    @Test
    public void testGetCosFunction() {
        FunctionFactory factory = FunctionFactory.get();
        List<FunctionEntry> entries = factory.get("cos");
        
        assertNotNull("Cos function should be found", entries);
        assertFalse("Cos function list should not be empty", entries.isEmpty());
        
        FunctionEntry entry = entries.get(0);
        assertEquals("Function name should be cos", "cos", entry.name);
        assertFalse("Cos should not be aggregate", entry.aggregate);
    }

    @Test
    public void testGetSqrtFunction() {
        FunctionFactory factory = FunctionFactory.get();
        List<FunctionEntry> entries = factory.get("sqrt");
        
        assertNotNull("Sqrt function should be found", entries);
        assertFalse("Sqrt function list should not be empty", entries.isEmpty());
        
        FunctionEntry entry = entries.get(0);
        assertEquals("Function name should be sqrt", "sqrt", entry.name);
        assertFalse("Sqrt should not be aggregate", entry.aggregate);
    }

    @Test
    public void testGetCountFunction() {
        FunctionFactory factory = FunctionFactory.get();
        List<FunctionEntry> entries = factory.get("count");
        
        assertNotNull("Count function should be found", entries);
        assertFalse("Count function list should not be empty", entries.isEmpty());
        
        FunctionEntry entry = entries.get(0);
        assertEquals("Function name should be count", "count", entry.name);
        assertTrue("Count should be aggregate", entry.aggregate);
    }

    @Test
    public void testGetSumFunction() {
        FunctionFactory factory = FunctionFactory.get();
        List<FunctionEntry> entries = factory.get("sum");
        
        assertNotNull("Sum function should be found", entries);
        assertFalse("Sum function list should not be empty", entries.isEmpty());
        assertTrue("Sum should be aggregate", entries.get(0).aggregate);
    }

    @Test
    public void testGetMaxFunction() {
        FunctionFactory factory = FunctionFactory.get();
        List<FunctionEntry> entries = factory.get("max");
        
        assertNotNull("Max function should be found", entries);
        assertFalse("Max function list should not be empty", entries.isEmpty());
        assertTrue("Max should be aggregate", entries.get(0).aggregate);
    }

    @Test
    public void testGetMinFunction() {
        FunctionFactory factory = FunctionFactory.get();
        List<FunctionEntry> entries = factory.get("min");
        
        assertNotNull("Min function should be found", entries);
        assertFalse("Min function list should not be empty", entries.isEmpty());
        assertTrue("Min should be aggregate", entries.get(0).aggregate);
    }

    @Test
    public void testGetTrimFunction() {
        FunctionFactory factory = FunctionFactory.get();
        List<FunctionEntry> entries = factory.get("trim");
        
        assertNotNull("Trim function should be found", entries);
        assertFalse("Trim function list should not be empty", entries.isEmpty());
        
        FunctionEntry entry = entries.get(0);
        assertEquals("Function name should be trim", "trim", entry.name);
        assertFalse("Trim should not be aggregate", entry.aggregate);
    }

    @Test
    public void testGetLtrimFunction() {
        FunctionFactory factory = FunctionFactory.get();
        List<FunctionEntry> entries = factory.get("ltrim");
        
        assertNotNull("Ltrim function should be found", entries);
        assertFalse("Ltrim function list should not be empty", entries.isEmpty());
        
        FunctionEntry entry = entries.get(0);
        assertEquals("Function name should be ltrim", "ltrim", entry.name);
        assertFalse("Ltrim should not be aggregate", entry.aggregate);
    }

    @Test
    public void testGetRtrimFunction() {
        FunctionFactory factory = FunctionFactory.get();
        List<FunctionEntry> entries = factory.get("rtrim");
        
        assertNotNull("Rtrim function should be found", entries);
        assertFalse("Rtrim function list should not be empty", entries.isEmpty());
        
        FunctionEntry entry = entries.get(0);
        assertEquals("Function name should be rtrim", "rtrim", entry.name);
        assertFalse("Rtrim should not be aggregate", entry.aggregate);
    }

    @Test
    public void testGetLengthFunction() {
        FunctionFactory factory = FunctionFactory.get();
        List<FunctionEntry> entries = factory.get("length");
        
        assertNotNull("Length function should be found", entries);
        assertFalse("Length function list should not be empty", entries.isEmpty());
        
        FunctionEntry entry = entries.get(0);
        assertEquals("Function name should be length", "length", entry.name);
        assertFalse("Length should not be aggregate", entry.aggregate);
    }

    @Test
    public void testGetReplaceFunction() {
        FunctionFactory factory = FunctionFactory.get();
        List<FunctionEntry> entries = factory.get("replace");
        
        assertNotNull("Replace function should be found", entries);
        assertFalse("Replace function list should not be empty", entries.isEmpty());
        
        FunctionEntry entry = entries.get(0);
        assertEquals("Function name should be replace", "replace", entry.name);
        assertFalse("Replace should not be aggregate", entry.aggregate);
    }

    @Test
    public void testCaseInsensitiveGet() {
        FunctionFactory factory = FunctionFactory.get();
        
        List<FunctionEntry> lowercase = factory.get("sin");
        List<FunctionEntry> uppercase = factory.get("SIN");
        List<FunctionEntry> mixedcase = factory.get("Sin");
        
        assertNotNull("Lowercase should find function", lowercase);
        assertNotNull("Uppercase should find function", uppercase);
        assertNotNull("Mixed case should find function", mixedcase);
    }

    @Test
    public void testGetNonExistentFunction() {
        FunctionFactory factory = FunctionFactory.get();
        List<FunctionEntry> entries = factory.get("nonExistentFunction123");
        
        assertNull("Non-existent function should return null", entries);
    }

    @Test
    public void testFunctionCardinality() {
        FunctionFactory factory = FunctionFactory.get();
        
        List<FunctionEntry> sinList = factory.get("sin");
        assertNotNull("Sin function should exist", sinList);
        assertFalse("Sin list should not be empty", sinList.isEmpty());
        assertTrue("Sin should have cardinality > 0", sinList.get(0).cardinality > 0);
        
        List<FunctionEntry> countList = factory.get("count");
        assertNotNull("Count function should exist", countList);
        assertFalse("Count list should not be empty", countList.isEmpty());
        assertTrue("Count should have defined cardinality", countList.get(0).cardinality >= 0);
    }

    @Test
    public void testMultipleFunctionLookups() {
        FunctionFactory factory = FunctionFactory.get();
        
        List<FunctionEntry> sin = factory.get("sin");
        List<FunctionEntry> cos = factory.get("cos");
        List<FunctionEntry> sqrt = factory.get("sqrt");
        
        assertNotNull("Sin should be found", sin);
        assertNotNull("Cos should be found", cos);
        assertNotNull("Sqrt should be found", sqrt);
        
        assertFalse("Sin list should not be empty", sin.isEmpty());
        assertFalse("Cos list should not be empty", cos.isEmpty());
        assertFalse("Sqrt list should not be empty", sqrt.isEmpty());
    }

    @Test
    public void testFactoryInitialization() {
        FunctionFactory factory = FunctionFactory.get();
        
        assertNotNull("Factory should be initialized", factory);
        
        // Verify key function categories are loaded
        assertNotNull("Math functions should be loaded", factory.get("sin"));
        assertNotNull("String functions should be loaded", factory.get("trim"));
        assertNotNull("Aggregate functions should be loaded", factory.get("count"));
        
        // Verify multiple function types work
        List<FunctionEntry> mathFunc = factory.get("sqrt");
        List<FunctionEntry> stringFunc = factory.get("length");
        List<FunctionEntry> aggFunc = factory.get("sum");
        
        assertNotNull("Math function should exist", mathFunc);
        assertNotNull("String function should exist", stringFunc);
        assertNotNull("Aggregate function should exist", aggFunc);
        
        assertFalse("Math should not be aggregate", mathFunc.get(0).aggregate);
        assertFalse("String should not be aggregate", stringFunc.get(0).aggregate);
        assertTrue("Sum should be aggregate", aggFunc.get(0).aggregate);
    }
}
