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

package com.hitorro.sql.latch.latches;

import com.hitorro.sql.latch.LatchSource;
import com.hitorro.sql.latch.var.vartypes.VarIntf;
import com.hitorro.util.typesystem.FieldBaseIntf;
import com.hitorro.util.typesystem.TypeFieldDataType;

public abstract class Latch implements VarIntf {
    public long currentRow = -1;
    public FieldBaseIntf tfi;
    public String fieldName;
    public boolean validRow;
    protected LatchSource ls;
    private boolean hasDefault = false;

    public Latch(LatchSource ls, FieldBaseIntf tfi) {
        this.ls = ls;
        this.tfi = tfi;
        fieldName = tfi.getName();
    }

    public static Latch getLatch(LatchSource ls, FieldBaseIntf tfi) {
        Class clazz = tfi.getImplementingClass();
        TypeFieldDataType tfdt = TypeFieldDataType.getFromClass(clazz);
        if (tfdt != null) {
            switch (tfdt) {
                case Double:
                    return new DoubleLatch(ls, tfi);
                case Int:
                    return new IntLatch(ls, tfi);
                case Long:
                    return new LongLatch(ls, tfi);
                case Float:
                    return new FloatLatch(ls, tfi);
                case Short:
                    return new ShortLatch(ls, tfi);
                case String:
                    return new StringLatch(ls, tfi);
                case HTSerializable:
                    return new ObjectLatch(ls, tfi);
            }
        }
        return null;
    }

    public void setHasDefault(boolean flag) {
        hasDefault = flag;
    }

    public boolean hasDefault() {
        return hasDefault;
    }

    public boolean isValidCacheValue() {
        return currentRow == ls.currentRow;
    }

    public boolean hasValue() {
        if (isValidCacheValue()) {
            return true;
        }
        return fetch();
    }

    public abstract boolean fetch();

    public boolean isAggregate() {
        return false;
    }
}
