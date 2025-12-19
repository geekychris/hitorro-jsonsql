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

package com.hitorro.sql.latch.coercion.stringto;

import com.hitorro.sql.latch.expressions.SingleParent;
import com.hitorro.sql.latch.var.vartypes.VarIntf;
import com.hitorro.sql.latch.var.vartypes.VarStringIntf;
import com.hitorro.util.typesystem.TypeFieldDataType;

public abstract class StringToXXX extends SingleParent<VarStringIntf> {
    public static final VarIntf get(TypeFieldDataType type, VarIntf parent) {
        SingleParent sp = null;
        switch (type) {
            case Double:
                sp = new StringToDouble();
                break;
            case Int:
                sp = new StringToInt();
                break;
            case Long:
                sp = new StringToLong();
                break;
            case Float:
                sp = new StringToFloat();
                break;
            case Short:
                sp = new StringToShort();
                break;
            case Boolean:
                sp = new StringToBoolean();
                break;
            case Date:
                sp = new StringToDate();
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
