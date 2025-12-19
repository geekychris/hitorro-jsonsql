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
import com.hitorro.sql.latch.var.vartypes.VarDateIntf;
import com.hitorro.util.typesystem.FieldBaseIntf;
import com.hitorro.util.typesystem.TypeFieldDataType;

import java.util.Date;

public class DateLatch extends Latch implements VarDateIntf {
    public Date value;
    public Date defaultVal;

    public DateLatch(LatchSource ls, FieldBaseIntf tfi) {
        super(ls, tfi);
    }

    @Override
    public boolean fetch() {
        return ls.get(fieldName, this);
    }


    @Override
    public Date getDate() {
        if (hasValue()) {
            return value;
        }
        return defaultVal;
    }

    @Override
    public TypeFieldDataType getOutputDataType() {
        return TypeFieldDataType.Double;
    }
}
