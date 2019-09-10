/*
 * Copyright (c) 2003-2019, Live Media Pty. Ltd.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice, this list of
 *     conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice, this list of
 *     conditions and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *  3. Neither the name of Live Media nor the names of its contributors may be used to endorse
 *     or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.argot.compiler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.logging.Logger;

import org.antlr.v4.runtime.tree.TerminalNode;

import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.TypeMap;
import com.argot.auto.TypeConstructorAuto;
import com.argot.compiler.ArgotParser.ArrayContext;
import com.argot.compiler.ArgotParser.ClusterContext;
import com.argot.compiler.ArgotParser.DefinitionContext;
import com.argot.compiler.ArgotParser.ExpressionContext;
import com.argot.compiler.ArgotParser.ExpressionIdentifierContext;
import com.argot.compiler.ArgotParser.FileContext;
import com.argot.compiler.ArgotParser.HeaderlineContext;
import com.argot.compiler.ArgotParser.HeadersContext;
import com.argot.compiler.ArgotParser.IdentifierContext;
import com.argot.compiler.ArgotParser.ImportlContext;
import com.argot.compiler.ArgotParser.LoadContext;
import com.argot.compiler.ArgotParser.MappedIdentifierContext;
import com.argot.compiler.ArgotParser.PrimaryContext;
import com.argot.compiler.ArgotParser.RelationContext;
import com.argot.compiler.ArgotParser.ReserveContext;
import com.argot.compiler.ArgotParser.SequenceContext;
import com.argot.compiler.ArgotParser.TagContext;
import com.argot.compiler.ArgotParser.TypeIdentifierContext;
import com.argot.compiler.ArgotParser.ValueContext;
import com.argot.compiler.dictionary.LibraryDefinition;
import com.argot.compiler.dictionary.LibraryEntry;
import com.argot.compiler.dictionary.LibraryName;
import com.argot.compiler.dictionary.LibraryRelation;
import com.argot.compiler.primitive.ArgotPrimitiveParser;
import com.argot.meta.DictionaryName;
import com.argot.meta.MetaCluster;
import com.argot.meta.MetaDefinition;
import com.argot.meta.MetaIdentity;
import com.argot.meta.MetaName;
import com.argot.meta.MetaReference;
import com.argot.meta.MetaSequence;
import com.argot.meta.MetaTag;
import com.argot.meta.MetaVersion;

public class ArgotCompilerListener extends ArgotBaseListener {

    Logger LOG = Logger.getLogger(ArgotCompilerListener.class.getName());

    private List<String> _errors = new ArrayList<String>();

    public List<String> getErrors() {
        return _errors;
    }

    private TypeLibrary _library;

    private TypeMap _map;

    private ArgotCompiler _compiler;

    private TypeMap _readMap;

    private Stack<TypeElement> _expressionStack = new Stack<TypeElement>();

    private Stack<Object> valueStack = new Stack<>();

    public Object construct(int definitionId, Object[] objects, Class<?> clss) throws TypeException {
        TypeElement structure = _library.getStructure(definitionId);
        if (structure instanceof MetaSequence) {
            MetaSequence sequence = (MetaSequence) structure;
            for (int x = 0; x < sequence.size(); x++) {
                _readMap.getStreamId(sequence.getElement(x).getTypeId());
            }

        }

        TypeConstructorAuto autoConstructor = new TypeConstructorAuto(clss);
        return autoConstructor.construct(null, objects);
    }

    public void setLibrary(TypeLibrary typeLibrary) {
        this._library = typeLibrary;
    }

    public void setTypeMap(TypeMap map) {
        this._map = map;
    }

    public void setReadTypeMap(TypeMap readMap) {
        this._readMap = readMap;
    }

    public void setArgotCompiler(ArgotCompiler argotCompiler) {
        this._compiler = argotCompiler;
    }

    @Override
    public void exitFile(FileContext ctx) {
        super.exitFile(ctx);
    }

    @Override
    public void exitHeaders(HeadersContext ctx) {
        super.exitHeaders(ctx);
    }

    @Override
    public void exitHeaderline(HeaderlineContext ctx) {

        super.exitHeaderline(ctx);
    }

    @Override
    public void exitImportl(ImportlContext ctx) {
        // This will import into the map the type specified.
        String typename = ctx.identifier(0).getText();
        try {

            // _map.map( _lastType++, _library.getTypeId( typename ) );
            _readMap.getStreamId(_library.getTypeId(typename));
        } catch (TypeException ex) {
            ex.printStackTrace();
            throw new ArgotParserException("Failed to import " + typename, null, null, ctx);
        }
        super.exitImportl(ctx);
    }

    @Override
    public void exitCluster(ClusterContext ctx) {
        String clustername = ctx.identifier().getText();

        try {

            MetaName name = MetaName.parseName(_library, clustername);
            _compiler.registerLibraryType(_map, new LibraryName(name), new MetaCluster());
        } catch (TypeException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Failed to create cluster:" + clustername, ex);
        }
        super.exitCluster(ctx);
    }

    @Override
    public void exitDefinition(DefinitionContext ctx) {

        short ma = Short.parseShort(ctx.Int(0).getText());
        short mi = Short.parseShort(ctx.Int(1).getText());
        String defname = ctx.identifier().getText();

        try {
            MetaDefinition expression = null;
            Object seq = this.valueStack.pop();
            if (seq instanceof List) {
                @SuppressWarnings("rawtypes")
                List list = (List) seq;
                if (list.size() == 1 && list.get(0) instanceof MetaDefinition) {
                    expression = (MetaDefinition) list.get(0);
                } else {
                    throw new ArgotParserException("Expression not a MetaExpression type", null, null, ctx);
                }

            } else if (seq instanceof MetaDefinition) {
                expression = (MetaDefinition) seq;
            } else {
                throw new ArgotParserException("Expression not a MetaExpression type", null, null, ctx);
            }

            MetaName name = MetaName.parseName(_library, defname);
            MetaVersion version = new MetaVersion(ma, mi);
            _compiler.registerLibraryType(_map, new LibraryDefinition(name, version), expression);
        } catch (TypeException ex) {
            ex.printStackTrace();
            throw new ArgotParserException("Failed to create name from:" + defname, null, null, ctx);
        }
        super.exitDefinition(ctx);
    }

    @Override
    public void enterRelation(RelationContext ctx) {
        valueStack.push(new ArrayList<Object>());
        super.enterRelation(ctx);
    }

    @Override
    public void exitRelation(RelationContext ctx) {

        short ma = Short.parseShort(ctx.Int(0).getText());
        short mi = Short.parseShort(ctx.Int(1).getText());
        String defname = ctx.identifier().getText();
        String tag = ctx.QSTRING().getText();

        try {
            MetaName name = MetaName.parseName(_library, defname);
            MetaVersion version = new MetaVersion(ma, mi);
            int id = _library.getTypeId(name);

            MetaDefinition expression = null;

            Object top = this.valueStack.pop();
            if (top instanceof List) {
                @SuppressWarnings("rawtypes")
                List list = (List) top;
                if (list.size() == 1 && list.get(0) instanceof MetaDefinition) {
                    expression = (MetaDefinition) list.get(0);
                } else {
                    throw new ArgotParserException("Expression not a MetaExpression type", null, null, ctx);
                }

            } else if (top instanceof MetaDefinition) {
                expression = (MetaDefinition) top;
            } else {
                throw new ArgotParserException("Expression not a MetaExpression type", null, null, ctx);
            }

            _compiler.registerLibraryType(_map, new LibraryRelation(id, version, tag), expression);
        } catch (TypeException ex) {
            ex.printStackTrace();
            throw new ArgotParserException("Failed to create name from:" + defname, null, null, ctx);
        }

        super.exitRelation(ctx);
    }

    @Override
    public void enterSequence(SequenceContext ctx) {
        valueStack.push(new ArrayList<Object>());
        super.enterSequence(ctx);
    }

    @Override
    public void exitSequence(SequenceContext ctx) {

        try {
            Object top = valueStack.pop();
            if (!(top instanceof ArrayList)) {
                throw new ArgotParserException("Sequence did not find values on stack", null, null, ctx);
            }

            @SuppressWarnings("unchecked")
            ArrayList<Object> list = (ArrayList<Object>) top;

            MetaSequence sequence = new MetaSequence(list.toArray());
            valueStack.push(sequence);
        } catch (IllegalArgumentException ex) {
            throw new ArgotParserException("Failed to create sequence", null, null, ctx);
        }
        super.exitSequence(ctx);
    }

    @Override
    public void exitTag(TagContext ctx) {

        String name = ctx.identifier(0).getText();
        String argotType = ctx.identifier(1).getText();
        try {

            int id = _library.getTypeId(argotType);

            Object top = valueStack.peek();
            if (!(top instanceof ArrayList)) {
                throw new ArgotParserException("Tag did not find values on stack", null, null, ctx);
            }

            @SuppressWarnings("unchecked")
            ArrayList<Object> list = (ArrayList<Object>) top;

            list.add(new MetaTag(name, new MetaReference(id)));
        } catch (TypeException ex) {
            throw new ArgotParserException("No type found for #" + argotType, null, null, ctx);
        }
        super.exitTag(ctx);
    }

    @Override
    public void exitReserve(ReserveContext ctx) {

        String typename = ctx.identifier().getText();

        try {

            //MetaName name = MetaName.parseName(_library, typename);
            _library.register(new DictionaryName(_library, typename), new MetaIdentity());
        } catch (TypeException ex) {
            ex.printStackTrace();
            throw new ArgotParserException("failed to reserve " + typename, null, null, ctx);
        }
        super.exitReserve(ctx);
    }

    @Override
    public void exitLoad(LoadContext ctx) {

        String filenameText = ctx.QSTRING().getText();
        String classname = ctx.identifier().getText();

        // This will load the file specified.
        String filename = filenameText;

        String loadername = null;

        if (classname != null)
            loadername = classname;

        if ("\"".equals(filename.substring(0, 1))) {
            filename = filename.substring(1, filename.length() - 1);
        }
        try {
            System.out.println("LOADING: " + filename);
            _compiler.loadDictionary(filename, loadername);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new ArgotParserException("failed to load " + filename + ".\n" + e.getMessage(), null, null, ctx);
        } catch (TypeException e) {
            e.printStackTrace();
            throw new ArgotParserException("failed to load " + filename + ".\n" + e.getMessage(), null, null, ctx);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ArgotParserException("failed to load " + filename + ".\n" + e.getMessage(), null, null, ctx);
        }
        super.exitLoad(ctx);
    }

    @Override
    public void exitExpressionIdentifier(ExpressionIdentifierContext ctx) {

        TerminalNode major = ctx.Int(0);
        TerminalNode minor = ctx.Int(1);
        TerminalNode id = ctx.identifier().IDENTIFIER();

        try {

            int nid;
            if (major != null & minor != null) {
                short ma = Short.parseShort(major.getText());
                short mi = Short.parseShort(minor.getText());
                //MetaName name = MetaName.parseName(_library, id.getText());
                MetaVersion version = new MetaVersion(ma, mi);
                nid = _readMap.getStreamId(_library.getTypeId(id.getText(), version.toString()));
                _expressionStack.push(_library.getStructure(_readMap.getDefinitionId(nid)));
            } else {
                nid = _readMap.getStreamId(_library.getTypeId(id.getText()));
                _expressionStack.push(_library.getStructure(_readMap.getDefinitionId(nid)));
            }

        } catch (TypeException ex) {
            throw new ArgotParserException("Failed to create object for: " + id.getText() + "\n" + ex.getMessage(), null, null, ctx);
        }

        super.exitExpressionIdentifier(ctx);
    }

    @Override
    public void exitPrimary(PrimaryContext ctx) {
        super.exitPrimary(ctx);
    }

    @Override
    public void enterExpression(ExpressionContext ctx) {

        // Push an array on the value stack to hold any values.
        List<Object> array = new ArrayList<>();
        valueStack.push(array);

        super.enterExpression(ctx);
    }

    @Override
    public void exitExpression(ExpressionContext ctx) {

        // The expression value is the object represented by the expression.
        Object expressionValue = null;

        // Construct the expression based on what is on the stack.
        String major = ctx.expressionIdentifier().Int(0) != null ? ctx.expressionIdentifier().Int(0).getText() : null;
        String minor = ctx.expressionIdentifier().Int(1) != null ? ctx.expressionIdentifier().Int(1).getText() : null;
        IdentifierContext id = ctx.expressionIdentifier().identifier();
        try {
            int nid;
            if (major != null & minor != null) {
                short ma = Short.parseShort(major);
                short mi = Short.parseShort(minor);
                //MetaName name = MetaName.parseName(_library, id.getText());
                MetaVersion version = new MetaVersion(ma, mi);
                nid = _readMap.getStreamId(_library.getTypeId(id.getText(), version.toString()));
            } else {
                nid = _readMap.getStreamId(_library.getTypeId(id.getText()));
            }
            int defId = _readMap.getDefinitionId(nid);
            Class<?> c = _library.getClass(defId);
            if (c == null) {
                throw new ArgotParserException("type has no class bound: " + id.getText(), null, null, ctx);
            }

            // We expect that 
            Object v = valueStack.pop();
            if (!(v instanceof ArrayList<?>)) {
                throw new ArgotParserException("Array not found on value stack", null, null, ctx);
            }

            ArrayList<?> l = (ArrayList<?>) v;

            expressionValue = construct(defId, l.toArray(), c);

            // Pop the structure off the expression stack.
            _expressionStack.pop();

            if (!valueStack.isEmpty()) {
                Object topValueStack = valueStack.peek();
                if (!(topValueStack instanceof ArrayList<?>)) {
                    LOG.warning("List not found on top of stack for ");
                } else {
                    @SuppressWarnings("unchecked")
                    ArrayList<Object> topList = (ArrayList<Object>) topValueStack;
                    topList.add(expressionValue);
                }
            } else {
                // this is for a definition. so push it on the value stack.
                valueStack.push(expressionValue);
            }

        } catch (TypeException ex) {
            throw new ArgotParserException("Failed to create object for: " + id.getText() + "\n" + ex.getMessage(), null, null, ctx);
        }

        // If this is a library.entry then register this as a new type.
        if ("library.entry".equals(id.getText())) {
            try {
                LibraryEntry structure = (LibraryEntry) expressionValue;
                _compiler.registerLibraryType(_map, structure.getLocation(), structure.getDefinition());
            } catch (TypeException ex) {
                ex.printStackTrace();
                throw new ArgotParserException("Failed to register " + id.getText(), null, null, ctx);
            }
        }

        super.exitExpression(ctx);
    }

    @Override
    public void enterArray(ArrayContext ctx) {
        valueStack.push(new ArrayList<Object>());
        super.enterArray(ctx);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void exitArray(ArrayContext ctx) {

        Object top = valueStack.pop();
        if (valueStack.peek() instanceof List<?>) {

            List<Object> list = (List<Object>) valueStack.peek();
            list.add(((List<Object>) top).toArray());
        } else {
            throw new ArgotParserException("Failed to add primary to list: ", null, null, ctx);
        }
        super.exitArray(ctx);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void exitValue(ValueContext ctx) {

        String name = ctx.identifier().getText();
        String str = ctx.Int() != null ? ctx.Int().getText() : ctx.QSTRING().getText();

        // Remove the first and large " characters.
        if (ctx.QSTRING() != null) {
            str = str.substring(1, str.length() - 1);
        }

        try {
            ArgotPrimitiveParser parser = _compiler.getPrimitiveParser(name);
            if (parser == null) {
                // See if we can find the type that is defined.
                TypeElement element = _expressionStack.peek();
                if (element instanceof MetaSequence) {
                    MetaSequence seq = (MetaSequence) element;
                    for (int x = 0; x < seq.size(); x++) {
                        TypeElement ele = seq.getElement(x);
                        if (ele instanceof MetaTag) {
                            MetaTag tag = (MetaTag) ele;
                            if (tag.getDescription().equals(name) && tag.getExpression() instanceof MetaReference) {
                                MetaReference ref = (MetaReference) tag.getExpression();
                                parser = _compiler.getPrimitiveParser(_library.getName(ref.getType()).getFullName());
                                break;
                            }
                        }
                    }
                }

                if (parser == null) {
                    throw new ArgotParserException("No parser for :" + name, null, null, ctx);
                }
            }

            if (valueStack.peek() instanceof List) {
                ((List<Object>) valueStack.peek()).add(parser.parse(str));
            } else {
                throw new ArgotParserException("List not on stack:" + name, null, null, ctx);
            }

        } catch (TypeException ex) {
            ex.printStackTrace();
            throw new ArgotParserException("Unable to parse primitive:" + name, null, null, ctx);
        }

        super.exitValue(ctx);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void exitTypeIdentifier(TypeIdentifierContext ctx) {

        String argotType = ctx.identifier().getText();
        try {
            int id = _library.getTypeId(argotType);

            if (valueStack.peek() instanceof List) {
                ((List<Object>) valueStack.peek()).add(Integer.valueOf(id));
            } else {
                throw new ArgotParserException("List not on stack:" + argotType, null, null, ctx);
            }

        } catch (TypeException ex) {
            throw new ArgotParserException("No type found for #" + argotType, null, null, ctx);
        }
        super.exitTypeIdentifier(ctx);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void exitMappedIdentifier(MappedIdentifierContext ctx) {

        String argotType = ctx.identifier().getText();
        try {
            int id = _map.getStreamId(argotType);
            id = _map.getDefinitionId(id);

            if (valueStack.peek() instanceof List) {
                ((List<Object>) valueStack.peek()).add(Integer.valueOf(id));
            } else {
                throw new ArgotParserException("List not on stack:" + argotType, null, null, ctx);
            }

        } catch (TypeException ex) {
            throw new ArgotParserException("No type found for #" + argotType, null, null, ctx);
        }
        super.exitMappedIdentifier(ctx);
    }

}
