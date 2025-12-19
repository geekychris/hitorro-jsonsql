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

package com.hitorro.sql;

import com.hitorro.util.core.ListUtil;
import com.hitorro.util.core.map.MapUtil;
import com.hitorro.sql.latch.aggregate.*;
import com.hitorro.sql.latch.math.Cos;
import com.hitorro.sql.latch.math.Sin;
import com.hitorro.sql.latch.math.Sqrt;
import com.hitorro.sql.latch.string.*;

import java.util.HashMap;
import java.util.List;

public class FunctionFactory {
    private static FunctionFactory me = new FunctionFactory();

    private HashMap<String, List<FunctionEntry>> functions = new HashMap();

    private FunctionFactory() {
        addall();
    }

    public static FunctionFactory get() {
        return me;
    }

    public List<FunctionEntry> get(String name) {
        List<FunctionEntry> fel = functions.get(name.toLowerCase());
        if (!ListUtil.nullOrEmpty(fel)) {
            return fel;
        }
        return null;
    }

    public void add(Object setter) {
        FunctionEntry fe = new FunctionEntry(setter);
        MapUtil.addMapListValue(functions, fe.name, fe, true);
    }

    private void addall() {
        add(new Sin());
        add(new Cos());
        add(new Sin());
        add(new Sqrt());
        add(new LeftTrim());
        add(new Trim());
        add(new RightTrim());
        add(new Length());
        add(new Replace());

        add(new Count());

        add(new MinLong());
        add(new MaxLong());
        add(new SumLong());

        add(new MinDouble());
        add(new MaxDouble());
        add(new SumDouble());
    }
}

