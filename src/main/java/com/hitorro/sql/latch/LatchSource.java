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

import com.hitorro.util.core.ArrayStack;
import com.hitorro.util.core.CompOperEnum;
import com.hitorro.util.core.LogicalOperEnum;
import com.hitorro.util.core.Operator;
import com.hitorro.util.core.map.HashHashMap;
import com.hitorro.util.core.string.Fmt;
import com.hitorro.util.core.string.StringUtil;
import com.hitorro.util.json.keys.BasefileProperty;
import com.hitorro.sql.latch.coercion.booleanto.BooleanToXXX;
import com.hitorro.sql.latch.coercion.dateto.DateToXXX;
import com.hitorro.sql.latch.coercion.doubleto.DoubleToXXX;
import com.hitorro.sql.latch.coercion.floatto.FloatToXXX;
import com.hitorro.sql.latch.coercion.integerto.IntegerToXXX;
import com.hitorro.sql.latch.coercion.longto.LongToXXX;
import com.hitorro.sql.latch.coercion.shortto.ShortToXXX;
import com.hitorro.sql.latch.coercion.stringto.StringToXXX;
import com.hitorro.sql.latch.comparators.CompBase;
import com.hitorro.sql.latch.exceptions.CastException;
import com.hitorro.sql.latch.exceptions.LatchException;
import com.hitorro.sql.latch.exceptions.OperatorException;
import com.hitorro.sql.latch.exceptions.TypeException;
import com.hitorro.sql.latch.latches.*;
import com.hitorro.sql.latch.logicalopers.IsNotNull;
import com.hitorro.sql.latch.logicalopers.LogicalOperator;
import com.hitorro.sql.latch.logicalopers.NotOperator;
import com.hitorro.sql.latch.math.operator.Operators;
import com.hitorro.sql.latch.string.BinaryConcat;
import com.hitorro.sql.latch.var.*;
import com.hitorro.sql.latch.var.vartypes.*;
import com.hitorro.util.typesystem.FieldBaseIntf;
import com.hitorro.util.typesystem.TypeBaseIntf;
import com.hitorro.util.typesystem.TypeFieldDataType;
import com.hitorro.util.typesystem.TypeFieldDataTypeMapMapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public abstract class LatchSource<T> {
    public static BasefileProperty CoercionMapFile = new BasefileProperty("latchsource.coercion.file", "", "file://${ht_data}/sql/coercion.csv");
    protected static HashHashMap<TypeFieldDataType, TypeFieldDataType, TypeFieldDataType> coercionMapper = getCoercionMapper();
    public long currentRow = 0;
    protected T row;
    protected TypeBaseIntf type;
    protected HashMap<String, VarIntf> vars = new HashMap<String, VarIntf>();
    protected int stringName = 0;
    private VarIntf leftFromStack;
    private VarIntf rightFromStack;
    private ArrayStack<VarIntf> dq = new ArrayStack<VarIntf>();
    private List<SelectItem> selectList = new ArrayList();

    private List<OrderItem> orderList = new ArrayList();

    private VarBooleanIntf whereClause;

    public LatchSource(TypeBaseIntf type) {
        this.type = type;

    }

    private static HashHashMap<TypeFieldDataType, TypeFieldDataType, TypeFieldDataType> getCoercionMapper() {
        TypeFieldDataTypeMapMapper mapper = new TypeFieldDataTypeMapMapper();

        return mapper.get(CoercionMapFile.apply(), null, true);
    }

    public static VarIntf cast(VarIntf varI, TypeFieldDataType castTo) {
        TypeFieldDataType varType = varI.getOutputDataType();
        if (varType == castTo) {
            return varI;
        }
        switch (varType) {
            case Double:
                return DoubleToXXX.get(castTo, varI);
            case Int:
                return IntegerToXXX.get(castTo, varI);
            case Long:
                return LongToXXX.get(castTo, varI);
            case Float:
                return FloatToXXX.get(castTo, varI);
            case Short:
                return ShortToXXX.get(castTo, varI);
            case Boolean:
                return BooleanToXXX.get(castTo, varI);
            case String:
                return StringToXXX.get(castTo, varI);
            case Date:
                return DateToXXX.get(castTo, varI);
            case HTSerializable:
                return varI;
            default:
                throw new LatchException("Dont know how to cast from type %s", varType);
        }
    }

    public static Object getValueAsObject(VarIntf intf) {
        switch (intf.getOutputDataType()) {
            case Double:
                return ((VarDoubleIntf) intf).getDouble();
            case Int:
                return ((VarIntIntf) intf).getInt();
            case Long:
                return ((VarLongIntf) intf).getLong();
            case Float:
                return ((VarFloatIntf) intf).getFloat();
            case Short:
                return ((VarShortIntf) intf).getShort();
            case Boolean:
                return ((VarBooleanIntf) intf).getBoolean();
            case String:
                return ((VarStringIntf) intf).getString();
            case Date:
                return ((VarDateIntf) intf).getDate();
        }
        return null;
    }

    public static void assertType(TypeFieldDataType type, VarIntf var) throws TypeException {
        if (type != var.getOutputDataType()) {
            throw new TypeException("Type %s != %s", type, var.getOutputDataType());
        }
    }

    public static TypeFieldDataType getPreferredCast(TypeFieldDataType dfdtLeft, TypeFieldDataType dfdtRight) throws CastException {
        if (dfdtLeft == dfdtRight) {
            return dfdtLeft;
        }
        return coercionMapper.get(dfdtLeft, dfdtRight);
    }

    public List<OrderItem> getOrderList() {
        return orderList;
    }

    public void setWhere(VarBooleanIntf where) {
        whereClause = where;
    }

    public VarBooleanIntf getWhereClause() {
        return whereClause;
    }

    public String getNextUniqueStringName() {
        return Fmt.S("var_%s", stringName++);
    }

    public void addOrderBy(VarIntf intf, String fieldName, boolean asc) {
        orderList.add(new OrderItem(intf, fieldName, asc));
    }

    public ArrayStack<VarIntf> getVarQ() {
        return dq;
    }

    public void present(T t) {
        row = t;
        currentRow++;
    }

    public abstract boolean get(String field, StringLatch ll);

    public abstract boolean get(String field, LongLatch ll);

    public abstract boolean get(String field, DateLatch ll);

    public abstract boolean get(String field, ShortLatch ll);

    public abstract boolean get(String field, IntLatch il);

    public abstract boolean get(String field, FloatLatch fl);

    public abstract boolean get(String field, DoubleLatch dl);

    public abstract boolean get(String field, ObjectLatch ol);

    public VarIntf getVar(String v) {
        return vars.get(v.toLowerCase());
    }

    public VarIntf putVar(String v, VarIntf var) {
        if (var != null && !StringUtil.nullOrEmptyOrBlankString(v)) {
            vars.put(v.toLowerCase(), var);
        }
        return var;
    }

    public VarIntf getConstant(String name, String val) {
        VarString vs = new VarString(name);
        vs.value = val;
        return this.putVar(name, vs);
    }

    public VarIntf getConstant(String name, int val) {
        VarInt vs = new VarInt(name);
        vs.value = val;
        return this.putVar(name, vs);
    }

    public VarIntf getConstant(String name, long val) {
        VarLong vs = new VarLong(name);
        vs.value = val;
        return this.putVar(name, vs);
    }

    public VarIntf getConstant(String name, double val) {
        VarDouble vs = new VarDouble(name);
        vs.value = val;
        return this.putVar(name, vs);
    }

    public VarIntf getConstant(String name, float val) {
        VarFloat vs = new VarFloat(name);
        vs.value = val;
        return this.putVar(name, vs);
    }

    public VarIntf getConstant(String name, short val) {
        VarShort vs = new VarShort(name);
        vs.value = val;
        return this.putVar(name, vs);
    }

    public VarIntf getConstant(String name, boolean val) {
        VarBoolean vs = new VarBoolean(name);
        vs.value = val;
        return this.putVar(name, vs);
    }

    public VarIntf getConstant(String name, Date val) {
        VarDate vs = new VarDate(name);
        vs.value = val;
        return this.putVar(name, vs);
    }

    public VarIntf getLatch(String field) {
        VarIntf v = getVar(field);
        if (v != null) {
            return v;
        }
        FieldBaseIntf tfi = type.getField(field);
        if (tfi != null) {
            return putVar(field, Latch.getLatch(this, tfi));
        }
        throw new LatchException("Unable to find field %s in type %s", field, type.getName());
    }

    public VarIntf getOperator(String leftVar, String rightVar, String operator, String storeAsVar)
            throws LatchException {
        VarIntf left = this.getVar(leftVar);
        VarIntf right = this.getVar(rightVar);

        assertLeftRightHaveValues(leftVar, rightVar, left, right);
        Operator op = Operator.get(operator);
        if (op == null) {
            throw new OperatorException("Unknown operator %s", operator);
        } else {
            TypeFieldDataType castType = getPreferredCast(left.getOutputDataType(), right.getOutputDataType());
            return this.putVar(storeAsVar, Operators.get(castType, op, cast(left, castType), cast(right, castType)));
        }
    }

    public VarIntf castAndStore(String var, String storeAsVar, TypeFieldDataType castTo) {
        VarIntf varI = getVar(var);
        if (varI == null) {
            return null;
        }
        TypeFieldDataType varType = varI.getOutputDataType();
        if (varType == castTo) {
            return putVar(storeAsVar, varI);
        }
        switch (varType) {
            case Double:
                return putVar(storeAsVar, DoubleToXXX.get(castTo, varI));
            case Int:
                return putVar(storeAsVar, IntegerToXXX.get(castTo, varI));
            case Long:
                return putVar(storeAsVar, LongToXXX.get(castTo, varI));
            case Float:
                return putVar(storeAsVar, FloatToXXX.get(castTo, varI));
            case Short:
                return putVar(storeAsVar, ShortToXXX.get(castTo, varI));
            case Boolean:
                return putVar(storeAsVar, BooleanToXXX.get(castTo, varI));
            case String:
                return putVar(storeAsVar, StringToXXX.get(castTo, varI));
            case Date:
                return putVar(storeAsVar, DateToXXX.get(castTo, varI));
        }
        return null;
    }

    public VarBooleanIntf getComparator(String leftVar, String rightVar, String operator, String storeAsVar)
            throws LatchException {
        VarIntf left = this.getVar(leftVar);
        VarIntf right = this.getVar(rightVar);

        assertLeftRightHaveValues(leftVar, rightVar, left, right);
        CompOperEnum op = CompOperEnum.compContext.getByShortName(operator);
        if (op == null) {
            throw new OperatorException("Unknown operator %s", operator);
        } else {
            TypeFieldDataType castType = getPreferredCast(left.getOutputDataType(), right.getOutputDataType());
            return (VarBooleanIntf) putVar(storeAsVar, CompBase.get(castType, op, left, right));
        }
    }

    private void assertLeftRightHaveValues(final String leftVar, final String rightVar, final VarIntf left, final VarIntf right) throws LatchException {
        if (left == null) {
            throw new LatchException("Undefined variable %s", leftVar);
        } else if (right == null) {
            throw new LatchException("Undefined variable %s", rightVar);
        }
    }

    private void castLeftRight(TypeFieldDataType type) {
        leftFromStack = cast(leftFromStack, type);
        rightFromStack = cast(rightFromStack, type);
    }


    private boolean popLeftRightFromStack() {
        rightFromStack = this.dq.topAndPop();
        leftFromStack = this.dq.topAndPop();
        return true;
    }

    public VarIntf getLogicalOperator(String leftVar, String rightVar, String operator, String storeAsVar)
            throws LatchException {
        VarIntf left = this.getVar(leftVar);
        VarIntf right = this.getVar(rightVar);
        assertType(TypeFieldDataType.Boolean, left);
        assertType(TypeFieldDataType.Boolean, right);
        assertLeftRightHaveValues(leftVar, rightVar, left, right);
        LogicalOperEnum op = LogicalOperEnum.get(operator);
        if (op == null) {
            throw new OperatorException("Unknown operator %s", operator);
        }
        return this.putVar(storeAsVar, LogicalOperator.get(op, left, right));
    }


    public boolean operatorBetween() throws LatchException {
        popLeftRightFromStack();
        assertLeftRightHaveValues("leftVal", "rightVal", leftFromStack, rightFromStack);
        VarIntf val = this.dq.topAndPop();

        TypeFieldDataType castLeftType = getPreferredCast(leftFromStack.getOutputDataType(), val.getOutputDataType());

        TypeFieldDataType castRightType = getPreferredCast(val.getOutputDataType(), rightFromStack.getOutputDataType());
        VarBooleanIntf leftComp = CompBase.get(castLeftType, CompOperEnum.LessThanOrEqual, leftFromStack, val);
        VarBooleanIntf rightComp = CompBase.get(castRightType, CompOperEnum.GreaterThanOrEqual, val, rightFromStack);

        LogicalOperator lo = LogicalOperator.get(LogicalOperEnum.And, leftComp, rightComp);
        this.dq.push(lo);
        return true;
    }

    public boolean operatorOnStack(CompOperEnum operator) throws LatchException {
        popLeftRightFromStack();
        assertLeftRightHaveValues("leftVal", "rightVal", leftFromStack, rightFromStack);
        TypeFieldDataType castType = getPreferredCast(leftFromStack.getOutputDataType(), rightFromStack.getOutputDataType());
        castLeftRight(castType);
        VarIntf vi = CompBase.get(castType, operator, leftFromStack, rightFromStack);
        this.dq.push(vi);
        return true;
    }

    public boolean operatorOnStack(Operator operator) throws LatchException {
        popLeftRightFromStack();
        assertLeftRightHaveValues("leftVal", "rightVal", leftFromStack, rightFromStack);
        TypeFieldDataType castType = getPreferredCast(leftFromStack.getOutputDataType(), rightFromStack.getOutputDataType());
        castLeftRight(castType);
        VarIntf vi = Operators.get(castType, operator, leftFromStack, rightFromStack);
        this.dq.push(vi);
        return true;
    }

    public boolean operatorOnStack(LogicalOperEnum operator) throws LatchException {
        popLeftRightFromStack();
        assertLeftRightHaveValues("leftVal", "rightVal", leftFromStack, rightFromStack);
        LogicalOperator op = LogicalOperator.get(operator, leftFromStack, rightFromStack);
        this.dq.push(op);
        return true;
    }

    public boolean isNullOnStack(boolean notNull) {
        IsNotNull notNullOper = new IsNotNull();
        notNullOper.setParent(this.getVarQ().topAndPop());
        if (notNull) {
            getVarQ().push(notNullOper);
        } else {
            NotOperator noper = new NotOperator();
            noper.setParent(notNullOper);
            getVarQ().push(noper);
        }
        return true;
    }

    public boolean stringConcatOnStack() {
        popLeftRightFromStack();
        assertLeftRightHaveValues("leftVal", "rightVal", leftFromStack, rightFromStack);
        BinaryConcat concat = new BinaryConcat();
        concat.set((VarStringIntf) leftFromStack, (VarStringIntf) rightFromStack);
        this.dq.push(concat);
        return true;
    }

    public void add(VarIntf intf, String as, boolean isAggregate) {
        SelectItem si = new SelectItem();
        si.setIntf(intf);
        si.setAs(as);
        si.setAggregate(isAggregate);
        selectList.add(si);
    }

    public List<SelectItem> getSelectList() {
        return selectList;
    }

    public boolean notOnStack() {
        NotOperator no = new NotOperator();
        VarIntf vi = this.dq.topAndPop();
        if (vi.getOutputDataType() != TypeFieldDataType.Boolean) {
            throw new LatchException("Stack value %s is not of type boolean it is %s", vi, vi.getOutputDataType());
        }
        no.setParent((VarBoolean) vi);
        this.dq.push(no);
        return true;
    }

}
