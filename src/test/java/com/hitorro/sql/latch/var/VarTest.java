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

package com.hitorro.sql.latch.var;

import com.hitorro.util.typesystem.TypeFieldDataType;
import org.junit.Test;

import static org.junit.Assert.*;

public class VarTest {

    @Test
    public void testVarIntGetName() {
        VarInt varInt = new VarInt("age");
        assertEquals("Variable name should be 'age'", "age", varInt.getName());
    }

    @Test
    public void testVarIntGetOutputDataType() {
        VarInt varInt = new VarInt("count");
        assertEquals("Output type should be Int", TypeFieldDataType.Int, varInt.getOutputDataType());
    }

    @Test
    public void testVarIntHasValue() {
        VarInt varInt = new VarInt("value");
        assertTrue("VarInt should always have value", varInt.hasValue());
    }

    @Test
    public void testVarIntHasDefault() {
        VarInt varInt = new VarInt("default");
        assertTrue("VarInt should have default", varInt.hasDefault());
    }

    @Test
    public void testVarIntIsNotAggregate() {
        VarInt varInt = new VarInt("field");
        assertFalse("VarInt should not be aggregate", varInt.isAggregate());
    }

    @Test
    public void testVarStringGetName() {
        VarString varString = new VarString("name");
        assertEquals("Variable name should be 'name'", "name", varString.getName());
    }

    @Test
    public void testVarStringGetOutputDataType() {
        VarString varString = new VarString("text");
        assertEquals("Output type should be String", TypeFieldDataType.String, varString.getOutputDataType());
    }

    @Test
    public void testVarDoubleGetName() {
        VarDouble varDouble = new VarDouble("price");
        assertEquals("Variable name should be 'price'", "price", varDouble.getName());
    }

    @Test
    public void testVarDoubleGetOutputDataType() {
        VarDouble varDouble = new VarDouble("amount");
        assertEquals("Output type should be Double", TypeFieldDataType.Double, varDouble.getOutputDataType());
    }

    @Test
    public void testVarLongGetName() {
        VarLong varLong = new VarLong("id");
        assertEquals("Variable name should be 'id'", "id", varLong.getName());
    }

    @Test
    public void testVarLongGetOutputDataType() {
        VarLong varLong = new VarLong("timestamp");
        assertEquals("Output type should be Long", TypeFieldDataType.Long, varLong.getOutputDataType());
    }

    @Test
    public void testVarFloatGetName() {
        VarFloat varFloat = new VarFloat("ratio");
        assertEquals("Variable name should be 'ratio'", "ratio", varFloat.getName());
    }

    @Test
    public void testVarFloatGetOutputDataType() {
        VarFloat varFloat = new VarFloat("percent");
        assertEquals("Output type should be Float", TypeFieldDataType.Float, varFloat.getOutputDataType());
    }

    @Test
    public void testVarShortGetName() {
        VarShort varShort = new VarShort("port");
        assertEquals("Variable name should be 'port'", "port", varShort.getName());
    }

    @Test
    public void testVarShortGetOutputDataType() {
        VarShort varShort = new VarShort("code");
        assertEquals("Output type should be Short", TypeFieldDataType.Short, varShort.getOutputDataType());
    }

    @Test
    public void testVarBooleanGetName() {
        VarBoolean varBoolean = new VarBoolean("active");
        assertEquals("Variable name should be 'active'", "active", varBoolean.getName());
    }

    @Test
    public void testVarBooleanGetOutputDataType() {
        VarBoolean varBoolean = new VarBoolean("enabled");
        assertEquals("Output type should be Boolean", TypeFieldDataType.Boolean, varBoolean.getOutputDataType());
    }

    @Test
    public void testVarDateGetName() {
        VarDate varDate = new VarDate("created");
        assertEquals("Variable name should be 'created'", "created", varDate.getName());
    }

    @Test
    public void testVarDateGetOutputDataType() {
        VarDate varDate = new VarDate("modified");
        assertEquals("Output type should be Date", TypeFieldDataType.Date, varDate.getOutputDataType());
    }

    @Test
    public void testVarObjectGetName() {
        VarObject varObject = new VarObject("data");
        assertEquals("Variable name should be 'data'", "data", varObject.getName());
    }

    @Test
    public void testVarObjectGetOutputDataType() {
        VarObject varObject = new VarObject("payload");
        assertEquals("Output type should be HTSerializable", TypeFieldDataType.HTSerializable, varObject.getOutputDataType());
    }

    @Test
    public void testMultipleVarsWithDifferentNames() {
        VarInt var1 = new VarInt("field1");
        VarInt var2 = new VarInt("field2");
        VarString var3 = new VarString("field3");
        
        assertNotEquals("Different variable names", var1.getName(), var2.getName());
        assertNotEquals("Different variable names", var1.getName(), var3.getName());
        assertEquals("field1", var1.getName());
        assertEquals("field2", var2.getName());
        assertEquals("field3", var3.getName());
    }

    @Test
    public void testVarIntValue() {
        VarInt varInt = new VarInt("test");
        varInt.value = 42;
        
        assertEquals("Value should be stored", 42, varInt.value);
        assertEquals("getInt() should return value", 42, varInt.getInt());
    }

    @Test
    public void testVarStringValue() {
        VarString varString = new VarString("message");
        varString.value = "Hello";
        
        assertEquals("Value should be stored", "Hello", varString.value);
        assertEquals("getString() should return value", "Hello", varString.getString());
    }

    @Test
    public void testVarDoubleValue() {
        VarDouble varDouble = new VarDouble("pi");
        varDouble.value = 3.14159;
        
        assertEquals("Value should be stored", 3.14159, varDouble.value, 0.00001);
        assertEquals("getDouble() should return value", 3.14159, varDouble.getDouble(), 0.00001);
    }
}
