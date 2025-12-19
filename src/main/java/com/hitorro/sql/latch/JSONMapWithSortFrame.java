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

import com.hitorro.util.core.JavaObjectOrderEnum;
import com.hitorro.util.core.JavaObjectOrderEnumCompInterface;
import com.hitorro.util.core.string.StringUtil;
import com.hitorro.util.json.JSONElement;
import com.hitorro.util.json.JSONMap;
import com.hitorro.sql.latch.exceptions.LatchException;

import java.util.Map;
import java.util.Set;

public class JSONMapWithSortFrame extends JSONMap implements JavaObjectOrderEnumCompInterface {
    public static final String SorterKey = "sorter";
    private Object sortFrame[];


    public JSONMapWithSortFrame(Map<String, JSONElement> map) {
        super(map);
    }

    public Object[] getSortFrame(JavaObjectOrderEnum[] barrel, String names[], int length) {
        if (sortFrame == null) {
            fillFrameFromObject(barrel, names, length);
        }
        return sortFrame;
    }

    public JavaObjectOrderEnum[] getBarrelSorter() {
        JSONMap map = (JSONMap) this.get(SorterKey);
        int size = map.size();
        JavaObjectOrderEnum frame[] = new JavaObjectOrderEnum[size];
        for (int i = 0; i < size; i++) {
            Set<String> keys = map.keySet();
            for (String key : keys) {
                int index = StringUtil.getIntegerNumberFromText(key);
                frame[index] = JavaObjectOrderEnum.getByName(key);
            }
        }
        return frame;
    }

    public void fillFrameFromObject(JavaObjectOrderEnum types[], String names[], int length) {
        for (int i = 0; i < length; i++) {
            JSONElement elem = this.get(names[i]);
            if (elem == null) {
                throw new LatchException("Object is missing key %s other values: %m", names[i], this);
            }
            sortFrame[i] = elem.get();
        }
    }
}
