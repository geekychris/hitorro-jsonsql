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

import com.hitorro.util.json.keys.propaccess.Propaccess;
import com.hitorro.sql.latch.var.vartypes.Aggregate;
import com.hitorro.sql.latch.var.vartypes.VarIntf;

public class SelectItem {
    private VarIntf intf;
    private Aggregate aggr;
    private String as;
    private boolean isAggregate;
    private Propaccess access;

    public Object getValue() {
        return LatchSource.getValueAsObject(getIntf());
    }

    public void clock() {
        if (isAggregate()) {
            aggr.clock();
        }
    }

    public VarIntf getIntf() {
        return intf;
    }

    public void setIntf(final VarIntf intf) {
        this.intf = intf;
        if (intf.isAggregate()) {
            aggr = (Aggregate) intf;
        }
    }

    public String getAs() {
        return as;
    }

    public void setAs(final String as) {
        this.as = as;
        this.access = new Propaccess(as);
    }

    public Propaccess getPropAccess() {
        return access;
    }

    public boolean isAggregate() {
        return isAggregate;
    }

    public void setAggregate(final boolean aggregate) {
        isAggregate = aggregate;
    }
}
