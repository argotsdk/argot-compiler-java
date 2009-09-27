/*
 * Copyright 2003-2009 (c) Live Media Pty Ltd. <argot@einet.com.au> 
 *
 * This software is licensed under the Argot Public License 
 * which may be found in the file LICENSE distributed 
 * with this software.
 *
 * More information about this license can be found at
 * http://www.einet.com.au/License
 * 
 * The Developer of this software is Live Media Pty Ltd,
 * PO Box 4591, Melbourne 3001, Australia.  The license is subject 
 * to the law of Victoria, Australia, and subject to exclusive 
 * jurisdiction of the Victorian courts.
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


file : (headers)? expression
	;


headers: headerline (headers)?
	{
	}
	;
	
headerline: importl | load | reserve | COMMENT
	{
	}
	;

reserve: '!'! 'reserve'^ IDENTIFIER ';'!
	;

load: '!'! 'load'^ QSTRING ';'!
	;

importl: '!'! 'import'^ IDENTIFIER ( 'as'! IDENTIFIER)? ';'!
	;
	
expression: '('^ IDENTIFIER (primary)* ')'!
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

IDENTIFIER:	('a'..'z'|'A'..'Z') ('a'..'z'|'A'..'Z'|'0'..'9'|'.'|'_'|'#')*;


