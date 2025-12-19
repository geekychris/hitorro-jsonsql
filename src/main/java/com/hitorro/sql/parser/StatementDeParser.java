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

package com.hitorro.sql.parser;

import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.WithItem;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;

import java.util.Iterator;

public class StatementDeParser implements StatementVisitor {
    protected StringBuffer buffer;
    protected LatchContext lc;

    public StatementDeParser(StringBuffer buffer, LatchContext lc) {
        this.buffer = buffer;
        this.lc = lc;
    }

    public void visit(CreateTable createTable) {
    }

    public void visit(Delete delete) {
    }

    public void visit(Drop drop) {

    }

    public void visit(Insert insert) {

    }

    public void visit(Replace replace) {
    }

    public void visit(Select select) {
        SelectDeParser selectDeParser = new SelectDeParser(lc);
        selectDeParser.setBuffer(buffer);
        ExpressionDeParser expressionDeParser = new ExpressionDeParser(selectDeParser, buffer, lc);
        selectDeParser.setExpressionVisitor(expressionDeParser);
        if (select.getWithItemsList() != null && !select.getWithItemsList().isEmpty()) {
            buffer.append("WITH ");
            for (Iterator iter = select.getWithItemsList().iterator(); iter.hasNext(); ) {
                WithItem withItem = (WithItem) iter.next();
                buffer.append(withItem);
                if (iter.hasNext()) {
                    buffer.append(",");
                }
                buffer.append(" ");
            }
        }
        select.getSelectBody().accept(selectDeParser);

    }

    public void visit(Truncate truncate) {

    }

    public void visit(Update update) {


    }

    public StringBuffer getBuffer() {
        return buffer;
    }

    public void setBuffer(StringBuffer buffer) {
        this.buffer = buffer;
    }

}
