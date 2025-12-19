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

package com.hitorro.sql.iterators;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.hitorro.util.basefile.fs.BaseFile;
import com.hitorro.util.basefile.tools.BaseFileUtil;
import com.hitorro.util.core.GenericKeyValue;
import com.hitorro.util.core.ListUtil;
import com.hitorro.util.core.iterator.AbstractIterator;
import com.hitorro.util.core.iterator.JsonValueSource;
import com.hitorro.util.io.csv.CSV2JSONIterator;
import com.hitorro.util.io.csv.CSVIterator;
import com.hitorro.sql.latch.JSONLatchSource;
import com.hitorro.sql.latch.LatchSource;
import com.hitorro.sql.latch.SelectItem;
import com.hitorro.sql.latch.var.vartypes.VarBooleanIntf;
import com.hitorro.sql.parser.LatchContext;
import com.hitorro.sql.parser.StatementDeParser;
import com.hitorro.util.typesystem.TypeIntf;
import com.hitorro.util.typesystem.TypeManager;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.select.Select;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class SqlIterator extends AbstractIterator<JsonValueSource> {
    private AbstractIterator<JsonValueSource> iterIn;
    private TypeIntf type;
    private LatchContext lc;
    private LatchSource ls;
    private JsonValueSource curr = null;
    private VarBooleanIntf filter;
    private SelectItem[] selectList;
    private SelectItem[] aggregates;
    private boolean wildcard;
    private boolean aggregateMode = false;
    private long filteredOut = 0;

    public SqlIterator(AbstractIterator<JsonValueSource> iterIn, String type, String sql) throws IOException, JSQLParserException {
        this(iterIn, TypeManager.get().getTypeByShortName(type), sql);
    }

    public SqlIterator(AbstractIterator<JsonValueSource> iterIn, TypeIntf type, String sql) throws IOException, JSQLParserException {
        this.type = type;
        this.iterIn = iterIn;
        CCJSqlParserManager pm = new CCJSqlParserManager();
        net.sf.jsqlparser.statement.Statement statement = pm.parse(new StringReader(sql));
        Select s = (Select) statement;
        StringBuffer sb = new StringBuffer();
        ls = new JSONLatchSource(type);
        lc = new LatchContext(ls);
        StatementDeParser deparser = new StatementDeParser(sb, lc);
        deparser.visit(s);
        filter = ls.getWhereClause();

        wildcard = lc.allColumnsSelected;
        List<SelectItem> list = ls.getSelectList();

        this.selectList = list.toArray(new SelectItem[list.size()]);

        List<SelectItem> aggr = new ArrayList();
        for (SelectItem si : selectList) {
            if (si.isAggregate()) {
                aggr.add(si);
                aggregateMode = true;
            }
        }
        aggregates = aggr.toArray(new SelectItem[aggr.size()]);
        advance();
        if (!ListUtil.nullOrEmpty(ls.getOrderList())) {
            while (this.hasNext()) {
                JsonValueSource elem = this.next();

                GenericKeyValue gkv = new GenericKeyValue(elem, elem);
            }

        }


    }

    public static SqlIterator getIteratorFromCSVFile(BaseFile bf, String type, String sql) throws IOException, JSQLParserException {
        CSVIterator csvIter = BaseFileUtil.bf2csv.apply(bf);
        AbstractIterator<JsonValueSource> iter = new CSV2JSONIterator(csvIter, TypeManager.get().getTypeByShortName(type));
        return new SqlIterator(iter, type, sql);
    }

    public long getRemoved() {
        return filteredOut;
    }

    private boolean advance() {
        while (iterIn.hasNext()) {
            JsonValueSource e = iterIn.next();
            ls.present(e);
            if (filter == null || filter.getBoolean()) {
                if (wildcard) {
                    curr = e;
                } else {
                    curr = new JsonValueSource(JsonNodeFactory.instance.objectNode());
                    curr.setType(e.getType());
                }

                if (aggregateMode) {
                    for (SelectItem si : aggregates) {
                        si.clock();
                    }
                }
                for (SelectItem si : selectList) {
                    curr.setValue(curr, si.getAs(), si.getValue(), true);
                }

                return true;
            } else {
                filteredOut++;
            }
        }
        curr = null;
        return false;
    }

    @Override
    public void close() throws Exception {
        iterIn.close();
    }

    @Override
    public boolean hasNext() {
        return curr != null;
    }

    @Override
    public JsonValueSource next() {
        JsonValueSource e = null;
        if (aggregateMode) {
            while (curr != null) {
                e = curr;
                advance();
            }
        } else {
            e = curr;
            advance();
        }
        return e;
    }


    @Override
    public void remove() {
    }
}
