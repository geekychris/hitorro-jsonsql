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

package com.hitorro.sql.latch.coercion.doubleto;

import com.hitorro.sql.latch.expressions.SingleParent;
import com.hitorro.sql.latch.var.vartypes.VarDoubleIntf;
import com.hitorro.sql.latch.var.vartypes.VarIntf;
import com.hitorro.util.typesystem.TypeFieldDataType;

public abstract class DoubleToXXX extends SingleParent<VarDoubleIntf> {
    public static final VarIntf get(TypeFieldDataType type, VarIntf parent) {
        SingleParent sp = null;
        switch (type) {
            case String:
                sp = new DoubleToString();
                break;
            case Int:
                sp = new DoubleToInt();
                break;
            case Long:
                sp = new DoubleToLong();
                break;
            case Float:
                sp = new DoubleToFloat();
                break;
            case Short:
                sp = new DoubleToShort();
                break;
            case Boolean:
                sp = new DoubleToBoolean();
                break;
            case HTSerializable:
                return parent;

        }
        if (sp != null) {
            sp.setParent(parent);
            return (VarIntf) sp;
        }
        return null;
    }

}
