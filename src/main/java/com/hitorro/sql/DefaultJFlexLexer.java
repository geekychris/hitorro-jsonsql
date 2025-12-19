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

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class DefaultJFlexLexer implements Lexer {

    protected int tokenStart;
    protected int tokenLength;
    protected int offset;

    protected Token token(TokenType type, int tStart, int tLength,
                          int newStart, int newLength) {
        tokenStart = newStart;
        tokenLength = newLength;
        return new Token(type, tStart + offset, tLength);
    }

    protected Token token(TokenType type, int start, int length) {
        return new Token(type, start + offset, length);
    }

    protected Token token(TokenType type) {
        return new Token(type, yychar() + offset, yylength());
    }

    protected Token token(TokenType type, int pairValue) {
        return new Token(type, yychar() + offset, yylength(), (byte) pairValue);
    }

    @Override
    public void parse(char buff[], int offset, int size, int ofst, List<Token> tokens) {
        try {
            CharArrayReader reader = new CharArrayReader(buff, offset, size);
            yyreset(reader);
            this.offset = ofst;
            for (Token t = yylex(); t != null; t = yylex()) {
                tokens.add(t);
            }
        } catch (IOException ex) {
            Logger.getLogger(DefaultJFlexLexer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public abstract void yyreset(Reader reader);

    public abstract Token yylex() throws java.io.IOException;

    public abstract char yycharat(int pos);

    public abstract int yylength();

    public abstract String yytext();

    public abstract int yychar();
}
