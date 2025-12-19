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

package com.hitorro.sql.latch.aggregate;

import com.hitorro.sql.latch.LatchFunctionDef;
import com.hitorro.sql.latch.expressions.SingleParent;
import com.hitorro.sql.latch.var.vartypes.Aggregate;
import com.hitorro.sql.latch.var.vartypes.VarIntIntf;
import com.hitorro.sql.latch.var.vartypes.VarIntf;
import com.hitorro.util.typesystem.TypeFieldDataType;

@LatchFunctionDef(cardinality = 1, name = "count", argumentTypes = {TypeFieldDataType.HTSerializable}, aggregate = true)
public class Count extends SingleParent<VarIntf> implements VarIntIntf, Aggregate {
    int counter;

    @Override
    public int getInt() {
        return counter;
    }

    @Override
    public TypeFieldDataType getOutputDataType() {
        return TypeFieldDataType.Int;
    }

    @Override
    public boolean hasValue() {
        return expression.hasValue();
    }


    public boolean hasDefault() {
        return expression.hasDefault();
    }

    @Override
    public boolean isAggregate() {
        return true;
    }

    @Override
    public void clock() {
        if (expression.hasValue()) {
            counter++;
        }
    }
}
