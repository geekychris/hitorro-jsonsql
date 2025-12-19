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

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Segment;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Token implements Serializable, Comparable {

    public final TokenType type;
    public final int start;
    public final int length;
    public final byte pairValue;
    public final short kind = 0;

    public Token(TokenType type, int start, int length) {
        this.type = type;
        this.start = start;
        this.length = length;
        this.pairValue = 0;
    }

    public Token(TokenType type, int start, int length, byte pairValue) {
        this.type = type;
        this.start = start;
        this.length = length;
        this.pairValue = pairValue;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Object) {
            Token token = (Token) obj;
            return ((this.start == token.start) &&
                    (this.length == token.length) &&
                    (this.type.equals(token.type)));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return start;
    }

    @Override
    public String toString() {
        if (pairValue == 0) {
            return String.format("%s (%d, %d)", type, start, length);
        } else {
            return String.format("%s (%d, %d) (%d)", type, start, length, pairValue);
        }
    }

    @Override
    public int compareTo(Object o) {
        Token t = (Token) o;
        if (this.start != t.start) {
            return (this.start - t.start);
        } else if (this.length != t.length) {
            return (this.length - t.length);
        } else {
            return this.type.compareTo(t.type);
        }
    }

    public int end() {
        return start + length;
    }

    public CharSequence getText(Document doc) {
        Segment text = new Segment();
        try {
            doc.getText(start, length, text);
        } catch (BadLocationException ex) {
            Logger.getLogger(Token.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return text;
        }
    }

    public String getString(Document doc) {
        String result = "";
        try {
            result = doc.getText(start, length);
        } catch (BadLocationException ex) {
            Logger.getLogger(Token.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return result;
        }
    }
}
