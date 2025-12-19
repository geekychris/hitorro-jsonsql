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

import com.hitorro.jsontypesystem.CSV2JVSIterator;
import com.hitorro.jsontypesystem.JVS;
import com.hitorro.jsontypesystem.JsonTypeSystem;
import com.hitorro.util.basefile.fs.BaseFile;
import com.hitorro.util.basefile.tools.BaseFileUtil;
import com.hitorro.util.core.GenericKeyValue;
import com.hitorro.util.core.ListUtil;
import com.hitorro.util.core.iterator.AbstractIterator;
import com.hitorro.util.io.csv.CSVIterator;
import com.hitorro.util.json.keys.propaccess.PropaccessError;
import com.hitorro.sql.latch.JSONLatchSource;
import com.hitorro.sql.latch.LatchSource;
import com.hitorro.sql.latch.SelectItem;
import com.hitorro.sql.latch.var.vartypes.VarBooleanIntf;
import com.hitorro.sql.parser.LatchContext;
import com.hitorro.sql.parser.StatementDeParser;
import com.hitorro.util.typesystem.TypeBaseIntf;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.select.Select;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class SQLJVSIterator extends AbstractIterator<JVS> {
    private AbstractIterator<JVS> iterIn;
    private TypeBaseIntf type;
    private LatchContext lc;
    private LatchSource ls;
    private JVS curr = null;
    private VarBooleanIntf filter;
    private SelectItem[] selectList;
    private SelectItem[] aggregates;
    private boolean wildcard;
    private boolean aggregateMode = false;
    private long filteredOut = 0;

    public SQLJVSIterator(AbstractIterator<JVS> iterIn, String type, String sql) throws IOException, JSQLParserException {
        this(iterIn, JsonTypeSystem.getMe().getType(type), sql);
    }

    public SQLJVSIterator(AbstractIterator<JVS> iterIn, TypeBaseIntf type, String sql) throws IOException, JSQLParserException {
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
                JVS elem = this.next();

                GenericKeyValue gkv = new GenericKeyValue(elem, elem);
            }

        }


    }

    public static SQLJVSIterator getIteratorFromCSVFile(BaseFile bf, String type, String sql) throws IOException, JSQLParserException {
        CSVIterator csvIter = BaseFileUtil.bf2csv.apply(bf);
        AbstractIterator<JVS> iter = new CSV2JVSIterator(csvIter, JsonTypeSystem.getMe().getType(type));
        return new SQLJVSIterator(iter, type, sql);
    }

    public long getRemoved() {
        return filteredOut;
    }

    private boolean advance() {
        while (iterIn.hasNext()) {
            JVS e = iterIn.next();
            ls.present(e);
            if (filter == null || filter.getBoolean()) {
                if (wildcard) {
                    curr = e;
                } else {
                    curr = new JVS();
                    try {
                        curr.setType(e.getType());
                    } catch (PropaccessError propaccessError) {
                    }
                }

                if (aggregateMode) {
                    for (SelectItem si : aggregates) {
                        si.clock();
                    }
                }
                for (SelectItem si : selectList) {
                    try {
                        curr.set(si.getPropAccess(), si.getValue());
                    } catch (PropaccessError propaccessError) {
                    }
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
    public JVS next() {
        JVS e = null;
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
