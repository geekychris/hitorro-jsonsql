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

import com.hitorro.sql.latch.var.vartypes.VarBooleanIntf;
import com.hitorro.sql.latch.var.vartypes.VarIntf;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;

import java.util.Iterator;
import java.util.List;

public class SelectDeParser implements SelectVisitor, OrderByVisitor, SelectItemVisitor, FromItemVisitor {
    protected StringBuffer buffer;
    protected ExpressionVisitor expressionVisitor;
    private LatchContext lc;

    public SelectDeParser(LatchContext lc) {
        this.lc = lc;
    }

    public SelectDeParser(ExpressionVisitor expressionVisitor, StringBuffer buffer, LatchContext lc) {
        this.buffer = buffer;
        this.expressionVisitor = expressionVisitor;
        this.lc = lc;
    }

    public void visit(PlainSelect plainSelect) {
        buffer.append("SELECT ");
        Top top = plainSelect.getTop();
        if (top != null) {
            top.toString();
        }
        if (plainSelect.getDistinct() != null) {
            buffer.append("DISTINCT ");
            if (plainSelect.getDistinct().getOnSelectItems() != null) {
                buffer.append("ON (");
                for (Iterator iter = plainSelect.getDistinct().getOnSelectItems().iterator(); iter.hasNext(); ) {
                    SelectItem selectItem = (SelectItem) iter.next();
                    selectItem.accept(this);
                    if (iter.hasNext()) {
                        buffer.append(", ");
                    }
                }
                buffer.append(") ");
            }

        }
        for (Iterator iter = plainSelect.getSelectItems().iterator(); iter.hasNext(); ) {
            SelectItem selectItem = (SelectItem) iter.next();
            selectItem.accept(this);
        }
        if (plainSelect.getFromItem() != null) {
            buffer.append("FROM ");
            plainSelect.getFromItem().accept(this);
        }

        if (plainSelect.getJoins() != null) {
            for (Iterator iter = plainSelect.getJoins().iterator(); iter.hasNext(); ) {
                Join join = (Join) iter.next();
                deparseJoin(join);
            }
        }

        if (plainSelect.getWhere() != null) {
            plainSelect.getWhere().accept(expressionVisitor);
            lc.ls.setWhere((VarBooleanIntf) lc.ls.getVarQ().topAndPop());
        }

        if (plainSelect.getGroupByColumnReferences() != null) {
            buffer.append(" GROUP BY ");
            for (Iterator iter = plainSelect.getGroupByColumnReferences().iterator(); iter.hasNext(); ) {
                Expression columnReference = (Expression) iter.next();
                columnReference.accept(expressionVisitor);
                if (iter.hasNext()) {
                    buffer.append(", ");
                }
            }
        }

        if (plainSelect.getHaving() != null) {
            buffer.append(" HAVING ");
            plainSelect.getHaving().accept(expressionVisitor);
        }

        if (plainSelect.getOrderByElements() != null) {
            deparseOrderBy(plainSelect.getOrderByElements());
        }

        if (plainSelect.getLimit() != null) {
            deparseLimit(plainSelect.getLimit());
        }

    }

    public void visit(Union union) {
        for (Iterator iter = union.getPlainSelects().iterator(); iter.hasNext(); ) {
            buffer.append("(");
            PlainSelect plainSelect = (PlainSelect) iter.next();
            plainSelect.accept(this);
            buffer.append(")");
            if (iter.hasNext()) {
                buffer.append(" UNION ");
            }

        }

        if (union.getOrderByElements() != null) {
            deparseOrderBy(union.getOrderByElements());
        }

        if (union.getLimit() != null) {
            deparseLimit(union.getLimit());
        }

    }

    public void visit(OrderByElement orderBy) {
        orderBy.getExpression().accept(expressionVisitor);
        if (orderBy.isAsc()) {
            buffer.append(" ASC");
        } else {
            buffer.append(" DESC");
        }
    }

