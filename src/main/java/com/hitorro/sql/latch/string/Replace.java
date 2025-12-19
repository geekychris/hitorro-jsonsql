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

package com.hitorro.sql.latch.string;

import com.hitorro.sql.latch.LatchFunctionDef;
import com.hitorro.sql.latch.var.vartypes.QueueSetter;
import com.hitorro.sql.latch.var.vartypes.VarStringIntf;
import com.hitorro.util.typesystem.TypeFieldDataType;

import java.util.List;

@LatchFunctionDef(cardinality = 3, name = "replace", argumentTypes = {TypeFieldDataType.String,
        TypeFieldDataType.String,
        TypeFieldDataType.String})
public class Replace implements VarStringIntf, QueueSetter<VarStringIntf> {
    private VarStringIntf recipient;
    private VarStringIntf find;
    private VarStringIntf replace;

    @Override
    public String getString() {
        String rec = recipient.getString();
        String f = find.getString();
        String r = replace.getString();
        if (rec == null) {
            return null;
        }
        return rec.replaceAll(f, r);
    }

    @Override
    public TypeFieldDataType getOutputDataType() {
        return TypeFieldDataType.String;
    }

    @Override
    public boolean hasValue() {
        return recipient.hasValue();
    }

    @Override
    public boolean hasDefault() {
        return recipient.hasDefault();
    }

    @Override
    public void setParents(final List<VarStringIntf> varIntfs) {
        recipient = varIntfs.get(0);
        find = varIntfs.get(1);
        replace = varIntfs.get(2);
    }

    @Override
    public boolean isAggregate() {
        return false;
    }

}
