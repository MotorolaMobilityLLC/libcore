/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.harmony.luni.lang.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.GenericSignatureFormatError;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/**
 * Implements a parser for the generics signature attribute.
 * Uses a top-down, resursive descent parsing approach for the following grammar:
 * <pre>
 * ClassSignature ::= 
 *     OptFormalTypeParams SuperclassSignature {SuperinterfaceSignature}.
 * SuperclassSignature ::= ClassTypeSignature.
 * SuperinterfaceSignature ::= ClassTypeSignature.
 *
 * OptFormalTypeParams ::= 
 *     ["<" FormalTypeParameter {FormalTypeParameter} ">"].
 *
 * FormalTypeParameter ::= Ident ClassBound {InterfaceBound}.
 * ClassBound ::= ":" [FieldTypeSignature].
 * InterfaceBound ::= ":" FieldTypeSignature.
 *
 * FieldTypeSignature ::= 
 *     ClassTypeSignature | ArrayTypeSignature | TypeVariableSignature.
 * ArrayTypeSignature ::= "[" TypSignature.
 *
 * ClassTypeSignature ::= 
 *     "L" {Ident "/"} Ident OptTypeArguments {"." Ident OptTypeArguments} ";".
 *
 * OptTypeArguments ::= "<" TypeArgument {TypeArgument} ">".
 *
 * TypeArgument ::= ([WildcardIndicator] FieldTypeSignature) | "*".
 * WildcardIndicator ::= "+" | "-".
 *
 * TypeVariableSignature ::= "T" Ident ";".
 *
 * TypSignature ::= FieldTypeSignature | BaseType.
 * BaseType ::= "B" | "C" | "D" | "F" | "I" | "J" | "S" | "Z".
 *
 * MethodTypeSignature ::= 
 *     OptFormalTypeParams "(" {TypeSignature} ")" ReturnType {ThrowsSignature}.
 * ThrowsSignature ::= ("^" ClassTypeSignature) | ("^" TypeVariableSignature).
 *
 * ReturnType ::= TypSignature | VoidDescriptor.
 * VoidDescriptor ::= "V".
 * </pre>
 */
public class GenericSignatureParser {

    public ListOfTypes exceptionTypes;
    public ListOfTypes parameterTypes;
    public TypeVariable[] formalTypeParameters;
    public Type returnType;
    public Type fieldType;
    public ListOfTypes interfaceTypes;
    public Type superclassType;
    public ClassLoader loader;
    
    GenericDeclaration genericDecl;

    /*
     * Parser:
     */
    char symbol; // 0: eof; else valid term symbol or first char of identifier.
    String identifier;


    /* 
     * Scanner:
     * eof is private to the scan methods
     * and it's set only when a scan is issued at the end of the buffer.
     */
    private boolean eof;

    char[] buffer;
    int pos;

    public GenericSignatureParser(ClassLoader loader) {
        this.loader = loader;
    }

    void setInput(GenericDeclaration genericDecl, String input) {
        if (input != null) {
            this.genericDecl = genericDecl;
            this.buffer = input.toCharArray();
            this.eof = false;
            scanSymbol();
        }
        else {
            this.eof = true;
        }
    }

    /**
     * Parses the generic signature of a class and creates the data structure
     * representing the signature.
     * 
     * @param genericDecl the GenericDeclaration calling this method
     * @param signature the generic signature of the class
     */
    public void parseForClass(GenericDeclaration genericDecl, 
            String signature) {
        setInput(genericDecl, signature);
        if (!eof) {
            parseClassSignature();
        } else {
            if(genericDecl instanceof Class) {
                Class c = (Class) genericDecl;
                this.formalTypeParameters = ListOfVariables.empty;
                this.superclassType = c.getSuperclass();
                this.interfaceTypes = new ListOfTypes(c.getInterfaces());
            } else {
                this.formalTypeParameters = ListOfVariables.empty;
                this.superclassType = Object.class;
                this.interfaceTypes = ListOfTypes.empty;
            }
        }
    }

    /**
     * Parses the generic signature of a method and creates the data structure
     * representing the signature.
     * 
     * @param genericDecl the GenericDeclaration calling this method
     * @param signature the generic signature of the class
     */
    public void parseForMethod(GenericDeclaration genericDecl, 
            String signature) {
        setInput(genericDecl, signature);
        if (!eof) {
            parseMethodTypeSignature();
        } else {
            if(genericDecl instanceof Method) {
                Method m = (Method) genericDecl;
                this.formalTypeParameters = ListOfVariables.empty;
                this.parameterTypes = new ListOfTypes(m.getParameterTypes());
                this.exceptionTypes = new ListOfTypes(m.getExceptionTypes());
                this.returnType = m.getReturnType();
            } else {
                this.formalTypeParameters = ListOfVariables.empty;
                this.parameterTypes = ListOfTypes.empty;
                this.exceptionTypes = ListOfTypes.empty;
                this.returnType = void.class;
            }
        }
    }
    
