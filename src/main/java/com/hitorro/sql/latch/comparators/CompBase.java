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

package com.hitorro.sql.latch.comparators;

import com.hitorro.util.core.CompOperEnum;
import com.hitorro.sql.latch.LatchSource;
import com.hitorro.sql.latch.expressions.LeftRightParent;
import com.hitorro.sql.latch.var.vartypes.VarBooleanIntf;
import com.hitorro.sql.latch.var.vartypes.VarIntf;
import com.hitorro.util.typesystem.TypeFieldDataType;

public abstract class
CompBase<T extends VarIntf> extends LeftRightParent<T> implements VarBooleanIntf {
    protected CompOperEnum oper;

    public static CompBase get(TypeFieldDataType type, CompOperEnum oper, VarIntf left, VarIntf right) {
        left = LatchSource.cast(left, type);
        right = LatchSource.cast(right, type);
        CompBase sp = null;
        switch (type) {
            case String:
                sp = new StringComp();
                break;
            case Int:
                sp = new IntegerComp();
                break;
            case Long:
                sp = new LongComp();
                break;
            case Float:
                sp = new FloatComp();
                break;
            case Short:
                sp = new ShortComp();
                break;
            case Date:
                sp = new DateComp();
                break;
            case Double:
                sp = new DoubleComp();
                break;
        }
        if (sp != null) {
            sp.set(left, right);
            sp.setOperator(oper);
            return sp;
        }
        return null;
    }

    @Override
    public TypeFieldDataType getOutputDataType() {
        return TypeFieldDataType.Boolean;
    }

    @Override
    public boolean hasValue() {
        return leftExpression.hasValue() && rightExpression.hasValue();
    }

    @Override
    public boolean hasDefault() {
        return leftExpression.hasDefault() && rightExpression.hasDefault();
    }

    public void setOperator(CompOperEnum oper) {
        this.oper = oper;
    }
}