    public void visit(Column column) {
        buffer.append(column.getWholeColumnName());
    }

    public void visit(AllColumns allColumns) {
        lc.allColumnsSelected = true;
    }

    public void visit(AllTableColumns allTableColumns) {
        buffer.append(allTableColumns.getTable().getWholeTableName() + ".*");
    }

    public void visit(SelectExpressionItem selectExpressionItem) {
        selectExpressionItem.getExpression().accept(expressionVisitor);
        String name;
        if (selectExpressionItem.getAlias() != null) {
            name = selectExpressionItem.getAlias();
        } else {
            name = selectExpressionItem.getExpression().toString();
        }
        VarIntf vi = (VarIntf) lc.ls.getVarQ().topAndPop();
        lc.ls.putVar(name, vi);
        lc.ls.add(vi, name, vi.isAggregate());
    }

    public void visit(SubSelect subSelect) {
        buffer.append("(");
        subSelect.getSelectBody().accept(this);
        buffer.append(")");
    }

    public void visit(Table tableName) {
        buffer.append(tableName.getWholeTableName());
        String alias = tableName.getAlias();
        if (alias != null && !alias.isEmpty()) {
            buffer.append(" AS " + alias);
        }
    }

    public void deparseOrderBy(List orderByElements) {
        for (Iterator iter = orderByElements.iterator(); iter.hasNext(); ) {
            OrderByElement orderByElement = (OrderByElement) iter.next();
            orderByElement.accept(this);
            lc.ls.addOrderBy((VarIntf) lc.ls.getVarQ().topAndPop(), orderByElement.getExpression().toString(), orderByElement.isAsc());
        }
    }

    public void deparseLimit(Limit limit) {
        buffer.append(" LIMIT ");
        if (limit.isRowCountJdbcParameter()) {
            buffer.append("?");
        } else if (limit.getRowCount() != 0) {
            buffer.append(limit.getRowCount());
        } else {
            buffer.append("18446744073709551615");
        }

        if (limit.isOffsetJdbcParameter()) {
            buffer.append(" OFFSET ?");
        } else if (limit.getOffset() != 0) {
            buffer.append(" OFFSET " + limit.getOffset());
        }

    }

    public StringBuffer getBuffer() {
        return buffer;
    }

    public void setBuffer(StringBuffer buffer) {
        this.buffer = buffer;
    }

    public ExpressionVisitor getExpressionVisitor() {
        return expressionVisitor;
    }

    public void setExpressionVisitor(ExpressionVisitor visitor) {
        expressionVisitor = visitor;
    }

    public void visit(SubJoin subjoin) {
        buffer.append("(");
        subjoin.getLeft().accept(this);
        buffer.append(" ");
        deparseJoin(subjoin.getJoin());
        buffer.append(")");
    }

    public void deparseJoin(Join join) {
        if (join.isSimple()) {
            buffer.append(", ");
        } else {

            if (join.isRight()) {
                buffer.append("RIGHT ");
            } else if (join.isNatural()) {
                buffer.append("NATURAL ");
            } else if (join.isFull()) {
                buffer.append("FULL ");
            } else if (join.isLeft()) {
                buffer.append("LEFT ");
            }

            if (join.isOuter()) {
                buffer.append("OUTER ");
            } else if (join.isInner()) {
                buffer.append("INNER ");
            }

            buffer.append("JOIN ");

        }

        FromItem fromItem = join.getRightItem();
        fromItem.accept(this);
        if (join.getOnExpression() != null) {
            buffer.append(" ON ");
            join.getOnExpression().accept(expressionVisitor);
        }
        if (join.getUsingColumns() != null) {
            buffer.append(" USING ( ");
            for (Iterator iterator = join.getUsingColumns().iterator(); iterator.hasNext(); ) {
                Column column = (Column) iterator.next();
                buffer.append(column.getWholeColumnName());
                if (iterator.hasNext()) {
                    buffer.append(" ,");
                }
            }
            buffer.append(")");
        }

    }

}