    /**
     * Parses the generic signature of a constructor and creates the data 
     * structure representing the signature.
     * 
     * @param genericDecl the GenericDeclaration calling this method
     * @param signature the generic signature of the class
     */
    public void parseForConstructor(GenericDeclaration genericDecl, 
            String signature) {
        setInput(genericDecl, signature);
        if (!eof) {
            parseMethodTypeSignature();
        } else {
            if(genericDecl instanceof Constructor) {
                Constructor c = (Constructor) genericDecl;
                this.formalTypeParameters = ListOfVariables.empty;
                this.parameterTypes = new ListOfTypes(c.getParameterTypes());
                this.exceptionTypes = new ListOfTypes(c.getExceptionTypes());
            } else {
                this.formalTypeParameters = ListOfVariables.empty;
                this.parameterTypes = ListOfTypes.empty;
                this.exceptionTypes = ListOfTypes.empty;
            }
        }
    }

    /**
     * Parses the generic signature of a field and creates the data structure 
     * representing the signature.
     * 
     * @param genericDecl the GenericDeclaration calling this method
     * @param signature the generic signature of the class
     */
    public void parseForField(GenericDeclaration genericDecl, 
            String signature) {
        setInput(genericDecl, signature);
        if (!eof) {
            this.fieldType = parseFieldTypeSignature();
        }
    }


    //
    // Parser:
    //

    void parseClassSignature() {
        // ClassSignature ::= 
        // OptFormalTypeParameters SuperclassSignature {SuperinterfaceSignature}.

        parseOptFormalTypeParameters();

        // SuperclassSignature ::= ClassTypeSignature.
        this.superclassType = parseClassTypeSignature();

        interfaceTypes = new ListOfTypes(16);
        while (symbol > 0) {
            // SuperinterfaceSignature ::= ClassTypeSignature.
            interfaceTypes.add(parseClassTypeSignature());
        }
    }

    void parseOptFormalTypeParameters() {
        // OptFormalTypeParameters ::= 
        // ["<" FormalTypeParameter {FormalTypeParameter} ">"].

        ListOfVariables typeParams = new ListOfVariables();

        if (symbol == '<') {
            scanSymbol();
            typeParams.add(parseFormalTypeParameter());
            while ((symbol != '>') && (symbol > 0)) {
                typeParams.add(parseFormalTypeParameter());
            }
            expect('>');
        }
        this.formalTypeParameters = typeParams.getArray();
    }

    ImplForVariable<GenericDeclaration> parseFormalTypeParameter() {
        // FormalTypeParameter ::= Ident ClassBound {InterfaceBound}.

        scanIdentifier();
        String name = identifier.intern(); // FIXME: is this o.k.?

        ListOfTypes bounds = new ListOfTypes(8);

        // ClassBound ::= ":" [FieldTypeSignature].
        expect(':');
        if (symbol == 'L' || symbol == '[' || symbol == 'T') {
            bounds.add(parseFieldTypeSignature());
        }

        while (symbol == ':') {
            // InterfaceBound ::= ":" FieldTypeSignature.
            scanSymbol();
            bounds.add(parseFieldTypeSignature());
        }

        return new ImplForVariable<GenericDeclaration>(genericDecl, name, bounds);
    }

    Type parseFieldTypeSignature() {
        // FieldTypeSignature ::= ClassTypeSignature | ArrayTypeSignature 
        //         | TypeVariableSignature.

        switch (symbol) {
        case 'L':
            return parseClassTypeSignature();
        case '[':
            // ArrayTypeSignature ::= "[" TypSignature.
            scanSymbol();
            return new ImplForArray(parseTypeSignature());
        case 'T':
            return parseTypeVariableSignature();
        default:
            throw new GenericSignatureFormatError();
        }
    }

    Type parseClassTypeSignature() {
        // ClassTypeSignature ::= "L" {Ident "/"} Ident 
        //         OptTypeArguments {"." Ident OptTypeArguments} ";".

        expect('L');

        StringBuilder qualIdent = new StringBuilder();
        scanIdentifier();
        while (symbol == '/') {
            scanSymbol();
            qualIdent.append(identifier).append(".");
            scanIdentifier();
        }

        qualIdent.append(this.identifier);

        ListOfTypes typeArgs = parseOptTypeArguments();
        ImplForType parentType = 
                new ImplForType(null, qualIdent.toString(), typeArgs, loader);
        ImplForType type = parentType;

        while (symbol == '.') {
            // Deal with Member Classes:
            scanSymbol();
            scanIdentifier();
            qualIdent.append("$").append(identifier); // FIXME: is "$" correct?
            typeArgs = parseOptTypeArguments();
            type = new ImplForType(parentType, qualIdent.toString(), typeArgs, 
                    loader);
        }

        expect(';');

        return type;
    }

