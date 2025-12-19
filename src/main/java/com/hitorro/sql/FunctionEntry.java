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

import com.hitorro.util.core.Log;
import com.hitorro.util.core.classes.ClassAnoUtil;
import com.hitorro.util.core.classes.MatchClass;
import com.hitorro.sql.latch.LatchFunctionDef;
import com.hitorro.sql.latch.LatchSource;
import com.hitorro.sql.latch.var.vartypes.QueueSetter;
import com.hitorro.sql.latch.var.vartypes.VarIntf;
import com.hitorro.util.typesystem.TypeFieldDataType;

import java.util.List;

public class FunctionEntry {
    private static final MatchClass latchFunctionDef = new MatchClass(LatchFunctionDef.class);
    Class clazz;
    int cardinality = 0;
    String name;
    TypeFieldDataType[] argumentTypes;
    boolean aggregate = false;
    private LatchFunctionDef def;

    public FunctionEntry(Object o) {
        def = (LatchFunctionDef) ClassAnoUtil.getClassLevelAnnotation(o.getClass(), latchFunctionDef);
        clazz = o.getClass();
        if (def != null) {
            cardinality = def.cardinality();
            argumentTypes = def.argumentTypes();
            name = def.name();
            aggregate = def.aggregate();
        } else {
            name = clazz.getName();
        }
    }

    public static FunctionEntry getBestFit(List<VarIntf> list, List<FunctionEntry> fel) {
        FunctionEntry current = fel.get(0);
        if (fel.size() == 1) {
            return current;
        }
        int cnt = 0;
        for (int i = 0; i < fel.size(); i++) {
            FunctionEntry fe = fel.get(i);
            int c = countExactMatches(list, fe);
            if (c > cnt) {
                cnt = c;
                current = fel.get(i);
            }
        }
        if (cnt != 0) {
            return current;
        }
        int distance = Integer.MAX_VALUE;
        for (int i = 0; i < fel.size(); i++) {
            FunctionEntry fe = fel.get(i);
            int c = castingDistance(list, fe);
            if (c < distance) {
                distance = c;
                current = fel.get(i);
            }
        }

        return current;
    }

    private static int countExactMatches(List<VarIntf> list, FunctionEntry fe) {
        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getOutputDataType() == fe.argumentTypes[i]) {
                count++;
            }
        }
        return count;
    }

    private static int castingDistance(List<VarIntf> list, FunctionEntry fe) {
        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(fe.argumentTypes[i])) {
                continue;
            }
            if (list.get(i).getOutputDataType().getIsFloat() == fe.argumentTypes[i].getIsFloat()) {
                count++;
            } else {
                count += 10;
            }
        }
        return count;
    }

    public QueueSetter getCopy(int argsCount, List<VarIntf> list) {
        try {


            if (cardinality == 0 && argsCount > 0) {
                throw new LatchStackKaputException("function:%s class:%s said it had cardinality: %s but found: %s on stack",
                        name, clazz, cardinality, argsCount);
            }
            boolean elipses = false;
            if (cardinality == Integer.MAX_VALUE) {
                elipses = true;
            } else {
                if (cardinality != argsCount) {
                    throw new LatchStackKaputException("function:%s class:%s said it had cardinality: %s but found: %s on stack",
                            name, clazz, cardinality, argsCount);
                }
            }
            for (int i = 0; i < argumentTypes.length; i++) {
                list.set(i, LatchSource.cast(list.get(i), argumentTypes[i]));
            }
            if (elipses) {
                for (int i = argumentTypes.length; i < list.size(); i++) {
                    list.set(i, LatchSource.cast(list.get(i), argumentTypes[argumentTypes.length - 1]));
                }
            }
            QueueSetter qs = (QueueSetter) clazz.newInstance();
            qs.setParents(list);
            return qs;
        } catch (InstantiationException e) {
            Log.util.error("Unable to construct %s %s %e", clazz, e, e);
        } catch (IllegalAccessException e) {
            Log.util.error("Unable to construct %s %s %e", clazz, e, e);
        }
        return null;
    }
}
