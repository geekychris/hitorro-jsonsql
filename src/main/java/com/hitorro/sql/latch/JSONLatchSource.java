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
import com.hitorro.util.core.iterator.JsonValueSource;
import com.hitorro.sql.latch.latches.*;
import com.hitorro.util.typesystem.TypeBaseIntf;
import com.hitorro.util.typesystem.TypeFieldDataType;

import java.util.Date;

public class JSONLatchSource extends LatchSource<JsonValueSource> {
    public JSONLatchSource(TypeBaseIntf type) {
        super(type);
    }

    @Override
    public boolean get(final String field, final StringLatch ll) {
        JsonNode e = (JsonNode) this.row.getValue(field);
        ll.currentRow = this.currentRow;
        if (e == null) {
            ll.validRow = false;
            return false;
        } else {
            ll.value = e.textValue();
            return true;
        }
    }

    @Override
    public boolean get(final String field, final LongLatch ll) {
        JsonNode e = (JsonNode) this.row.getValue(field);
        ll.currentRow = this.currentRow;
        if (e == null) {
            ll.validRow = false;
            return false;
        } else {
            ll.value = e.longValue();
            return true;
        }
    }

    @Override
    public boolean get(final String field, final DateLatch ll) {
        JsonNode e = (JsonNode) this.row.getValue(field);
        ll.currentRow = this.currentRow;
        if (e == null) {
            ll.validRow = false;
            return false;
        } else {
            ll.value = (Date) TypeFieldDataType.Date.convertFromString(e.toString());
            return true;
        }
    }

    @Override
    public boolean get(final String field, final IntLatch il) {
        JsonNode e = (JsonNode) this.row.getValue(field);
        il.currentRow = this.currentRow;
        if (e == null) {
            il.validRow = false;
            return false;
        } else {
            il.value = e.intValue();
            return true;
        }
    }

    @Override
    public boolean get(final String field, final ShortLatch il) {
        JsonNode e = (JsonNode) this.row.getValue(field);
        il.currentRow = this.currentRow;
        if (e == null) {
            il.validRow = false;
            return false;
        } else {
            il.value = (short) e.intValue();
            return true;
        }
    }

    @Override
    public boolean get(final String field, final FloatLatch fl) {
        JsonNode e = (JsonNode) this.row.getValue(field);
        fl.currentRow = this.currentRow;
        if (e == null) {
            fl.validRow = false;
            return false;
        } else {
            fl.value = (float) e.doubleValue();
            return true;
        }
    }

    @Override
    public boolean get(final String field, final DoubleLatch dl) {
        JsonNode e = (JsonNode) this.row.getValue(field);
        dl.currentRow = this.currentRow;
        if (e == null) {
            dl.validRow = false;
            return false;
        } else {
            dl.value = e.doubleValue();
            return true;
        }
    }

    @Override
    public boolean get(final String field, final ObjectLatch ll) {
        JsonNode e = (JsonNode) this.row.getValue(field);
        ll.currentRow = this.currentRow;
        if (e == null) {
            ll.validRow = false;
            return false;
        } else {
            ll.value = e;
            return true;
        }
    }


}
