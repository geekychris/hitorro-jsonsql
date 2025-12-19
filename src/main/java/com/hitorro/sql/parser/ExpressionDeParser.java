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

import com.hitorro.util.core.CompOperEnum;
import com.hitorro.util.core.LogicalOperEnum;
import com.hitorro.util.core.Operator;
import com.hitorro.util.core.string.Fmt;
import com.hitorro.sql.FunctionEntry;
import com.hitorro.sql.FunctionFactory;
import com.hitorro.sql.latch.var.vartypes.QueueSetter;
import com.hitorro.sql.latch.var.vartypes.VarIntf;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SubSelect;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class ExpressionDeParser implements ExpressionVisitor, ItemsListVisitor {

    protected StringBuffer buffer;
    protected SelectVisitor selectVisitor;
    protected boolean useBracketsInExprList = true;
    protected LatchContext lc;

    public ExpressionDeParser(LatchContext lc) {
        this.lc = lc;
    }

    public ExpressionDeParser(SelectVisitor selectVisitor, StringBuffer buffer, LatchContext lc) {
        this.selectVisitor = selectVisitor;
        this.buffer = buffer;
        this.lc = lc;
    }

    public StringBuffer getBuffer() {
        return buffer;
    }

    public void visit(InExpression inExpression) {
        inExpression.getLeftExpression().accept(this);
        if (inExpression.isNot()) {
            buffer.append(" NOT");
        }
        buffer.append(" IN ");

        inExpression.getItemsList().accept(this);
    }

    public void visit(IsNullExpression isNullExpression) {
        isNullExpression.getLeftExpression().accept(this);
        if (isNullExpression.isNot()) {
            lc.ls.isNullOnStack(false);
        } else {
            lc.ls.isNullOnStack(true);
        }
    }

    public void visit(JdbcParameter jdbcParameter) {
        buffer.append("?");
    }

    public void visit(LikeExpression likeExpression) {
        visitBinaryExpression(likeExpression, " LIKE ");
    }

    public void visit(ExistsExpression existsExpression) {
        if (existsExpression.isNot()) {
            buffer.append(" NOT EXISTS ");
        } else {
            buffer.append(" EXISTS ");
        }
        existsExpression.getRightExpression().accept(this);
    }

    public void visit(NullValue nullValue) {
        buffer.append("NULL");
    }

    private void visitBinaryExpression(BinaryExpression binaryExpression, String operator) {
        if (binaryExpression.isNot()) {
            buffer.append(" NOT ");
        }
        binaryExpression.getLeftExpression().accept(this);
        buffer.append(operator);
        binaryExpression.getRightExpression().accept(this);

    }

    public void visit(SubSelect subSelect) {
        buffer.append("(");
        subSelect.getSelectBody().accept(selectVisitor);
        buffer.append(")");
    }

    public void visit(Function function) {
        buffer.append(function.getName());
        String functionName = function.getName();

        List<FunctionEntry> fel = FunctionFactory.get().get(functionName);

        int argsCount = 0;
        if (function.getParameters() == null) {
            buffer.append("()");
        } else {
            int argsBase = lc.ls.getVarQ().size();
            visit(function.getParameters());
            argsCount = lc.ls.getVarQ().size() - argsBase;
        }
        List<VarIntf> list = new ArrayList(argsCount);
        lc.ls.getVarQ().fillList(list, argsCount, true);
        FunctionEntry fe = FunctionEntry.getBestFit(list, fel);
        QueueSetter qs = fe.getCopy(argsCount, list);
        lc.ls.getVarQ().push(qs);
    }

    public void visit(ExpressionList expressionList) {
        for (Iterator iter = expressionList.getExpressions().iterator(); iter.hasNext(); ) {
            Expression expression = (Expression) iter.next();
            expression.accept(this);
        }
    }

    public void visit(CaseExpression caseExpression) {
        buffer.append("CASE ");
        Expression switchExp = caseExpression.getSwitchExpression();
        if (switchExp != null) {
            switchExp.accept(this);
        }

        List clauses = caseExpression.getWhenClauses();
        for (Iterator iter = clauses.iterator(); iter.hasNext(); ) {
            Expression exp = (Expression) iter.next();
            exp.accept(this);
        }

        Expression elseExp = caseExpression.getElseExpression();
        if (elseExp != null) {
            elseExp.accept(this);
        }

        buffer.append(" END");
    }

    public void visit(WhenClause whenClause) {
        buffer.append(" WHEN ");
        whenClause.getWhenExpression().accept(this);
        buffer.append(" THEN ");
        whenClause.getThenExpression().accept(this);
    }

    public void visit(AllComparisonExpression allComparisonExpression) {
        buffer.append(" ALL ");
        allComparisonExpression.GetSubSelect().accept((ExpressionVisitor) this);
    }

    public void visit(AnyComparisonExpression anyComparisonExpression) {
        buffer.append(" ANY ");
        anyComparisonExpression.GetSubSelect().accept((ExpressionVisitor) this);
    }

    public void visit(InverseExpression inverseExpression) {
        buffer.append("-");
        inverseExpression.getExpression().accept(this);
    }

    public void visit(Concat concat) {
        concat.getLeftExpression().accept(this);
        concat.getRightExpression().accept(this);
        lc.ls.stringConcatOnStack();
    }

    public void visit(Matches matches) {
        visitBinaryExpression(matches, " @@ ");
    }



    public void visit(Multiplication multiplication) {
        visitBinaryExpression(multiplication, Operator.Mult);
    }

    public void visit(Addition addition) {
        visitBinaryExpression(addition, Operator.Add);
    }

    public void visit(Division division) {
        visitBinaryExpression(division, Operator.Divide);
    }

    public void visit(Subtraction subtraction) {
        visitBinaryExpression(subtraction, Operator.Minus);
    }

    public void visit(BitwiseAnd bitwiseAnd) {
        visitBinaryExpression(bitwiseAnd, Operator.BitwiseAnd);
    }

    public void visit(BitwiseOr bitwiseOr) {
        visitBinaryExpression(bitwiseOr, Operator.BitwiseOr);
    }

    public void visit(BitwiseXor bitwiseXor) {
        visitBinaryExpression(bitwiseXor, Operator.BitwiseXOr);
    }

    private void visitBinaryExpression(BinaryExpression binaryExpression, Operator operator) {
        binaryExpression.getLeftExpression().accept(this);
        binaryExpression.getRightExpression().accept(this);
        lc.ls.operatorOnStack(operator);

        if (binaryExpression.isNot()) {
            lc.ls.notOnStack();
        }
    }


    private void visitBinaryExpression(BinaryExpression binaryExpression, CompOperEnum operator) {
        binaryExpression.getLeftExpression().accept(this);
        binaryExpression.getRightExpression().accept(this);
        lc.ls.operatorOnStack(operator);

        if (binaryExpression.isNot()) {
            lc.ls.notOnStack();
        }
    }



    public void visit(AndExpression andExpression) {
        visitBinaryExpression(andExpression, LogicalOperEnum.And);
    }

    public void visit(OrExpression orExpression) {
        visitBinaryExpression(orExpression, LogicalOperEnum.Or);
    }

    private void visitBinaryExpression(BinaryExpression binaryExpression, LogicalOperEnum operator) {
        binaryExpression.getLeftExpression().accept(this);
        binaryExpression.getRightExpression().accept(this);
        lc.ls.operatorOnStack(operator);

        if (binaryExpression.isNot()) {
            lc.ls.notOnStack();
        }
    }


    public void visit(DateValue dateValue) {
        lc.ls.getVarQ().push(lc.ls.getConstant(lc.ls.getNextUniqueStringName(), new Date(dateValue.getValue().getTime())));
    }

    public void visit(TimestampValue timestampValue) {
        lc.ls.getVarQ().push(lc.ls.getConstant(lc.ls.getNextUniqueStringName(), new Date(timestampValue.getValue().getTime())));
    }

    public void visit(TimeValue timeValue) {
        lc.ls.getVarQ().push(lc.ls.getConstant(lc.ls.getNextUniqueStringName(), new Date(timeValue.getValue().getTime())));
    }

    public void visit(LongValue longValue) {
        lc.ls.getVarQ().push(lc.ls.getConstant(lc.ls.getNextUniqueStringName(), longValue.getValue()));
    }

    public void visit(StringValue stringValue) {
        lc.ls.getVarQ().push(lc.ls.getConstant(lc.ls.getNextUniqueStringName(), stringValue.getValue()));
    }

    public void visit(DoubleValue doubleValue) {
        lc.ls.getVarQ().push(lc.ls.getConstant(lc.ls.getNextUniqueStringName(), doubleValue.getValue()));
    }


    public void visit(MinorThan minorThan) {
        visitBinaryExpression(minorThan, CompOperEnum.LessThan);
    }

    public void visit(MinorThanEquals minorThanEquals) {
        visitBinaryExpression(minorThanEquals, CompOperEnum.LessThanOrEqual);
    }

    public void visit(NotEqualsTo notEqualsTo) {
        visitBinaryExpression(notEqualsTo, CompOperEnum.NotEqual);
    }

    public void visit(EqualsTo equalsTo) {
        visitBinaryExpression(equalsTo, CompOperEnum.Equals);
    }

    public void visit(GreaterThan greaterThan) {
        visitBinaryExpression(greaterThan, CompOperEnum.GreaterThan);
    }

    public void visit(GreaterThanEquals greaterThanEquals) {
        visitBinaryExpression(greaterThanEquals, CompOperEnum.GreaterThanOrEqual);
    }

    public void visit(Parenthesis parenthesis) {
        parenthesis.getExpression().accept(this);
        if (parenthesis.isNot()) {
            lc.ls.notOnStack();
        }
    }


    public void visit(Column tableColumn) {

        String tableName = tableColumn.getTable().getWholeTableName();
        String colName = tableColumn.getColumnName();

        if (tableName != null) {
            buffer.append(tableName + ".");
            colName = Fmt.S("%s.%s", tableName, colName);
        }

        buffer.append(colName);
        VarIntf vi = lc.ls.getLatch(colName);
        lc.ls.getVarQ().push(vi);
    }


    public void visit(Between between) {
        between.getLeftExpression().accept(this);
        between.getBetweenExpressionStart().accept(this);
        between.getBetweenExpressionEnd().accept(this);
        lc.ls.operatorBetween();
        if (between.isNot()) {
            lc.ls.notOnStack();
        }
    }
}
