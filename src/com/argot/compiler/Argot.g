/*
 * Copyright (c) 2003-2013, Live Media Pty. Ltd.
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
grammar Argot;


options 
{
  output=AST;
  ASTLabelType=CommonTree;
}


@header 
{
package com.argot.compiler;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Vector;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.argot.TypeLibrary;
import com.argot.TypeMap;
import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.auto.TypeConstructorAuto;
import com.argot.meta.MetaDefinition;
import com.argot.meta.MetaExpression;
import com.argot.meta.MetaReference;
import com.argot.meta.MetaSequence;
import com.argot.meta.MetaArray;

import com.argot.dictionary.Dictionary;

import com.argot.compiler.primitive.ArgotPrimitiveParser;

}

@lexer::header {
package com.argot.compiler;
}


@members{

}


file : (headers)? (expression)?
	;


headers: headerline (headers)?
	{
	}
	;
	
headerline: importl | load | reserve | cluster | definition | relation | COMMENT
	{
	}
	;

cluster: 'cluster'^ IDENTIFIER ';'!
  ;

definition: 'definition'^ IDENTIFIER INT '.'! INT ':'! ( expression | sequence ) ';'!
  ;
  
relation: 'relation'^ IDENTIFIER INT '.'! INT QSTRING ':'! ( expression ) ';'!
  ;

sequence: '{'^ (tag)* '}'!
  ;

tag: '@'^ IDENTIFIER '#'! IDENTIFIER ';'!
  ;

reserve: '!'! 'reserve'^ IDENTIFIER ';'!
	;

load: '!'! 'load'^ QSTRING (','! IDENTIFIER )? ';'!
	;

importl: '!'! 'import'^ IDENTIFIER ( 'as'! IDENTIFIER)? ';'!
	;
	
expression: '('^ IDENTIFIER ('/'! INT '.'! INT )? (primary)* ')'!
	/*{ #expression = #([EXPR], id, p); }*/

	/*| LCBRACE (primary) RCBRACE*/
	;

primary: expression
	| INT^
	| QSTRING^
	| '['^ ( primary)*  ']'!
	| '#'^ IDENTIFIER
	| IDENTIFIER^ ':'! value
	| '$'^ IDENTIFIER
	;
	
value: INT^
	| QSTRING^
	;


WS: (' ' | '\t' | '\r'? '\n' )+ {$channel=HIDDEN;};

DQUOTE:	'"';
PIPE:	'|';
COMA:	',';
LBRACK:	'<';
RBRACK:	'>';
LBRACE:	'(';
RBRACE:	')';
LCBRACE: '{';
RCBRACE: '}';
ATSYM:	'@';
DOLLAR:	'$';
ASTERIX: '*';
UNDER:	'_';
HYPHEN:	'-';
PLUS: '+';
PERC:	'%';
HASH:	'#';
BANG:	'!';
COLON	:	':';
SEMI: ';';
SLBRACK	:	'[';
SRBRACK :	']';
FSLASH	:	'/';
PERIOD	:	'.';

/*DIGIT:	'0'..'9';*/

QSTRING:	'"'! ( options {greedy=false;} : . )* '"'!;

COMMENT
  : '/*' {if (input.LA(1)=='*') $type=COMMENT; else $channel=HIDDEN;} .* '*/';
 
INT	:	('0'..'9')+;

IDENTIFIER:	('a'..'z'|'A'..'Z') ('a'..'z'|'A'..'Z'|'0'..'9'|'.'|'_')*;


