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


public enum TokenType {

    OPERATOR, // Language operators
    DELIMITER, // Delimiters.  Constructs that are not necessarily operators for a language
    KEYWORD, // language reserved keywords
    KEYWORD2, // Other language reserved keywords, like C #defines
    IDENTIFIER, // identifiers, variable names, class names
    NUMBER,     // numbers in various formats
    STRING,     // String
    STRING2,    // For highlighting meta chars within a String
    COMMENT,    // comments
    COMMENT2,   // special stuff within comments
    REGEX,      // regular expressions
    REGEX2,     // special chars within regular expressions
    TYPE,       // Types, usually not keywords, but supported by the language
    TYPE2,      // Types from standard libraries
    TYPE3,      // Types for users
    DEFAULT,    // any other text
    WARNING,    // Text that should be highlighted as a warning
    ERROR;      // Text that signals an error

    public static boolean isComment(Token t) {
        return t != null && (t.type == COMMENT || t.type == COMMENT2);
    }

    public static boolean isKeyword(Token t) {
        return t != null && (t.type == KEYWORD || t.type == KEYWORD2);
    }


    public static boolean isString(Token t) {
        return t != null && (t.type == STRING || t.type == STRING2);
    }
}