    ListOfTypes parseOptTypeArguments() {
        // OptTypeArguments ::= "<" TypeArgument {TypeArgument} ">".

        ListOfTypes typeArgs = new ListOfTypes(8);
        if (symbol == '<') {
            scanSymbol();

            typeArgs.add(parseTypeArgument());
            while ((symbol != '>') && (symbol > 0)) {
                typeArgs.add(parseTypeArgument());
            }
            expect('>');
        }
        return typeArgs;
    }

    Type parseTypeArgument() {
        // TypeArgument ::= (["+" | "-"] FieldTypeSignature) | "*".
        ListOfTypes extendsBound = new ListOfTypes(1);
        ListOfTypes superBound = new ListOfTypes(1);
        if (symbol == '*') {
            scanSymbol();
            extendsBound.add(Object.class);
            return new ImplForWildcard(extendsBound, superBound);
        }
        else if (symbol == '+') {
            scanSymbol();
            extendsBound.add(parseFieldTypeSignature());
            return new ImplForWildcard(extendsBound, superBound);
        }
        else if (symbol == '-') {
            scanSymbol();
            superBound.add(parseFieldTypeSignature());
            extendsBound.add(Object.class);
            return new ImplForWildcard(extendsBound, superBound);
        }
        else {
            return parseFieldTypeSignature();
        }
    }

    ImplForVariable<GenericDeclaration> parseTypeVariableSignature() {
        // TypeVariableSignature ::= "T" Ident ";".
        expect('T');
        scanIdentifier();
        expect(';');
        // Reference to type variable:
        // Note: we don't know the declaring GenericDeclaration yet.
        return new ImplForVariable<GenericDeclaration>(genericDecl, identifier);
    }

    Type parseTypeSignature() {
        switch (symbol) {
        case 'B': scanSymbol(); return byte.class;
        case 'C': scanSymbol(); return char.class;
        case 'D': scanSymbol(); return double.class;
        case 'F': scanSymbol(); return float.class;
        case 'I': scanSymbol(); return int.class;
        case 'J': scanSymbol(); return long.class;
        case 'S': scanSymbol(); return short.class;
        case 'Z': scanSymbol(); return boolean.class;
        default:
            // Not an elementary type, but a FieldTypeSignature.
            return parseFieldTypeSignature();
        }
    }

    void parseMethodTypeSignature() {
        // MethodTypeSignature ::= [FormalTypeParameters] 
        //         "(" {TypeSignature} ")" ReturnType {ThrowsSignature}.

        parseOptFormalTypeParameters();

        parameterTypes = new ListOfTypes(16);
        expect('(');
        while (symbol != ')' && (symbol > 0)) {
            parameterTypes.add(parseTypeSignature());
        }
        expect(')');

        returnType = parseReturnType();

        exceptionTypes = new ListOfTypes(8);
        while (symbol == '^') {
            scanSymbol();

            // ThrowsSignature ::= ("^" ClassTypeSignature) |
            //     ("^" TypeVariableSignature).
            if (symbol == 'T') {
                exceptionTypes.add(parseTypeVariableSignature());
            } else {
                exceptionTypes.add(parseClassTypeSignature());
            }
        }
    }

    Type parseReturnType() {
        // ReturnType ::= TypeSignature | "V".
        if (symbol != 'V') { return parseTypeSignature(); }
        else { scanSymbol(); return void.class; }
    }


    //
    // Scanner:
    //

    void scanSymbol() {
        if (!eof) {
            if (pos < buffer.length) {
                symbol = buffer[pos];
                pos++;
            } else {
                symbol = 0;
                eof = true;
            }
        } else {
            throw new GenericSignatureFormatError();
        }
    }

    void expect(char c) {
        if (symbol == c) {
            scanSymbol();
        } else {
            throw new GenericSignatureFormatError();
        }
    }

    boolean isStopSymbol(char ch) {
        switch (ch) {
        case ':':
        case '/':
        case ';':
        case '<':
        case '.':
            return true;
        }
        return false;
    }

    // PRE: symbol is the first char of the identifier.
    // POST: symbol = the next symbol AFTER the identifier. 
    void scanIdentifier() {
        if (!eof) {
            StringBuilder identBuf = new StringBuilder(32);
            if (!isStopSymbol(symbol)) {
                identBuf.append(symbol);
                do {
                    char ch = buffer[pos];
                    if ((ch >= 'a') && (ch <= 'z') || (ch >= 'A') && (ch <= 'Z')
                            || !isStopSymbol(ch)) {
                        identBuf.append(buffer[pos]);
                        pos++;
                    } else {
                        identifier = identBuf.toString();
                        scanSymbol();
                        return;
                    }
                } while (pos != buffer.length);
                identifier = identBuf.toString();
                symbol = 0;
                eof = true;
            } else {
                // Ident starts with incorrect char.
                symbol = 0;
                eof = true;
                throw new GenericSignatureFormatError(); 
            }
        } else {
            throw new GenericSignatureFormatError();
        }
    }

}
